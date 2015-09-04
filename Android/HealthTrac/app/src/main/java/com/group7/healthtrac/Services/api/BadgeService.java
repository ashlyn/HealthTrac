package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.ChallengeUserActivity;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.badgeevents.BadgeCreatedEvent;
import com.group7.healthtrac.events.badgeevents.CreateBadgeEvent;
import com.group7.healthtrac.events.badgeevents.ObtainBadgeEvent;
import com.group7.healthtrac.events.badgeevents.ObtainUserBadgeEvent;
import com.group7.healthtrac.events.badgeevents.ObtainUserBadgesEvent;
import com.group7.healthtrac.events.badgeevents.UserBadgeObtainedEvent;
import com.group7.healthtrac.events.badgeevents.UserBadgesObtainedEvent;
import com.group7.healthtrac.models.Badge;
import com.group7.healthtrac.models.UserBadge;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BadgeService {

    private final static String TAG = "BadgeService";
    private HealthTracApi mApi;
    private Bus mBus;
    private ObtainUserBadgeEvent mObtainUserBadgeEvent;
    private ObtainBadgeEvent mObtainBadgeEvent;

    public BadgeService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onObtainUserBadgesEvent(ObtainUserBadgesEvent event) {
        mBus.post(new UserBadgesObtainedEvent(null));
//        mApi.getUserBadges(event.getUserId(), new Callback<List<Badge>>() {
//            @Override
//            public void success(List<Badge> badges, Response response) {
//                mBus.post(new UserBadgesObtainedEvent(badges));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not obtain badges.", ApiErrorEvent.Cause.OBTAIN));
//            }
//        });
    }

    @Subscribe
    public void onObtainUserBadgeEvent(ObtainUserBadgeEvent event) {
        mObtainUserBadgeEvent = event;
        mApi.getUserBadge(event.getUserBadgeId(), new Callback<UserBadge>() {
            @Override
            public void success(UserBadge userBadge, Response response) {
                mBus.post(new ObtainBadgeEvent(userBadge, userBadge.getBadgeId(), mObtainUserBadgeEvent.getRequester()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain user's badge information.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onObtainBadgeEvent(ObtainBadgeEvent event) {
        mObtainBadgeEvent = event;
        mApi.getBadgeById(event.getBadgeId(), new Callback<Badge>() {
            @Override
            public void success(Badge badge, Response response) {
                mBus.post(new UserBadgeObtainedEvent(mObtainBadgeEvent.getUserBadge(), badge, mObtainUserBadgeEvent.getRequester()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain badge information.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onCreateBadge(CreateBadgeEvent event) {
        mBus.post(new BadgeCreatedEvent(event.getUserBadge()));
//        mApi.createUserBadge(event.getUserBadge(), new Callback<UserBadge>() {
//            @Override
//            public void success(UserBadge userBadge, Response response) {
//                mBus.post(new BadgeCreatedEvent(userBadge));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not create badge.", ApiErrorEvent.Cause.CREATE));
//            }
//        });
    }
}
