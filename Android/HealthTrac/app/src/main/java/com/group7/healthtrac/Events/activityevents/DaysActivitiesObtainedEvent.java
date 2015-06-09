package com.group7.healthtrac.events.activityevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Activity;

import java.util.List;

/**
 * Created by Mike C on 4/9/2015.
 */
public class DaysActivitiesObtainedEvent implements IEvent {

    private List<Activity> mDaysActivities;

    public DaysActivitiesObtainedEvent(List<Activity> daysActivities) {
        mDaysActivities = daysActivities;
    }

    public List<Activity> getDaysActivities() {
        return mDaysActivities;
    }
}
