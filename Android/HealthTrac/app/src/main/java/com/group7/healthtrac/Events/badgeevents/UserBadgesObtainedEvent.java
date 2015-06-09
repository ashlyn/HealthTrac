package com.group7.healthtrac.events.badgeevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Badge;

import java.util.List;

/**
 * Created by Mike C on 3/26/2015.
 */
public class UserBadgesObtainedEvent implements IEvent {

    private List<Badge> mBadges;

    public UserBadgesObtainedEvent(List<Badge> badges) {
        mBadges = badges;
    }

    public List<Badge> getBadges() {
        return mBadges;
    }
}
