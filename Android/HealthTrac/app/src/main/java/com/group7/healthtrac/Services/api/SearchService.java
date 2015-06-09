package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.searchevents.CompleteSearchEvent;
import com.group7.healthtrac.events.searchevents.SearchCompletedEvent;
import com.group7.healthtrac.events.searchevents.UsersObtainedEvent;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.User;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Mike C on 2/19/2015.
 */
public class SearchService {

    private final static String TAG = "SearchService";
    private HealthTracApi mApi;
    private CompleteSearchEvent mSearchEvent;
    private UsersObtainedEvent mUsersEvent;
    private Bus mBus;

    public SearchService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onSearchRequest(CompleteSearchEvent event) {
        mSearchEvent = event;
        mApi.searchUsers(event.getQuery(), new Callback<List<User>>() {
            @Override
            public void success(List<User> users, Response response) {
                mBus.post(new UsersObtainedEvent(users, mSearchEvent.getQuery()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not process search of users", ApiErrorEvent.Cause.SEARCH));
            }
        });
    }

    @Subscribe
    public void onUsersSearched(UsersObtainedEvent event) {
        mUsersEvent = event;
        mApi.searchGroups(event.getGroupQuery(), new Callback<List<Group>>() {
            @Override
            public void success(List<Group> groups, Response response) {
                mBus.post(new SearchCompletedEvent(groups, mUsersEvent.getUsers()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not process search of groups", ApiErrorEvent.Cause.SEARCH));
            }
        });
    }
}
