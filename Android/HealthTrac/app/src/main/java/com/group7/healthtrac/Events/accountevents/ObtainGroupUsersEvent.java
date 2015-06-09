package com.group7.healthtrac.events.accountevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 2/20/2015.
 */
public class ObtainGroupUsersEvent implements IEvent {

    private int mGroupId;

    public ObtainGroupUsersEvent(int groupId) {
        mGroupId = groupId;
    }

    public int getGroupId() {
        return mGroupId;
    }
}
