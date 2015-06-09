package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Group;

import java.util.List;

/**
 * Created by Mike C on 2/19/2015.
 */
public class AllGroupsObtainedEvent implements IEvent {

    private List<Group> mGroups;

    public AllGroupsObtainedEvent(List<Group> groups) {
        mGroups = groups;
    }

    public List<Group> getGroups() {
        return mGroups;
    }
}
