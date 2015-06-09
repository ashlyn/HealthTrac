package com.group7.healthtrac.events.searchevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.User;

import java.util.List;

/**
 * Created by Mike C on 2/19/2015.
 */
public class SearchCompletedEvent implements IEvent {

    private List<User> mUsers;
    private List<Group> mGroups;

    public SearchCompletedEvent(List<Group> groups, List<User> users) {
        mGroups = groups;
        mUsers = users;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public List<Group> getGroups() {
        return mGroups;
    }
}
