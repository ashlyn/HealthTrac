package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.moodevents.AllMoodsObtainedEvent;
import com.group7.healthtrac.events.moodevents.MoodUpdatedEvent;
import com.group7.healthtrac.events.moodevents.ObtainAllMoodsEvent;
import com.group7.healthtrac.events.moodevents.ObtainMoodEvent;
import com.group7.healthtrac.events.moodevents.ObtainUserMoodEvent;
import com.group7.healthtrac.events.moodevents.UpdateMoodEvent;
import com.group7.healthtrac.events.moodevents.UserMoodObtainedEvent;
import com.group7.healthtrac.models.Mood;
import com.group7.healthtrac.models.UserMood;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MoodService {

    private final static String TAG = "MoodService";
    private HealthTracApi mApi;
    private Bus mBus;
    private ObtainUserMoodEvent mObtainUserMoodEvent;
    private ObtainMoodEvent mObtainMoodEvent;

    public MoodService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onUpdateMoodEvent(UpdateMoodEvent event) {
        mApi.createUserMood(event.getUserMood(), new Callback<UserMood>() {
            @Override
            public void success(UserMood userMood, Response response) {
                mBus.post(new MoodUpdatedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not update your mood.", ApiErrorEvent.Cause.UPDATE));
            }
        });
    }

    @Subscribe
    public void onObtainUserMoodEvent(ObtainUserMoodEvent event) {
        mObtainUserMoodEvent = event;
        mApi.getUserMood(event.getUserMoodId(), new Callback<UserMood>() {
            @Override
            public void success(UserMood userMood, Response response) {
                mBus.post(new ObtainMoodEvent(userMood.getMoodId(), mObtainUserMoodEvent.getRequester()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain mood event.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onObtainMoodEvent(ObtainMoodEvent event) {
        mObtainMoodEvent = event;
        mApi.getMoodById(event.getMoodId(), new Callback<Mood>() {
            @Override
            public void success(Mood mood, Response response) {
                mBus.post(new UserMoodObtainedEvent(mood, mObtainMoodEvent.getRequester()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain mood information.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onObtainAllMoods(ObtainAllMoodsEvent event) {
        mApi.getAllMoods(new Callback<List<Mood>>() {
            @Override
            public void success(List<Mood> moods, Response response) {
                mBus.post(new AllMoodsObtainedEvent(moods));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain moods list.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }
}
