package com.group7.healthtrac.events.activityevents;

import com.group7.healthtrac.events.IEvent;

import java.util.Date;

/**
 * Created by Mike C on 4/9/2015.
 */
public class ObtainDaysActivitiesEvent implements IEvent {

    private String mUserId;
    private Date mDate;

    public ObtainDaysActivitiesEvent(String userId, Date date) {
        mUserId = userId;
        mDate = date;
    }

    public String getUserId() {
        return mUserId;
    }

    public Date getDate() {
        return mDate;
    }
}
