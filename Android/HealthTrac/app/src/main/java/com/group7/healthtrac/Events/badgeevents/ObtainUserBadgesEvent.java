package com.group7.healthtrac.events.badgeevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 3/26/2015.
 */
public class ObtainUserBadgesEvent implements IEvent {

    private String mUserId;

    public ObtainUserBadgesEvent(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }
}
