package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Mike C on 3/8/2015.
 */
public class GeoPoint implements Parcelable {
    private double Latitude;
    private double Longitude;
    private int ActivityId;

    public GeoPoint(double latitude, double longitude) {
        this.Latitude = latitude;
        this.Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("(" + this.Latitude + ", " + this.Longitude + ")");
    }

    public LatLng convertToLatLng() {
        return new LatLng(Latitude, Longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(Latitude);
        dest.writeDouble(Longitude);
        dest.writeInt(ActivityId);
    }

    public static final Creator<GeoPoint> CREATOR = new Creator<GeoPoint>() {
        @Override
        public GeoPoint createFromParcel(Parcel source) {
            return new GeoPoint(source);
        }

        @Override
        public GeoPoint[] newArray(int size) {
            return new GeoPoint[size];
        }
    };

    private GeoPoint(Parcel in) {
        Latitude = in.readDouble();
        Longitude = in.readDouble();
        ActivityId = in.readInt();
    }
}
