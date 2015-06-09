package com.group7.healthtrac.events.accountevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 2/18/2015.
 */
public class ObtainUserByFacebookEvent implements IEvent {

    private String mUserId;

    public ObtainUserByFacebookEvent(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }
}
