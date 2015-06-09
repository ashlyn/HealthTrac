package com.group7.healthtrac.events.membershipevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Josh on 4/2/2015.
 */
public class MembershipUpdatedEvent implements IEvent{
    private String mMessage;

    public MembershipUpdatedEvent(String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
