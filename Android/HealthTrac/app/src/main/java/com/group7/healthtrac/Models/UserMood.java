package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.group7.healthtrac.services.utilities.Utility;

import java.text.ParseException;
import java.util.Date;

public class UserMood implements Parcelable {

    private final static String TAG = "UserMood";
    private Date Time;
    private int MoodId;
    private String UserId;
    private int Id;

    public UserMood(String UserId, int MoodId, Date Time) {
        this.UserId = UserId;
        this.MoodId = MoodId;
        this.Time = Time;
    }

    public UserMood(String UserId, int MoodId, Date Time, int Id) {
        this.UserId = UserId;
        this.MoodId = MoodId;
        this.Id = Id;
        this.Time = Time;
    }

    public Date getTime() {
        return Time;
    }

    public void setTime(Date time) {
        Time = time;
    }

    public int getMoodId() {
        return MoodId;
    }

    public void setMoodId(int moodId) {
        MoodId = moodId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeInt(MoodId);
        dest.writeString(UserId);
        dest.writeString(Time.toString());
    }

    public static final Creator<UserMood> CREATOR = new Creator<UserMood>() {
        @Override
        public UserMood createFromParcel(Parcel source) {
            return new UserMood(source);
        }

        @Override
        public UserMood[] newArray(int size) {
            return new UserMood[size];
        }
    };

    private UserMood(Parcel in) {
        Id = in.readInt();
        MoodId = in.readInt();
        UserId = in.readString();
        try {
            Time = Utility.parseDateFromUtcToDisplay(in.readString());
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
