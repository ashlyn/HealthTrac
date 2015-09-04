package com.group7.healthtrac.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.group7.healthtrac.services.utilities.Utility;

import java.text.ParseException;
import java.util.Date;

public class FeedEvent implements Parcelable {

    public enum EventType {
        Badge(0),
        GroupJoin(1),
        GroupLeave(2),
        Activity(3),
        Food(4),
        GoalSet(5),
        GoalAchieved(6),
        Mood(7),
        EndOfDay(8);

        private int value;

        EventType(int value) {
            this.value = value;
        }
        public static EventType fromInt(int i) {
            for (EventType t : FeedEvent.EventType.values()) {
                if (t.getValue() == i) return t;
            }

            return null;
        }

        public int getValue() {
            return value;
        }
    }

    //private EventType Type;
    private int Type;
    private int Id;
    private int EventId;
    private String UserId;
    private Date Date;
    private String Description;
    private User User;

    public FeedEvent(int type, int id, int eventId, String userId, Date date) {
        //this.Type = EventType.fromInt(type);
        Type = type;
        this.Id = id;
        this.EventId = eventId;
        this.UserId = userId;
        this.Date = date;
    }

    public FeedEvent(int type, int id, int eventId, String userId, java.util.Date date, String description, com.group7.healthtrac.models.User user) {
        Type = type;
        Id = id;
        EventId = eventId;
        UserId = userId;
        Date = date;
        Description = description;
        User = user;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getEventId() {
        return EventId;
    }

    public void setEventId(int eventId) {
        EventId = eventId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Date getDate() {
        return Date;
    }

    public void setDate(Date date) {
        Date = date;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDescription() {
        return Description;
    }

    public void setUser(User user) {
        User = user;
    }

    public User getUser() {
        return User;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Type);
        dest.writeInt(Id);
        dest.writeInt(EventId);
        dest.writeString(UserId);
        dest.writeString(Date.toString());
        dest.writeParcelable(User, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
    }

    public static final Creator<FeedEvent> CREATOR = new Creator<FeedEvent>() {
        @Override
        public FeedEvent createFromParcel(Parcel source) {
            return new FeedEvent(source);
        }

        @Override
        public FeedEvent[] newArray(int size) {
            return new FeedEvent[size];
        }
    };

    private FeedEvent(Parcel in) {
        //this.Type = EventType.fromInt(in.readInt());
        Type = in.readInt();
        this.Id = in.readInt();
        this.EventId = in.readInt();
        this.UserId = in.readString();
        try {
            this.Date = Utility.parseDateFromJSONToDisplay(in.readString());
        } catch (ParseException e) {
            Log.e("FeedEventCreation", e.getMessage());
        }
        User = in.readParcelable(User.class.getClassLoader());
    }
}
