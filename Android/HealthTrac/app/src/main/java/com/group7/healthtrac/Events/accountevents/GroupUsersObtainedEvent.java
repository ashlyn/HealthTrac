package com.group7.healthtrac.events.accountevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.User;

import java.util.List;

/**
 * Created by Mike C on 2/20/2015.
 */
public class GroupUsersObtainedEvent  implements IEvent {

    private List<User> mUsers;

    public GroupUsersObtainedEvent(List<User> users) {
        mUsers = users;
    }

    public List<User> getUsers() {
        return mUsers;
    }
}
