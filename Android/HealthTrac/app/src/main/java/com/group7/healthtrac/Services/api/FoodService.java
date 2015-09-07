package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.foodevents.CreateFoodEvent;
import com.group7.healthtrac.events.foodevents.FoodCreatedEvent;
import com.group7.healthtrac.events.foodevents.FoodObtainedEvent;
import com.group7.healthtrac.events.foodevents.ObtainFoodEvent;
import com.group7.healthtrac.models.Food;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Mike C on 3/31/2015.
 */
public class FoodService {

    private final static String TAG = "FoodService";
    private HealthTracApi mApi;
    private Bus mBus;
    private ObtainFoodEvent mObtainFoodEvent;

    public FoodService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onObtainFoodEvent(ObtainFoodEvent event) {
        mObtainFoodEvent = event;
        mApi.getFood(event.getFoodId(), new Callback<Food>() {
            @Override
            public void success(Food food, Response response) {
                mBus.post(new FoodObtainedEvent(food, mObtainFoodEvent.getRequester()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain food eaten.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onCreateFoodEvent(CreateFoodEvent event) {
        mBus.post(new FoodCreatedEvent(event.getFood()));
//        mApi.createFood(event.getFood(), new Callback<Food>() {
//            @Override
//            public void success(Food food, Response response) {
//                mBus.post(new FoodCreatedEvent(food));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not add food eaten.", ApiErrorEvent.Cause.CREATE));
//            }
//        });
    }
}
