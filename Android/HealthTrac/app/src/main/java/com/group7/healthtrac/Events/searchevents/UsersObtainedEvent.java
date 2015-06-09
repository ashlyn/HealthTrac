package com.group7.healthtrac.events.searchevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.User;

import java.util.List;

/**
 * Created by Mike C on 2/19/2015.
 */
public class UsersObtainedEvent implements IEvent {

    private List<User> mUsers;
    private String mGroupQuery;

    public UsersObtainedEvent(List<User> users, String groupQuery) {
        mUsers = users;
        mGroupQuery = groupQuery;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public String getGroupQuery() {
        return mGroupQuery;
    }
}
