package com.group7.healthtrac.events.goalevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Goal;

/**
 * Created by Mike C on 3/26/2015.
 */
public class CreateGoalEvent implements IEvent {

    private Goal mGoal;

    public CreateGoalEvent(Goal goal) {
        mGoal = goal;
    }

    public Goal getGoal() {
        return mGoal;
    }
}
