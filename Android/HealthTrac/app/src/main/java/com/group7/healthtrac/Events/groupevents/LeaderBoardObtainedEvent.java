package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.services.utilities.Tuple;

import java.util.List;

/**
 * Created by Mike C on 4/4/2015.
 */
public class LeaderBoardObtainedEvent implements IEvent {

    private String mCategory;
    private List<Tuple> mLeaders;

    public LeaderBoardObtainedEvent(List<Tuple> leaders, String category) {
        mLeaders = leaders;
        mCategory = category;
    }

    public String getCategory() {
        return mCategory;
    }

    public List<Tuple> getLeaders() {
        return mLeaders;
    }
}
