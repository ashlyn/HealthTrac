package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Group;

/**
 * Created by Mike C on 2/19/2015.
 */
public class GroupCreatedEvent implements IEvent {

    private Group mGroup;

    public GroupCreatedEvent(Group group) {
        mGroup = group;
    }

    public Group getGroup() {
        return mGroup;
    }
}
