package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 2/20/2015.
 */
public class DeleteGroupEvent implements IEvent {

    private int mGroupId;

    public DeleteGroupEvent(int groupId) {
        mGroupId = groupId;
    }

    public int getGroupId() {
        return mGroupId;
    }
}
