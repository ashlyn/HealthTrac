package com.group7.healthtrac.events.moodevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 3/30/2015.
 */
public class ObtainUserMoodEvent implements IEvent {

    private String mRequester;
    private int mUserMoodId;

    public ObtainUserMoodEvent(int userMoodId, String requester) {
        mRequester = requester;
        mUserMoodId = userMoodId;
    }

    public String getRequester() {
        return mRequester;
    }

    public int getUserMoodId() {
        return mUserMoodId;
    }
}
