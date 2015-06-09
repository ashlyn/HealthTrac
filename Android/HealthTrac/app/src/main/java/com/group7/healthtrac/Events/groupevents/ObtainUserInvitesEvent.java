package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Courtney on 4/26/2015.
 */
public class ObtainUserInvitesEvent implements IEvent {

    private String mUserId;

    public ObtainUserInvitesEvent(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }
}
