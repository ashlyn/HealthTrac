package com.group7.healthtrac.events.badgeevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.UserBadge;

public class ObtainBadgeEvent implements IEvent {

    private UserBadge mUserBadge;
    private int  mBadgeId;
    private String mRequester;

    public ObtainBadgeEvent(UserBadge userBadge, int badgeId, String requester) {
        mBadgeId = badgeId;
        mUserBadge = userBadge;
        mRequester = requester;
    }

    public UserBadge getUserBadge() {
        return mUserBadge;
    }

    public int getBadgeId() {
        return mBadgeId;
    }

    public String getRequester() {
        return mRequester;
    }
}
