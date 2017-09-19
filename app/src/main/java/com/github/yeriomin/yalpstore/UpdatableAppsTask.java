package com.github.yeriomin.yalpstore;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.github.yeriomin.yalpstore.model.App;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UpdatableAppsTask extends GoogleApiAsyncTask {

    protected List<App> updatableApps = new ArrayList<>();
    protected Map<String, App> installedApps = new HashMap<>();

    static public Map<String, App> getInstalledApps(Context context) {
        Map<String, App> apps = new HashMap<>();
        PackageManager pm = context.getPackageManager();
        for (PackageInfo reducedPackageInfo: pm.getInstalledPackages(0)) {
            App app = getInstalledApp(pm, reducedPackageInfo.packageName);
            if (null != app) {
                apps.put(app.getPackageName(), app);
            }
        }
        return apps;
    }

    static protected App getInstalledApp(PackageManager pm, String packageName) {
        try {
            App app = new App(pm.getPackageInfo(packageName, PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS));
            app.setDisplayName(pm.getApplicationLabel(app.getPackageInfo().applicationInfo).toString());
            return app;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    static public Map<String, App> filterSystemApps(Map<String, App> apps) {
        Map<String, App> result = new HashMap<>();
        for (App app: apps.values()) {
            if (!app.isSystem()) {
                result.put(app.getPackageName(), app);
            }
        }
        return result;
    }

    static public Map<String, App> filterBlacklistedApps(Context context, Map<String, App> apps) {
        Set<String> packageNames = new HashSet<>(apps.keySet());
        if (PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceActivity.PREFERENCE_UPDATE_LIST_WHITE_OR_BLACK, PreferenceActivity.LIST_BLACK).equals(PreferenceActivity.LIST_BLACK)) {
            packageNames.removeAll(new BlackWhiteListManager(context).get());
        } else {
            packageNames.retainAll(new BlackWhiteListManager(context).get());
        }
        Map<String, App> result = new HashMap<>();
        for (App app: apps.values()) {
            if (packageNames.contains(app.getPackageName())) {
                result.put(app.getPackageName(), app);
            }
        }
        return result;
    }

    @Override
    protected Throwable doInBackground(String... params) {
        // Building local apps list
        installedApps = getInstalledApps(context);
        if (!PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_SHOW_SYSTEM_APPS)) {
            installedApps = filterSystemApps(installedApps);
        }
        // Requesting info from Google Play Market for installed apps
        publishProgress();
        List<App> appsFromPlayStore = new ArrayList<>();
        try {
            appsFromPlayStore.addAll(getAppsFromPlayStore(filterBlacklistedApps(context, installedApps).keySet()));
        } catch (Throwable e) {
            return e;
        }
        // Comparing versions and building updatable apps list
        for (App appFromMarket: appsFromPlayStore) {
            String packageName = appFromMarket.getPackageName();
            if (TextUtils.isEmpty(packageName) || !installedApps.containsKey(packageName)) {
                continue;
            }
            App installedApp = installedApps.get(packageName);
            appFromMarket = addInstalledAppInfo(appFromMarket, installedApp);
            if (installedApp.getVersionCode() < appFromMarket.getVersionCode()) {
                installedApps.remove(packageName);
                updatableApps.add(appFromMarket);
            } else {
                installedApps.put(packageName, appFromMarket);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable result) {
        super.onPostExecute(result);
        Collections.sort(updatableApps);
    }

    @Override
    protected void processIOException(IOException e) {
        super.processIOException(e);
        if (noNetwork(e) && context instanceof Activity) {
            ContextUtil.toast(context, R.string.error_no_network);
        }
    }

    private App addInstalledAppInfo(App appFromMarket, App installedApp) {
        if (null != installedApp) {
            appFromMarket.setPackageInfo(installedApp.getPackageInfo());
            appFromMarket.setVersionName(installedApp.getVersionName());
            appFromMarket.setDisplayName(installedApp.getDisplayName());
            appFromMarket.setSystem(installedApp.isSystem());
            appFromMarket.setInstalled(true);
        }
        return appFromMarket;
    }

    protected List<App> getAppsFromPlayStore(Collection<String> packageNames) throws IOException {
        List<App> appsFromPlayStore = new ArrayList<>();
        boolean builtInAccount = PreferenceActivity.getBoolean(context, PreferenceActivity.PREFERENCE_APP_PROVIDED_EMAIL);
        for (App app: new PlayStoreApiWrapper(context).getDetails(new ArrayList<>(packageNames))) {
            if (!builtInAccount || app.isFree()) {
                appsFromPlayStore.add(app);
            }
        }
        return appsFromPlayStore;
    }
}
