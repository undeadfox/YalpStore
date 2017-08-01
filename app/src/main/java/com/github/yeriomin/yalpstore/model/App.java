package com.github.yeriomin.yalpstore.model;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App implements Comparable<App> {

    private PackageInfo packageInfo;

    private String displayName;
    private String versionName;
    private int versionCode;
    private int offerType;
    private String updated;
    private long size;
    private String installs;
    private Rating rating = new Rating();
    private String iconUrl;
    private String videoUrl;
    private String changes;
    private String developerName;
    private String description;
    private Set<String> permissions = new HashSet<>();
    private boolean isInstalled;
    private boolean isFree;
    private List<String> screenshotUrls = new ArrayList<>();
    private Review userReview;
    private String categoryId;
    private String price;
    private boolean containsAds;
    private Set<String> dependencies = new HashSet<>();
    private Map<String, String> offerDetails = new HashMap<>();
    private boolean system;
    private boolean inPlayStore;
    private Map<String, String> relatedLinks = new HashMap<>();

    public App() {
        this.packageInfo = new PackageInfo();
    }

    public App(PackageInfo packageInfo) {
        this.setPackageInfo(packageInfo);
        this.setVersionName(packageInfo.versionName);
        this.setVersionCode(packageInfo.versionCode);
        this.setInstalled(true);
        this.setSystem((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        if (null != packageInfo.requestedPermissions) {
            this.setPermissions(Arrays.asList(packageInfo.requestedPermissions));
        }
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public String getPackageName() {
        return packageInfo.packageName;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getOfferType() {
        return offerType;
    }

    public void setOfferType(int offerType) {
        this.offerType = offerType;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getInstalls() {
        return installs;
    }

    public void setInstalls(String installs) {
        this.installs = installs;
    }

    public Rating getRating() {
        return rating;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public ImageSource getIconInfo() {
        ImageSource imageSource = new ImageSource();
        if (null != packageInfo && null != packageInfo.applicationInfo) {
            imageSource.setApplicationInfo(packageInfo.applicationInfo);
        }
        if (!TextUtils.isEmpty(iconUrl)) {
            imageSource.setUrl(iconUrl);
        }
        return imageSource;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getChanges() {
        return changes;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<String> permissions) {
        this.permissions = new HashSet<>(permissions);
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean installed) {
        isInstalled = installed;
    }

    public int getInstalledVersionCode() {
        if (null != packageInfo) {
            return packageInfo.versionCode;
        }
        return 0;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public List<String> getScreenshotUrls() {
        return screenshotUrls;
    }

    public Review getUserReview() {
        return userReview;
    }

    public void setUserReview(Review userReview) {
        this.userReview = userReview;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean containsAds() {
        return containsAds;
    }

    public void setContainsAds(boolean containsAds) {
        this.containsAds = containsAds;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public Map<String, String> getOfferDetails() {
        return offerDetails;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public boolean isInPlayStore() {
        return inPlayStore;
    }

    public void setInPlayStore(boolean inPlayStore) {
        this.inPlayStore = inPlayStore;
    }

    public Map<String, String> getRelatedLinks() {
        return relatedLinks;
    }

    @Override
    public int compareTo(App o) {
        return getDisplayName().compareToIgnoreCase(o.getDisplayName());
    }
}
