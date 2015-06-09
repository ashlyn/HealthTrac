package com.group7.healthtrac.events.membershipevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 2/20/2015.
 */
public class CreateMembershipEvent implements IEvent {

    private int mGroupId;
    private String mUserId;
    private int mStatus;

    public CreateMembershipEvent(int groupId, String userId, int status) {
        mGroupId = groupId;
        mUserId = userId;
        mStatus = status;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public String getUserId() {
        return mUserId;
    }

    public int getStatus() {
        return mStatus;
    }
}
