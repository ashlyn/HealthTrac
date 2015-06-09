package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.activityevents.ActivityClassifiedEvent;
import com.group7.healthtrac.events.activityevents.ActivityCreatedEvent;
import com.group7.healthtrac.events.activityevents.ActivityObtainedEvent;
import com.group7.healthtrac.events.activityevents.ClassifyActivityEvent;
import com.group7.healthtrac.events.activityevents.CreateActivityEvent;
import com.group7.healthtrac.events.activityevents.DaysActivitiesObtainedEvent;
import com.group7.healthtrac.events.activityevents.ObtainActivityByIdEvent;
import com.group7.healthtrac.events.activityevents.ObtainDaysActivitiesEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.utilities.Utility;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ActivityService {

    private final static String TAG = "ActivityService";
    private HealthTracApi mApi;
    private Bus mBus;
    private ObtainActivityByIdEvent activityByIdEvent;

    public ActivityService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onCreateActivityEvent(CreateActivityEvent event) {
        mApi.createActivity(event.getActivity(), new Callback<Activity>() {
            @Override
            public void success(Activity activity, Response response) {
                mBus.post(new ActivityCreatedEvent(activity));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not create activity", ApiErrorEvent.Cause.CREATE));
            }
        });
    }

    @Subscribe
    public void onObtainActivityByIdEvent(ObtainActivityByIdEvent event) {
        activityByIdEvent = event;
        mApi.getActivityById(event.getActivityId(), new Callback<Activity>() {
            @Override
            public void success(Activity activity, Response response) {
                mBus.post(new ActivityObtainedEvent(activity, activityByIdEvent.getRequester()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain activity", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onClassifyActivity(ClassifyActivityEvent event) {
        mApi.classifyActivity(event.getActivity(), new Callback<Integer>() {
            @Override
            public void success(Integer integer, Response response) {
                mBus.post(new ActivityClassifiedEvent(integer));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not classify activity.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onObtainDaysActivities(ObtainDaysActivitiesEvent event) {
        String date = Utility.displayUtcDate(event.getDate()).split("T")[0];
        mApi.getDaysActivities(event.getUserId(), date, new Callback<List<Activity>>() {
            @Override
            public void success(List<Activity> activities, Response response) {
                mBus.post(new DaysActivitiesObtainedEvent(activities));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain the day's activities.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }
}
