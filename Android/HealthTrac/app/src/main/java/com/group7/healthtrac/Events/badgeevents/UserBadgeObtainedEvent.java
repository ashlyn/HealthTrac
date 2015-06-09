package com.group7.healthtrac.events.badgeevents;

import com.group7.healthtrac.models.Badge;
import com.group7.healthtrac.models.UserBadge;

/**
 * Created by Mike C on 3/30/2015.
 */
public class UserBadgeObtainedEvent {

    private UserBadge mUserBadge;
    private Badge mBadge;
    private String mRequester;

    public UserBadgeObtainedEvent(UserBadge userBadge, Badge badge, String requester) {
        mUserBadge = userBadge;
        mBadge = badge;
        mRequester = requester;
    }

    public UserBadge getUserBadge() {
        return mUserBadge;
    }

    public String getRequester() {
        return mRequester;
    }

    public Badge getBadge() {
        return mBadge;
    }
}
