package com.group7.healthtrac.events.accountevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.User;

/**
 * Created by Mike C on 2/18/2015.
 */
public class CreateAccountEvent  implements IEvent {

    private User mUser;

    public CreateAccountEvent(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }
}
