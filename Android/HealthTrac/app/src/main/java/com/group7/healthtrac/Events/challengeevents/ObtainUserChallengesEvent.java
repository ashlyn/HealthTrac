package com.group7.healthtrac.events.challengeevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Courtney on 4/26/2015.
 */
public class ObtainUserChallengesEvent implements IEvent {

    private String mUserId;

    public ObtainUserChallengesEvent(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getUserId() {
        return mUserId;
    }
}
