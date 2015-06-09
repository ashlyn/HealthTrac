package com.group7.healthtrac.events.foodevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Food;

public class FoodObtainedEvent implements IEvent {

    private Food mFood;
    private String mRequester;

    public FoodObtainedEvent(Food food, String requester) {
        mFood = food;
        mRequester = requester;
    }

    public Food getFood() {
        return mFood;
    }

    public String getRequester() {
        return mRequester;
    }
}
