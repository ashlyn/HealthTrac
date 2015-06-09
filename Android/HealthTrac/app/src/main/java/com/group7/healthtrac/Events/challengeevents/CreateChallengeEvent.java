package com.group7.healthtrac.events.challengeevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Challenge;

/**
 * Created by Courtney on 4/26/2015.
 */
public class CreateChallengeEvent implements IEvent {
    private Challenge mChallenge;

    public CreateChallengeEvent(Challenge challenge) {
        mChallenge = challenge;
    }

    public Challenge getChallenge() {
        return mChallenge;
    }
}
