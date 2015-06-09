package com.group7.healthtrac.events.accountevents;

import com.group7.healthtrac.events.IEvent;

public class ObtainUserFriendsEvent implements IEvent {

    private String mUserId;

    public ObtainUserFriendsEvent(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }
}
