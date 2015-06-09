package com.group7.healthtrac.events.badgeevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.UserBadge;

public class BadgeCreatedEvent implements IEvent {

    private UserBadge mUserBadge;

    public BadgeCreatedEvent(UserBadge userBadge) {
        mUserBadge = userBadge;
    }

    public UserBadge getUserBadge() {
        return mUserBadge;
    }
}
