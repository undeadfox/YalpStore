package com.github.yeriomin.yalpstore.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Rating implements Parcelable {

    private float average;
    private int[] stars = new int[5];

    public Rating() { }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public int getStars(int starNum) {
        return stars[starNum - 1];
    }

    public void setStars(int starNum, int ratings) {
        stars[starNum - 1] = ratings;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(average);
        out.writeIntArray(stars);
    }

    protected Rating(Parcel in) {
        average = in.readFloat();
        in.readIntArray(stars);
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Rating> CREATOR = new Parcelable.Creator<Rating>() {
        public Rating createFromParcel(Parcel in) {
            return new Rating(in);
        }

        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };
}
