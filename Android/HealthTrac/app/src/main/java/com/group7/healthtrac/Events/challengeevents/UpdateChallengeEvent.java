package com.group7.healthtrac.events.challengeevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Challenge;

public class UpdateChallengeEvent implements IEvent {

    private Challenge mChallenge;

    public UpdateChallengeEvent(Challenge challenge) {
        mChallenge = challenge;
    }

    public Challenge getChallenge() {
        return mChallenge;
    }
}
