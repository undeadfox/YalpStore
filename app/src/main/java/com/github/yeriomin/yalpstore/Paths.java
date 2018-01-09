package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;

public class Paths {

    static public final String FALLBACK_DIRECTORY = "Android/data/" + BuildConfig.APPLICATION_ID + "/files";

    static public File getStorageRoot(Context context) {
        File storageRoot = Environment.getExternalStorageDirectory();
        File[] externalFilesDirs = getExternalFilesDirs(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP
            || externalFilesDirs.length < 2
            || (Environment.isExternalStorageEmulated(storageRoot) && !Environment.isExternalStorageRemovable(storageRoot))
        ) {
            return storageRoot;
        }
        for (File file: externalFilesDirs) {
            if (Environment.isExternalStorageEmulated(file) && !Environment.isExternalStorageRemovable(file)) {
                return new File(file.getAbsolutePath().replace(FALLBACK_DIRECTORY, ""));
            }
        }
        return storageRoot;
    }

    static public File getYalpPath(Context context) {
        return new File(
            getStorageRoot(context),
            PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceActivity.PREFERENCE_DOWNLOAD_DIRECTORY, "")
        );
    }

    static public File getApkPath(Context context, String packageName, int version) {
        String filename = packageName + "." + String.valueOf(version) + ".apk";
        return new File(getYalpPath(context), filename);
    }

    static public File getDeltaPath(Context context, String packageName, int version) {
        return new File(getApkPath(context, packageName, version).getAbsolutePath() + ".delta");
    }

    static public File getObbPath(String packageName, int version, boolean main) {
        File obbDir = new File(new File(Environment.getExternalStorageDirectory(), "Android/obb"), packageName);
        String filename = (main ? "main" : "patch") + "." + String.valueOf(version) + "." + packageName + ".obb";
        return new File(obbDir, filename);
    }

    static private File[] getExternalFilesDirs(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
            ? context.getExternalFilesDirs(null)
            : new File[] {new File(Environment.getExternalStorageDirectory(), FALLBACK_DIRECTORY)}
        ;
    }
}
