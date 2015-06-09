package com.group7.healthtrac.events.accountevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.User;

/**
 * Created by Mike C on 2/18/2015.
 */
public class AccountCreatedEvent implements IEvent {

    private User mUser;

    public AccountCreatedEvent(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }
}
