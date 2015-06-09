package com.group7.healthtrac.events.foodevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Food;

/**
 * Created by Mike C on 3/31/2015.
 */
public class CreateFoodEvent implements IEvent {

    private Food mFood;

    public CreateFoodEvent(Food food) {
        mFood = food;
    }

    public Food getFood() {
        return mFood;
    }
}
