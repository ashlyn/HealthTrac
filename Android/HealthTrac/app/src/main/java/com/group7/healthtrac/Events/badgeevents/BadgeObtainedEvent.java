package com.group7.healthtrac.events.badgeevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Badge;
import com.group7.healthtrac.models.UserBadge;

/**
 * Created by Mike C on 3/31/2015.
 */
public class BadgeObtainedEvent implements IEvent {

    private UserBadge mUserBadge;
    private Badge mBadge;
    private String mRequester;

    public BadgeObtainedEvent(UserBadge userBadge, Badge badge, String requester) {
        mBadge = badge;
        mUserBadge = userBadge;
        mRequester = requester;
    }

    public UserBadge getUserBadge() {
        return mUserBadge;
    }

    public Badge getBadge() {
        return mBadge;
    }

    public String getRequester() {
        return mRequester;
    }
}
