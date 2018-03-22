package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapManager {

    static public final long VALID_MILLIS = 1000*60*60*24*7;
    static private LruCache<String, Bitmap> memoryCache;

    private File baseDir;
    private boolean noImages;

    static {
        final int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024 / 8);
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };
    }

    public BitmapManager(Context context) {
        baseDir = context.getCacheDir();
        noImages = PreferenceUtil.getBoolean(context, PreferenceUtil.PREFERENCE_NO_IMAGES) && NetworkState.isMetered(context);
    }

    public Bitmap getBitmap(String url, boolean fullSize) {
        Bitmap bitmap = memoryCache.get(url);
        if (null != bitmap) {
            return bitmap;
        }
        File onDisk = getFile(url);
        if (isStoredAndValid(onDisk)) {
            bitmap = getCachedBitmapFromDisk(onDisk);
            cacheBitmapInMemory(url, bitmap);
            return bitmap;
        }
        if (noImages) {
            return null;
        }
        bitmap = downloadBitmap(url, fullSize);
        if (null != bitmap) {
            cacheBitmapOnDisk(bitmap, onDisk);
            cacheBitmapInMemory(url, bitmap);
        }
        return bitmap;
    }

    public File downloadAndGetFile(String url) {
        File onDisk = getFile(url);
        if (isStoredAndValid(onDisk)) {
            return onDisk;
        }
        Bitmap bitmap = downloadBitmap(url, true);
        if (null != bitmap) {
            cacheBitmapOnDisk(bitmap, onDisk);
            return onDisk;
        }
        return null;
    }

    private File getFile(String urlString) {
        return new File(baseDir, String.valueOf(urlString.hashCode()) + ".png");
    }

    static private boolean isStoredAndValid(File cached) {
        return cached.exists()
            && cached.lastModified() + VALID_MILLIS > System.currentTimeMillis()
            && cached.length() > 0
        ;
    }

    static private Bitmap getCachedBitmapFromDisk(File cached) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inDither = false;
            return BitmapFactory.decodeStream(new FileInputStream(cached), null, options);
        } catch (IOException e) {
            Log.e(BitmapManager.class.getSimpleName(), "Could not get cached bitmap: " + e.getClass().getName() + " " + e.getMessage());
            return null;
        }
    }

    static private void cacheBitmapOnDisk(Bitmap bitmap, File cached) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(cached);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeSilently(out);
        }
    }

    static private void cacheBitmapInMemory(String url, Bitmap bitmap) {
        if (null != url && null != bitmap) {
            memoryCache.put(url, bitmap);
        }
    }

    static private Bitmap downloadBitmap(String url, boolean fullSize) {
        InputStream input = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            connection.setConnectTimeout(3000);
            input = connection.getInputStream();

            BitmapFactory.Options options = new BitmapFactory.Options();
            if (!fullSize) {
                options.inSampleSize = 4;
            }
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(input, null, options);
        } catch (IOException e) {
            Log.e(BitmapManager.class.getSimpleName(), "Could not get icon from " + url + " " + e.getMessage());
        } finally {
            Util.closeSilently(input);
        }
        return null;
    }
}
