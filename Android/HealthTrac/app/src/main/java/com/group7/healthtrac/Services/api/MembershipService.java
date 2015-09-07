package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.membershipevents.CreateMembershipEvent;
import com.group7.healthtrac.events.membershipevents.DeleteMembershipEvent;
import com.group7.healthtrac.events.membershipevents.MembershipCreatedEvent;
import com.group7.healthtrac.events.membershipevents.MembershipDeletedEvent;
import com.group7.healthtrac.events.membershipevents.MembershipUpdatedEvent;
import com.group7.healthtrac.events.membershipevents.UpdateMembershipEvent;
import com.group7.healthtrac.models.Membership;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Mike C on 2/20/2015.
 */
public class MembershipService {

    private final static String TAG = "MembershipService";
    private HealthTracApi mApi;
    private Bus mBus;
    private UpdateMembershipEvent mUpdateMembershipEvent;

    public MembershipService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onCreateMembershipEvent(CreateMembershipEvent event) {
        mBus.post(new MembershipCreatedEvent(new Membership(event.getGroupId(), event.getUserId(), event.getStatus())));
//        mApi.createMembership(new Membership(event.getGroupId(), event.getUserId(), event.getStatus()), new Callback<Membership>() {
//            @Override
//            public void success(Membership membership, Response response) {
//                mBus.post(new MembershipCreatedEvent(membership));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not create membership", ApiErrorEvent.Cause.CREATE));
//            }
//        });
    }

    @Subscribe
    public void onDeleteMembershipEvent(DeleteMembershipEvent event) {
        mApi.deleteMembership(event.getMembership().getId(), new Callback<Membership>() {
            @Override
            public void success(Membership membership, Response response) {
                mBus.post(new MembershipDeletedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not delete membership", ApiErrorEvent.Cause.DELETE));
            }
        });
    }

    @Subscribe
    public void onUpdateMembershipEvent(UpdateMembershipEvent event){
        mUpdateMembershipEvent = event;
        mApi.updateMembership(event.getMembership().getId(), event.getMembership(), new Callback<Membership>() {
            @Override
            public void success(Membership membership, Response response) {
                mBus.post(new MembershipUpdatedEvent(mUpdateMembershipEvent.getMessage()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not update membership.", ApiErrorEvent.Cause.UPDATE));
            }
        });
    }
}
