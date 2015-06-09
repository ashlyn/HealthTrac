package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mike C on 2/19/2015.
 */
public class Badge implements Parcelable {

    private int Id;
    private String Name;
    private String Description;
    private String ImageUrl;

    public Badge(int id, String name, String description, String imageUrl) {
        Id = id;
        Name = name;
        Description = description;
        ImageUrl = imageUrl;
    }

    public Badge(String name, String description) {
        Name = name;
        Description = description;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
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
        dest.writeString(Name);
        dest.writeString(Description);
        dest.writeInt(Id);
        dest.writeString(ImageUrl);
    }

    public static final Creator<Badge> CREATOR = new Creator<Badge>() {
        @Override
        public Badge createFromParcel(Parcel source) {
            return new Badge(source);
        }

        @Override
        public Badge[] newArray(int size) {
            return new Badge[size];
        }
    };

    private Badge(Parcel in) {
        Name = in.readString();
        Description = in.readString();
        Id = in.readInt();
       ImageUrl = in.readString();
    }
}
