package com.group7.healthtrac.events.badgeevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 3/30/2015.
 */
public class ObtainUserBadgeEvent implements IEvent {

    private int mUserBadgeId;
    private String mRequester;

    public ObtainUserBadgeEvent(int userBadgeId, String requester) {
        mUserBadgeId = userBadgeId;
        mRequester = requester;
    }

    public int getUserBadgeId() {
        return mUserBadgeId;
    }

    public String getRequester() {
        return mRequester;
    }
}
