package com.group7.healthtrac.events.moodevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Mood;

/**
 * Created by Mike C on 3/31/2015.
 */
public class UserMoodObtainedEvent implements IEvent {

    private String mRequester;
    private Mood mMood;

    public UserMoodObtainedEvent(Mood mood, String requester) {
        mRequester = requester;
        mMood = mood;
    }

    public String getRequester() {
        return mRequester;
    }

    public Mood getMood() {
        return mMood;
    }
}
