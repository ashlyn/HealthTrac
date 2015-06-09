package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 2/19/2015.
 */
public class ObtainUserGroupsEvent implements IEvent {

    private String mUserId;

    public ObtainUserGroupsEvent(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }
}
