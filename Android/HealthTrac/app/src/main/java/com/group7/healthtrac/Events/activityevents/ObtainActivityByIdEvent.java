package com.group7.healthtrac.events.activityevents;

import com.group7.healthtrac.events.IEvent;

public class ObtainActivityByIdEvent implements IEvent {

    private int mActivityId;
    private String mRequester;

    public ObtainActivityByIdEvent(int activityId, String requester) {
        mActivityId = activityId;
        mRequester = requester;
    }

    public int getActivityId() {
        return mActivityId;
    }

    public String getRequester() {
        return mRequester;
    }
}
