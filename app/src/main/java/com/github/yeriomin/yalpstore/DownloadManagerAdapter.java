package com.github.yeriomin.yalpstore;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import com.github.yeriomin.playstoreapi.AndroidAppDeliveryData;
import com.github.yeriomin.yalpstore.model.App;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class DownloadManagerAdapter extends DownloadManagerAbstract {

    private DownloadManager dm;

    public DownloadManagerAdapter(Context context) {
        super(context);
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public long enqueue(App app, AndroidAppDeliveryData deliveryData, Type type, OnDownloadProgressListener listener) {
        DownloadManager.Request request;
        Log.i(getClass().getSimpleName(), "Downloading " + type.name() + " for " + app.getPackageName());
        switch (type) {
            case APK:
                request = new DownloadRequestBuilderApk(context, app, deliveryData).build();
                break;
            case DELTA:
                request = new DownloadRequestBuilderDelta(context, app, deliveryData).build();
                break;
            case OBB_MAIN:
                request = new DownloadRequestBuilderObb(context, app, deliveryData).setMain(true).build();
                break;
            case OBB_PATCH:
                request = new DownloadRequestBuilderObb(context, app, deliveryData).setMain(false).build();
                break;
            default:
                throw new RuntimeException("Unknown request type");
        }
        if (DownloadState.get(app.getPackageName()).getTriggeredBy().equals(DownloadState.TriggeredBy.SCHEDULED_UPDATE)
            && PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_BACKGROUND_UPDATE_WIFI_ONLY)
        ) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        }
        long downloadId = dm.enqueue(request);
        if (null != listener) {
            DownloadManagerProgressUpdater updater = new DownloadManagerProgressUpdater(downloadId, this, listener);
            updater.update();
        }
        return downloadId;
    }

    @Override
    public boolean finished(long downloadId) {
        return null != getCursor(downloadId);
    }

    @Override
    public boolean success(long downloadId) {
        Cursor cursor = getCursor(downloadId);
        if (null == cursor) {
            return false;
        }
        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
        cursor.close();
        return status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS;
    }

    @Override
    public String getError(long downloadId) {
        Cursor cursor = getCursor(downloadId);
        if (null == cursor) {
            return getErrorString(context, DownloadManagerInterface.ERROR_UNKNOWN);
        }
        int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
        cursor.close();
        return getErrorString(context, reason);
    }

    @Override
    public void cancel(long downloadId) {
        super.cancel(downloadId);
        dm.remove(downloadId);
    }

    public Pair<Integer, Integer> getProgress(long downloadId) {
        Cursor cursor = getCursor(downloadId);
        if (null == cursor) {
            return null;
        }
        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
        int total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        int complete = status == DownloadManager.STATUS_SUCCESSFUL || reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS
            ? total
            : cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
        ;
        cursor.close();
        return new Pair<>(complete, total);
    }

    private Cursor getCursor(long downloadId) {
        Cursor cursor = dm.query(new DownloadManager.Query().setFilterById(downloadId));
        if (null == cursor) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}
