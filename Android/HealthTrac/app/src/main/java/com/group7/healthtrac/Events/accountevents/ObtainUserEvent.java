package com.group7.healthtrac.events.accountevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 2/19/2015.
 */
public class ObtainUserEvent implements IEvent {

    private String mUserId;

    public ObtainUserEvent(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }
}
