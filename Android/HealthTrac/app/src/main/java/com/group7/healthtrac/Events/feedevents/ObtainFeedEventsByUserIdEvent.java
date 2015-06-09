package com.group7.healthtrac.events.feedevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 3/23/2015.
 */
public class ObtainFeedEventsByUserIdEvent implements IEvent {

    private String mUserId;
    private String mRequester;

    public ObtainFeedEventsByUserIdEvent(String userId, String requester) {
        mUserId = userId;
        mRequester = requester;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getRequester() {
        return mRequester;
    }
}
