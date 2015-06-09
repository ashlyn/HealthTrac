package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.group7.healthtrac.services.utilities.Utility;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Mike C on 4/6/2015.
 */
public class EndOfDayReport implements Parcelable {

    private final static String TAG = "EndOfDayReport";
    private int Id;
    private double TotalDuration;
    private int TotalSteps;
    private double TotalDistance;
    private Date Date;
    private String UserId;

    public EndOfDayReport(int id, double totalDuration, int totalSteps, double totalDistance, java.util.Date date, String userId) {
        Id = id;
        TotalDuration = totalDuration;
        TotalSteps = totalSteps;
        TotalDistance = totalDistance;
        Date = date;
        UserId = userId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public double getTotalDuration() {
        return TotalDuration;
    }

    public void setTotalDuration(double totalDuration) {
        TotalDuration = totalDuration;
    }

    public int getTotalSteps() {
        return TotalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        TotalSteps = totalSteps;
    }

    public double getTotalDistance() {
        return TotalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        TotalDistance = totalDistance;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
        Date = date;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeDouble(TotalDuration);
        dest.writeInt(TotalSteps);
        dest.writeDouble(TotalDistance);
        dest.writeString(Date.toString());
        dest.writeString(UserId);
    }

    public static final Creator<EndOfDayReport> CREATOR = new Creator<EndOfDayReport>() {
        @Override
        public EndOfDayReport createFromParcel(Parcel source) {
            return new EndOfDayReport(source);
        }

        @Override
        public EndOfDayReport[] newArray(int size) {
            return new EndOfDayReport[size];
        }
    };

    private EndOfDayReport(Parcel in) {
        Id = in.readInt();
        TotalDuration = in.readDouble();
        TotalSteps = in.readInt();
        TotalDistance = in.readDouble();
        try {
            Date = Utility.parseDateFromDisplayToUtc(in.readString());
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        UserId = in.readString();
    }
}
