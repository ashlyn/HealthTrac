package com.group7.healthtrac.events.goalevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Goal;

import java.util.List;

/**
 * Created by Mike C on 4/7/2015.
 */
public class UserGoalsObtainedEvent implements IEvent {

    private List<Goal> mGoals;

    public UserGoalsObtainedEvent(List<Goal> goals) {
        mGoals = goals;
    }

    public List<Goal> getGoals() {
        return mGoals;
    }
}
