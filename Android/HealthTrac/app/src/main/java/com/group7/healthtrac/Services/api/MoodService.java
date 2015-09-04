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

import java.util.ArrayList;
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
        mBus.post(new MoodUpdatedEvent());
//        mApi.createUserMood(event.getUserMood(), new Callback<UserMood>() {
//            @Override
//            public void success(UserMood userMood, Response response) {
//                mBus.post(new MoodUpdatedEvent());
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not update your mood.", ApiErrorEvent.Cause.UPDATE));
//            }
//        });
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
        List<Mood> moods = new ArrayList<>();
        moods.add(new Mood("Angry", 1, "http://i.imgur.com/kClMrGV.png"));
        moods.add(new Mood("Anxious", 2, "http://i.imgur.com/HJwmzpO.png"));
        moods.add(new Mood("Accomplished", 3, "http://i.imgur.com/loh7Uvu.png"));
        moods.add(new Mood("Fabulous", 4, "http://i.imgur.com/g5TYEBn.png"));
        moods.add(new Mood("Happy", 5, "http://i.imgur.com/Z54l7Uz.png"));
        moods.add(new Mood("Motivated", 6, "http://i.imgur.com/N1u5cxZ.png"));
        moods.add(new Mood("Sad", 7, "http://i.imgur.com/ZZfDpFI.png"));
        moods.add(new Mood("Salty", 8, "http://i.imgur.com/wnuMWSv.png"));
        moods.add(new Mood("Sick", 9, "http://i.imgur.com/TqKBTvv.png"));
        moods.add(new Mood("Sweaty", 10, "http://i.imgur.com/Tvn6NK7.png"));
        moods.add(new Mood("Tired", 11, "http://i.imgur.com/s4bm7IR.png"));
        moods.add(new Mood("Victorious", 12, "http://i.imgur.com/S4YZt5j.png"));

        mBus.post(new AllMoodsObtainedEvent(moods));
//        mApi.getAllMoods(new Callback<List<Mood>>() {
//            @Override
//            public void success(List<Mood> moods, Response response) {
//                mBus.post(new AllMoodsObtainedEvent(moods));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not obtain moods list.", ApiErrorEvent.Cause.OBTAIN));
//            }
//        });
    }
}
