package com.group7.healthtrac.events.moodevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.UserMood;

/**
 * Created by Mike C on 3/25/2015.
 */
public class UpdateMoodEvent implements IEvent {

    private UserMood mUserMood;

    public UpdateMoodEvent(UserMood userMood) {
        mUserMood = userMood;
    }

    public UserMood getUserMood() {
        return mUserMood;
    }
}
