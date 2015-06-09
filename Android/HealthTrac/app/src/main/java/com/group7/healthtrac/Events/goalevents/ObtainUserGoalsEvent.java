package com.group7.healthtrac.events.goalevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 4/7/2015.
 */
public class ObtainUserGoalsEvent implements IEvent {

    private String mUserId;

    public ObtainUserGoalsEvent(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }
}
