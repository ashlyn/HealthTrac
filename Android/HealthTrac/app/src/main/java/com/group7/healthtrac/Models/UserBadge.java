package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserBadge implements Parcelable {

    private int Id;
    private String UserId;
    private int BadgeId;

    public UserBadge(int id, String userId, int badgeId) {
        Id = id;
        UserId = userId;
        BadgeId = badgeId;
    }

    public UserBadge(int badgeId, String userId) {
        BadgeId = badgeId;
        UserId = userId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public int getBadgeId() {
        return BadgeId;
    }

    public void setBadgeId(int badgeId) {
        BadgeId = badgeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(UserId);
        dest.writeInt(BadgeId);
    }

    public static final Creator<UserBadge> CREATOR = new Creator<UserBadge>() {
        @Override
        public UserBadge createFromParcel(Parcel source) {
            return new UserBadge(source);
        }

        @Override
        public UserBadge[] newArray(int size) {
            return new UserBadge[size];
        }
    };

    private UserBadge(Parcel in) {
        Id = in.readInt();
        UserId = in.readString();
        BadgeId = in.readInt();
    }
}
