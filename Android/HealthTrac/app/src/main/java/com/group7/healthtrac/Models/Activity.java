package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.group7.healthtrac.services.utilities.Utility;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mike C on 2/19/2015.
 */
public class Activity implements Parcelable {

    public enum ActivityType {
        Run(0),
        Bike(1),
        Jog(2),
        Walk(3),
        Other(4);

        private int value;

        ActivityType(int value) {
            this.value = value;
        }

        public static ActivityType fromString(String type) {
            ActivityType temp = Walk;

            switch(type) {
                case "Run":
                    temp = Run;
                    break;
                case "Walk":
                    temp = Walk;
                    break;
                case "Jog":
                    temp = Jog;
                    break;
                case "Bike":
                    temp = Bike;
                    break;
                case "Other":
                    temp = Other;
                    break;
            }

            return temp;
        }

        public int getValue() {
            return value;
        }

        public static ActivityType fromInt(int i) {
            for (ActivityType t : Activity.ActivityType.values()) {
                if (t.getValue() == i) return t;
            }

            return null;
        }
    }

    private int Id;
    private int Type;
    private double Duration;
    private String UserId;
    private String Name;
    private double Distance;
    private Date StartTime;
    private int Steps;
    private List<GeoPoint> RoutePoints;

    public Activity(int Id, int Type, double Duration, String UserId, String Name, Date StartTime, double Distance, int Steps, List<GeoPoint> RoutePoints) {
        this.Id = Id;
        //this.activityType = ActivityType.fromInt(Type);
        this.Type = Type;
        this.Duration = Duration;
        this.UserId = UserId;
        this.Name = Name;
        this.StartTime = StartTime;
        this.Distance = Distance;
        this.Steps = Steps;
        this.RoutePoints = RoutePoints;
    }

    public Activity(int type, double duration, String userId, String name, Date startTime, double distance, int steps, List<GeoPoint> routePoints) {
        //activityType = ActivityType.fromInt(type);
        Type = type;
        Duration = duration;
        UserId = userId;
        Name = name;
        StartTime = startTime;
        Distance = distance;
        Steps = steps;
        RoutePoints = routePoints;
    }

    public Activity(int type, double duration, String userId, String name, Date startTime, double distance, int steps) {
        //activityType = ActivityType.fromInt(type);
        Type = type;
        Duration = duration;
        UserId = userId;
        Name = name;
        StartTime = startTime;
        Distance = distance;
        Steps = steps;
        RoutePoints = new ArrayList<>();
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public int getSteps() {
        return Steps;
    }

    public void setSteps(int steps) {
        Steps = steps;
    }

    public List<GeoPoint> getRoutePoints() {
        return RoutePoints;
    }

    public void setRoutePoints(List<GeoPoint> routePoints) {
        RoutePoints = routePoints;
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

    public double getDuration() {
        return Duration;
    }

    public void setDuration(double duration) {
        Duration = duration;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Date getStartTime() {
        return StartTime;
    }

    public void setStartTime(Date startTime) {
        StartTime = startTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        //dest.writeInt(Type.getValue());
        dest.writeInt(Type);
        dest.writeString(UserId);
        dest.writeString(Name);
        dest.writeDouble(Distance);
        dest.writeDouble(Duration);
        dest.writeInt(Steps);
        dest.writeString(StartTime.toString());
        dest.writeList(RoutePoints);
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel source) {
            return new Activity(source);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    private Activity(Parcel in) {
        Id = in.readInt();
        //Type = ActivityType.fromInt(in.readInt());
        Type = in.readInt();
        //activityType = ActivityType.fromInt(Type);
        UserId = in.readString();
        Name = in.readString();
        Distance = in.readDouble();
        Duration = in.readDouble();
        Steps = in.readInt();
        RoutePoints = new ArrayList<>();
        try {
            StartTime = Utility.parseDateFromJSONToUtc(in.readString());
        } catch(ParseException e) {
            Log.e("ActivityParcel", e.getMessage());
        }
        in.readList(RoutePoints, GeoPoint.class.getClassLoader());
    }
}
