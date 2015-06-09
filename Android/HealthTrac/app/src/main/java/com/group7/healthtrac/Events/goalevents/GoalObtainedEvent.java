package com.group7.healthtrac.events.goalevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Goal;

/**
 * Created by Mike C on 3/31/2015.
 */
public class GoalObtainedEvent implements IEvent {

    private Goal mGoal;
    private String mRequester;

    public GoalObtainedEvent(Goal goal, String requester) {
        mGoal = goal;
        mRequester = requester;
    }

    public Goal getGoal() {
        return mGoal;
    }

    public String getRequester() {
        return mRequester;
    }
}
