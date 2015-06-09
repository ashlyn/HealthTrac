package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Group;

/**
 * Created by Mike C on 2/19/2015.
 */
public class CreateGroupEvent implements IEvent {

    private Group mGroup;

    public CreateGroupEvent(Group group) {
        mGroup = group;
    }

    public Group getGroup() {
        return mGroup;
    }
}
