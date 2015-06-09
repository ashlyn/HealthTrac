package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.challengeevents.ChallengeCreatedEvent;
import com.group7.healthtrac.events.challengeevents.ChallengeDeletedEvent;
import com.group7.healthtrac.events.challengeevents.ChallengeUpdatedEvent;
import com.group7.healthtrac.events.challengeevents.ChallengerChallengesObtainedEvent;
import com.group7.healthtrac.events.challengeevents.CreateChallengeEvent;
import com.group7.healthtrac.events.challengeevents.DeleteChallengeEvent;
import com.group7.healthtrac.events.challengeevents.FriendChallengesObtainedEvent;
import com.group7.healthtrac.events.challengeevents.ObtainChallengerChallengesEvent;
import com.group7.healthtrac.events.challengeevents.ObtainFriendChallengesEvent;
import com.group7.healthtrac.events.challengeevents.ObtainUserChallengesEvent;
import com.group7.healthtrac.events.challengeevents.UpdateChallengeEvent;
import com.group7.healthtrac.events.challengeevents.UserChallengesObtainedEvent;
import com.group7.healthtrac.models.Challenge;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Courtney on 4/26/2015.
 */
public class ChallengeService {

    private Bus mBus;
    private HealthTracApi mApi;
    private static String TAG = "ChallengeService";

    public ChallengeService(HealthTracApi api, Bus bus) {
        mBus = bus;
        mApi = api;
    }

    @Subscribe
    public void onObtainUserChallenges(ObtainUserChallengesEvent event) {
        mApi.getUserChallenges(event.getUserId(), new Callback<List<Challenge>>() {
            @Override
            public void success(List<Challenge> challenges, Response response) {
                mBus.post(new UserChallengesObtainedEvent(challenges));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain challenges.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onObtainChallengerChallenges(ObtainChallengerChallengesEvent event) {
        mApi.getChallengerChallenges(event.getUserId(), new Callback<List<Challenge>>() {
            @Override
            public void success(List<Challenge> challenges, Response response) {
                mBus.post(new ChallengerChallengesObtainedEvent(challenges));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain challenges.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onObtainFriendChallenges(ObtainFriendChallengesEvent event) {
        mApi.getFriendChallenges(event.getUserId(), new Callback<List<Challenge>>() {
            @Override
            public void success(List<Challenge> challenges, Response response) {
                mBus.post(new FriendChallengesObtainedEvent(challenges));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain challenges.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onCreateChallenge(CreateChallengeEvent event) {
        mApi.createChallenge(event.getChallenge(), new Callback<Challenge>() {
            @Override
            public void success(Challenge challenge, Response response) {
                mBus.post(new ChallengeCreatedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Failed to create challenge.", ApiErrorEvent.Cause.CREATE));
            }
        });
    }

    @Subscribe
    public void onUpdateChallenge(UpdateChallengeEvent event) {
        mApi.updateChallenge(event.getChallenge().getId(), event.getChallenge(), new Callback<Challenge>() {
            @Override
            public void success(Challenge challenge, Response response) {
                mBus.post(new ChallengeUpdatedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Failed to accept challenge.", ApiErrorEvent.Cause.UPDATE));
            }
        });
    }

    @Subscribe
    public void onDeleteChallenge(DeleteChallengeEvent event) {
        mApi.deleteChallenge(event.getChallenge().getId(), new Callback<Challenge>() {
            @Override
            public void success(Challenge challenge, Response response) {
                mBus.post(new ChallengeDeletedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Failed to decline challenge.", ApiErrorEvent.Cause.DELETE));
            }
        });
    }
}
