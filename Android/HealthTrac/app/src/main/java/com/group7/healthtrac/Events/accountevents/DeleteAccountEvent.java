package com.group7.healthtrac.events.accountevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 3/30/2015.
 */
public class DeleteAccountEvent implements IEvent {

    private String mUserId;

    public DeleteAccountEvent(String userId) {
        mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }
}
