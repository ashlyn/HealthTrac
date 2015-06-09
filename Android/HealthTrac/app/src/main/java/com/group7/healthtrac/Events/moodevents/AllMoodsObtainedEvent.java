package com.group7.healthtrac.events.moodevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Mood;

import java.util.List;

/**
 * Created by Mike C on 4/1/2015.
 */
public class AllMoodsObtainedEvent implements IEvent {

    private List<Mood> mMoods;

    public AllMoodsObtainedEvent(List<Mood> moods) {
        mMoods = moods;
    }

    public List<Mood> getMoods() {
        return mMoods;
    }
}
