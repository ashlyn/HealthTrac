package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mike C on 4/1/2015.
 */
public class Mood implements Parcelable {

    private String Type;
    private int Id;
    private String ImageUrl;

    public Mood(String type, int id, String imageUrl) {
        Type = type;
        Id = id;
        ImageUrl = imageUrl;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ImageUrl);
        dest.writeInt(Id);
        dest.writeString(Type);
    }

    public static final Creator<Mood> CREATOR = new Creator<Mood>() {
        @Override
        public Mood createFromParcel(Parcel source) {
            return new Mood(source);
        }

        @Override
        public Mood[] newArray(int size) {
            return new Mood[size];
        }
    };

    private Mood(Parcel in) {
        ImageUrl = in.readString();
        Id = in.readInt();
        Type = in.readString();
    }
}
