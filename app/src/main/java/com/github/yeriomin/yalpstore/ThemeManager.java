package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

public class ThemeManager {

    static public void setTheme(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String theme = prefs.getString(PreferenceActivity.PREFERENCE_UI_THEME, PreferenceActivity.THEME_NONE);
        switch (theme) {
            default:
            case PreferenceActivity.THEME_NONE:
                if (isAmazonTv(activity)) {
                    activity.setTheme(getThemeDark());
                }
                break;
            case PreferenceActivity.THEME_LIGHT:
                activity.setTheme(getThemeLight());
                break;
            case PreferenceActivity.THEME_DARK:
                activity.setTheme(getThemeDark());
                break;
            case PreferenceActivity.THEME_BLACK:
                activity.setTheme(getThemeDark());
                activity.getWindow().setBackgroundDrawableResource(android.R.color.black);
                break;
        }
    }

    static private int getThemeLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return android.R.style.Theme_Material_Light;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return android.R.style.Theme_Holo_Light;
        } else {
            return android.R.style.Theme_Light;
        }
    }

    static private int getThemeDark() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return android.R.style.Theme_Material;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return android.R.style.Theme_Holo;
        } else {
            return android.R.style.Theme;
        }
    }

    static private boolean isAmazonTv(Activity activity) {
        return ((YalpStoreApplication) activity.getApplication()).isTv() && Build.MANUFACTURER.toLowerCase().contains("amazon");
    }
}
