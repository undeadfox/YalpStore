package com.github.yeriomin.yalpstore.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {

    private int rating;
    private String title;
    private String comment;

    private String userName;
    private String userPhotoUrl;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public Review() { }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(rating);
        out.writeString(title);
        out.writeString(comment);
        out.writeString(userName);
        out.writeString(userPhotoUrl);
    }

    protected Review(Parcel in) {
        rating = in.readInt();
        title = in.readString();
        comment = in.readString();
        userName = in.readString();
        userPhotoUrl = in.readString();
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
