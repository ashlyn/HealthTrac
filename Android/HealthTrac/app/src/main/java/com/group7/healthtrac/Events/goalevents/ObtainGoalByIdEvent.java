package com.group7.healthtrac.events.goalevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 3/31/2015.
 */
public class ObtainGoalByIdEvent implements IEvent {

    private String mRequester;
    private int mGoalId;

    public ObtainGoalByIdEvent(int goalId, String requester) {
        this.mRequester = requester;
        this.mGoalId = goalId;
    }

    public String getRequester() {
        return mRequester;
    }

    public int getGoalId() {
        return mGoalId;
    }
}
