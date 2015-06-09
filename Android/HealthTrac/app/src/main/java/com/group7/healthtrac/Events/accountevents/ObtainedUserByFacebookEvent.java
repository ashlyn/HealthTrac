package com.group7.healthtrac.events.accountevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.User;

import retrofit.RetrofitError;

/**
 * Created by Mike C on 2/18/2015.
 */
public class ObtainedUserByFacebookEvent implements IEvent {

    private User mUser;
    private RetrofitError mError;

    public ObtainedUserByFacebookEvent(User user, RetrofitError error) {
        mError = error;
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }

    public RetrofitError getError() {
        return mError;
    }
}
