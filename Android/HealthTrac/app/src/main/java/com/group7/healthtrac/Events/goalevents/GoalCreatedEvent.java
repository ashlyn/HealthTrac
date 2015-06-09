package com.group7.healthtrac.events.goalevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Goal;

/**
 * Created by Mike C on 3/26/2015.
 */
public class GoalCreatedEvent implements IEvent {

    private Goal mGoal;

    public GoalCreatedEvent(Goal goal) {
        mGoal = goal;
    }

    public Goal getGoal() {
        return mGoal;
    }
}
