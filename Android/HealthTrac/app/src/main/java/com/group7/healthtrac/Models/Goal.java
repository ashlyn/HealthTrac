package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.group7.healthtrac.services.utilities.Utility;

import java.text.ParseException;
import java.util.Date;

public class Goal implements Parcelable {

    private static final String TAG = "Goal";

    public enum GoalType {
        Duration(0),
        Distance(1),
        Steps(2);

        private int value;

        GoalType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static GoalType fromInt(int i) {
            for (GoalType t : GoalType.values()) {
                if (t.getValue() == i) return t;
            }

            return null;
        }
    }

    public enum GoalTimeFrame {
        Daily(0),
        Weekly(1),
        Monthly(2),
        Yearly(3);

        private int value;

        GoalTimeFrame(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static GoalTimeFrame fromInt(int i) {
            for (GoalTimeFrame t : GoalTimeFrame.values()) {
                if (t.getValue() == i) return t;
            }

            return null;
        }
    }

    private int Id;
    private int Type;
    private int TimeFrame;
    private Date SetDate;
    private boolean Completed;
    private double Progress;
    private double Target;
    private String UserId;

    public Goal(int id, int type, int timeFrame, Date setDate, boolean completed, double progress, double target, String userId) {
        Id = id;
        Type = type;
        TimeFrame = timeFrame;
        SetDate = setDate;
        Completed = completed;
        Progress = progress;
        Target = target;
        UserId = userId;
    }

    public Goal(int type, int timeFrame, Date setDate, boolean completed, double progress, double target, String userId) {
        Type = type;
        TimeFrame = timeFrame;
        SetDate = setDate;
        Completed = completed;
        Progress = progress;
        Target = target;
        UserId = userId;
    }

    public Goal(Goal goal) {
        this.Id = 0;
        this.TimeFrame = goal.getTimeFrame();
        this.Target = goal.getTarget();
        this.Type = goal.getType();
        this.SetDate = new Date();
        this.Completed = false;
        this.Progress = 0;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getTimeFrame() {
        return TimeFrame;
    }

    public void setTimeFrame(int timeFrame) {
        TimeFrame = timeFrame;
    }

    public Date getSetDate() {
        return SetDate;
    }

    public void setSetDate(Date setDate) {
        SetDate = setDate;
    }

    public boolean isCompleted() {
        return Completed;
    }

    public void setCompleted(boolean completed) {
        Completed = completed;
    }

    public double getProgress() {
        return Progress;
    }

    public void setProgress(double progress) {
        Progress = progress;
    }

    public double getTarget() {
        return Target;
    }

    public void setTarget(double target) {
        Target = target;
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
        dest.writeInt(Type);
        dest.writeInt(TimeFrame);
        dest.writeString(Utility.displayDate(SetDate));
        dest.writeByte((byte)(Completed ? 1 : 0));
        dest.writeDouble(Progress);
        dest.writeDouble(Target);
        dest.writeString(UserId);
    }

    public static final Creator<Goal> CREATOR = new Creator<Goal>() {
        @Override
        public Goal createFromParcel(Parcel source) {
            return new Goal(source);
        }

        @Override
        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };

    private Goal(Parcel in) {
        Id = in.readInt();
        Type = in.readInt();
        TimeFrame = in.readInt();
        try {
            SetDate = Utility.parseDateFromDisplayToUtc(in.readString());
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }
        Completed = in.readByte() != 0;
        Progress = in.readDouble();
        Target = in.readDouble();
        UserId = in.readString();
    }
}
