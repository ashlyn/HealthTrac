package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mike C on 2/19/2015.
 */
public class Membership implements Parcelable {

    public static final int MEMBER = 0;
    public static final int ADMIN = 1;
    public static final int LEFT = 2;
    public static final int BANNED = 3;
    public static final int INVITED = 4;

    private int Status;
    private int Id;
    private String UserId;
    private int GroupId;

    public Membership(int groupId, String userId, int status) {
        UserId = userId;
        GroupId = groupId;
        Status = status;
    }

    public Membership(int groupId, String userId, int status, int id) {
        GroupId = groupId;
        UserId = userId;
        Status = status;
        Id = id;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
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

    public int getGroupId() {
        return GroupId;
    }

    public void setGroupId(int groupId) {
        GroupId = groupId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Status);
        dest.writeInt(Id);
        dest.writeInt(GroupId);
        dest.writeString(UserId);
    }

    public static final Creator<Membership> CREATOR = new Creator<Membership>() {
        @Override
        public Membership createFromParcel(Parcel source) {
            return new Membership(source);
        }

        @Override
        public Membership[] newArray(int size) {
            return new Membership[size];
        }
    };

    private Membership(Parcel in) {
        Status = in.readInt();
        Id = in.readInt();
        GroupId = in.readInt();
        UserId = in.readString();
    }
}
