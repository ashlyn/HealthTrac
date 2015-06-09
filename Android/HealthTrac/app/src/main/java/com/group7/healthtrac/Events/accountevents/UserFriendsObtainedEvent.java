package com.group7.healthtrac.events.accountevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.User;

import java.util.List;

public class UserFriendsObtainedEvent implements IEvent {

    private List<User> mUserFriends;

    public UserFriendsObtainedEvent(List<User> userFriends) {
        mUserFriends = userFriends;
    }

    public List<User> getUserFriends() {
        return mUserFriends;
    }
}
