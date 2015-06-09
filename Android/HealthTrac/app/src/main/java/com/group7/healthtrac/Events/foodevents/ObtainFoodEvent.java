package com.group7.healthtrac.events.foodevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 3/31/2015.
 */
public class ObtainFoodEvent implements IEvent {

    private int mFoodId;
    private String mRequester;

    public ObtainFoodEvent(int foodId, String requester) {
        mFoodId = foodId;
        mRequester = requester;
    }

    public int getFoodId() {
        return mFoodId;
    }

    public String getRequester() {
        return mRequester;
    }
}
