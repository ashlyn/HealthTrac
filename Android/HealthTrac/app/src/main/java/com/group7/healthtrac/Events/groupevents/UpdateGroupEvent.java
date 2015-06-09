package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Group;

/**
 * Created by Mike C on 2/19/2015.
 */
public class UpdateGroupEvent implements IEvent {

    private Group mGroup;
    private int mGroupId;

    public UpdateGroupEvent(Group group, int groupId) {
        mGroup = group;
        mGroupId = groupId;
    }

    public Group getGroup() {
        return mGroup;
    }

    public int getGroupId() {
        return mGroupId;
    }
}
