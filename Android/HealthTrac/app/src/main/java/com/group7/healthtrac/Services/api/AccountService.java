package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.accountevents.AccountCreatedEvent;
import com.group7.healthtrac.events.accountevents.AccountDeletedEvent;
import com.group7.healthtrac.events.accountevents.AccountUpdatedEvent;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.accountevents.CreateAccountEvent;
import com.group7.healthtrac.events.accountevents.DeleteAccountEvent;
import com.group7.healthtrac.events.accountevents.GroupUsersObtainedEvent;
import com.group7.healthtrac.events.accountevents.ObtainGroupUsersEvent;
import com.group7.healthtrac.events.accountevents.ObtainUserByFacebookEvent;
import com.group7.healthtrac.events.accountevents.ObtainUserEvent;
import com.group7.healthtrac.events.accountevents.ObtainUserFriendsEvent;
import com.group7.healthtrac.events.accountevents.ObtainedUserByFacebookEvent;
import com.group7.healthtrac.events.accountevents.UpdateAccountEvent;
import com.group7.healthtrac.events.accountevents.UserFriendsObtainedEvent;
import com.group7.healthtrac.events.accountevents.UserObtainedEvent;
import com.group7.healthtrac.models.UpdateUser;
import com.group7.healthtrac.models.User;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Mike C on 2/18/2015.
 */
public class AccountService {

    private final static String TAG = "AccountService";
    private HealthTracApi mApi;
    private Bus mBus;

    public AccountService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onCreateAccount(CreateAccountEvent event) {
        event.getUser().setId("testing");
        mBus.post(new AccountCreatedEvent(event.getUser()));
//        mApi.createUser(event.getUser(), new Callback<User>() {
//            @Override
//            public void success(User user, Response response) {
//                mBus.post(new AccountCreatedEvent(user));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not create account", ApiErrorEvent.Cause.CREATE));
//            }
//        });
    }

    @Subscribe
    public void onUpdateAccount(UpdateAccountEvent event) {

        mApi.updateUser(event.getUser().getId(), new UpdateUser(event.getUser()), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                mBus.post(new AccountUpdatedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not update account", ApiErrorEvent.Cause.UPDATE));
            }
        });
    }

    @Subscribe
    public void onGetUserBySocialNetworkId(ObtainUserByFacebookEvent event) {
        Log.i(TAG, "obtained social network request");
        mBus.post(new ObtainedUserByFacebookEvent(null, null));
//        mApi.getUserBySocialNetworkId(event.getUserId(), new Callback<User>() {
//            @Override
//            public void success(User user, Response response) {
//                Log.i(TAG, "success");
//                mBus.post(new ObtainedUserByFacebookEvent(user, null));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                Log.i(TAG, "Didn't find user");
//                mBus.post(new ObtainedUserByFacebookEvent(null, error));
//            }
//        });
    }

    @Subscribe
    public void onObtainUser(ObtainUserEvent event) {
        Log.i(TAG, "Trying to obtain user info");
        mApi.getUserById(event.getUserId(), new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                Log.i(TAG, "user obtained in service");
                mBus.post(new UserObtainedEvent(user));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i(TAG, "failed");
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain user information", ApiErrorEvent.Cause.UPDATE));
            }
        });
    }

    @Subscribe
    public void onObtainGroupUsersEvent(ObtainGroupUsersEvent event) {
        mApi.getUsersInGroup(event.getGroupId(), new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                mBus.post(new GroupUsersObtainedEvent(users));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain user information", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onDeleteAccountEvent(DeleteAccountEvent event) {
        mApi.deleteUser(event.getUserId(),new Callback<String>() {
            @Override
            public void success(String userId, Response response) {
                mBus.post(new AccountDeletedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not delete account", ApiErrorEvent.Cause.DELETE));
            }
        });
    }

    @Subscribe
    public void onObtainFriends(ObtainUserFriendsEvent event) {
        mApi.getUserFriends(event.getUserId(), new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                mBus.post(new UserFriendsObtainedEvent(users));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain friends list", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }
}
