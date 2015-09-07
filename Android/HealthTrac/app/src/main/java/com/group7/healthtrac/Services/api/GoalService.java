package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.goalevents.CreateGoalEvent;
import com.group7.healthtrac.events.goalevents.GoalCreatedEvent;
import com.group7.healthtrac.events.goalevents.GoalObtainedEvent;
import com.group7.healthtrac.events.goalevents.ObtainGoalByIdEvent;
import com.group7.healthtrac.events.goalevents.ObtainUserGoalsEvent;
import com.group7.healthtrac.events.goalevents.UserGoalsObtainedEvent;
import com.group7.healthtrac.models.Goal;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GoalService {

    private final static String TAG = "GoalService";
    private HealthTracApi mApi;
    private Bus mBus;
    private ObtainGoalByIdEvent mObtainGoalByIdEvent;

    public GoalService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onCreateGoal(CreateGoalEvent event) {
        mBus.post(new GoalCreatedEvent(event.getGoal()));
//        mApi.createGoal(event.getGoal(), new Callback<Goal>() {
//            @Override
//            public void success(Goal goal, Response response) {
//                mBus.post(new GoalCreatedEvent(goal));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not add goal", ApiErrorEvent.Cause.CREATE));
//            }
//        });
    }

    @Subscribe
    public void onObtainGoalByIdEvent(ObtainGoalByIdEvent event) {
        mObtainGoalByIdEvent = event;
        mApi.getGoal(event.getGoalId(), new Callback<Goal>() {
            @Override
            public void success(Goal goal, Response response) {
                mBus.post(new GoalObtainedEvent(goal, mObtainGoalByIdEvent.getRequester()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain goal information", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onGetUserGoals(ObtainUserGoalsEvent event) {
        List<Goal> goals = new ArrayList<>();
        goals.add(new Goal(0, 0, new Date(), false, .75, 10000, "test"));
        mBus.post(new UserGoalsObtainedEvent(goals));
//       mApi.getUserGoals(event.getUserId(), new Callback<List<Goal>>() {
//            @Override
//            public void success(List<Goal> goals, Response response) {
//                mBus.post(new UserGoalsObtainedEvent(goals));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not obtain user's goals", ApiErrorEvent.Cause.OBTAIN));
//            }
//        });
    }
}
