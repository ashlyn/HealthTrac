package com.group7.healthtrac.events.badgeevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.UserBadge;

/**
 * Created by Mike C on 4/4/2015.
 */
public class CreateBadgeEvent implements IEvent {

    private UserBadge mUserBadge;

    public CreateBadgeEvent(UserBadge userBadge) {
        mUserBadge = userBadge;
    }

    public UserBadge getUserBadge() {
        return mUserBadge;
    }
}
