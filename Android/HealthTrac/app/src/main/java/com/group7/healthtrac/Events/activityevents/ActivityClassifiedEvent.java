package com.group7.healthtrac.events.activityevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 4/7/2015.
 */
public class ActivityClassifiedEvent implements IEvent {

    private int mActivityType;

    public ActivityClassifiedEvent(int activityType) {
        mActivityType = activityType;
    }

    public int getActivityType() {
        return mActivityType;
    }
}
