package com.github.yeriomin.yalpstore.model;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App implements Comparable<App>, Parcelable {

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
    private String changes;
    private String developerName;
    private String description;
    private Set<String> permissions = new HashSet<>();
    private boolean isInstalled;
    private boolean isFree;
    private List<String> screenshotUrls = new ArrayList<>();
    private Review userReview;
    private List<App> similarApps = new ArrayList<>();
    private List<App> usersAlsoInstalledApps = new ArrayList<>();
    private String categoryId;
    private String price;
    private boolean containsAds;
    private Set<String> dependencies = new HashSet<>();
    private Map<String, String> offerDetails = new HashMap<>();
    private boolean system;
    private boolean inPlayStore;

    public App() {
        this.packageInfo = new PackageInfo();
    }

    public App(PackageInfo packageInfo) {
        this.setPackageInfo(packageInfo);
        this.setVersionName(packageInfo.versionName);
        this.setVersionCode(packageInfo.versionCode);
        this.setSystem((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        if (null != packageInfo.requestedPermissions) {
            this.setPermissions(Arrays.asList(packageInfo.requestedPermissions));
        }
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(packageInfo, flags);
        out.writeString(displayName);
        out.writeString(versionName);
        out.writeInt(versionCode);
        out.writeInt(offerType);
        out.writeString(updated);
        out.writeLong(size);
        out.writeString(installs);
        out.writeParcelable(rating, flags);
        out.writeString(iconUrl);
        out.writeString(changes);
        out.writeString(developerName);
        out.writeString(description);
        out.writeStringList(new ArrayList<>(permissions));
        out.writeInt(isInstalled ? 1 : 0);
        out.writeInt(isFree ? 1 : 0);
        out.writeStringList(screenshotUrls);
        out.writeParcelable(userReview, flags);
        out.writeParcelableArray((App[]) similarApps.toArray(), flags);
        out.writeParcelableArray((App[]) usersAlsoInstalledApps.toArray(), flags);
        out.writeString(categoryId);
        out.writeString(price);
        out.writeInt(containsAds ? 1 : 0);
        out.writeStringList(new ArrayList<>(dependencies));
        out.writeMap(offerDetails);
        out.writeInt(system ? 1 : 0);
        out.writeInt(inPlayStore ? 1 : 0);
    }

    protected App(Parcel in) {
        packageInfo = in.readParcelable(PackageInfo.class.getClassLoader());
        displayName = in.readString();
        versionName = in.readString();
        versionCode = in.readInt();
        offerType = in.readInt();
        updated = in.readString();
        size = in.readLong();
        installs = in.readString();
        rating = in.readParcelable(Rating.class.getClassLoader());
        iconUrl = in.readString();
        changes = in.readString();
        developerName = in.readString();
        description = in.readString();
        List<String> permissions = new ArrayList<>();
        in.readStringList(permissions);
        this.permissions.addAll(permissions);
        isInstalled = in.readInt() != 0;
        isFree = in.readInt() != 0;
        in.readStringList(screenshotUrls);
        userReview = in.readParcelable(Review.class.getClassLoader());
        similarApps = Arrays.asList((App[]) in.readParcelableArray(App.class.getClassLoader()));
        usersAlsoInstalledApps = Arrays.asList((App[]) in.readParcelableArray(App.class.getClassLoader()));
        categoryId = in.readString();
        price = in.readString();
        containsAds = in.readInt() != 0;
        List<String> dependencies = new ArrayList<>();
        in.readStringList(dependencies);
        this.dependencies.addAll(dependencies);
        offerDetails.putAll(in.readHashMap(HashMap.class.getClassLoader()));
        system = in.readInt() != 0;
        inPlayStore = in.readInt() != 0;
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<App> CREATOR = new Parcelable.Creator<App>() {
        public App createFromParcel(Parcel in) {
            return new App(in);
        }

        public App[] newArray(int size) {
            return new App[size];
        }
    };

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

    public IconInfo getIconInfo() {
        IconInfo iconInfo = new IconInfo();
        if (null != packageInfo && null != packageInfo.applicationInfo) {
            iconInfo.setApplicationInfo(packageInfo.applicationInfo);
        }
        if (!TextUtils.isEmpty(iconUrl)) {
            iconInfo.setUrl(iconUrl);
        }
        return iconInfo;
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

    public List<App> getSimilarApps() {
        return similarApps;
    }

    public List<App> getUsersAlsoInstalledApps() {
        return usersAlsoInstalledApps;
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

    @Override
    public int compareTo(App o) {
        return getDisplayName().compareToIgnoreCase(o.getDisplayName());
    }
}
