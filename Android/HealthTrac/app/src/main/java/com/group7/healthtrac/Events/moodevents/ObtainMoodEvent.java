package com.group7.healthtrac.events.moodevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 4/1/2015.
 */
public class ObtainMoodEvent implements IEvent {

    private int mMoodId;
    private String mRequester;

    public ObtainMoodEvent(int moodId, String requester) {
        mMoodId = moodId;
        mRequester = requester;
    }

    public int getMoodId() {
        return mMoodId;
    }

    public String getRequester() {
        return mRequester;
    }
}
