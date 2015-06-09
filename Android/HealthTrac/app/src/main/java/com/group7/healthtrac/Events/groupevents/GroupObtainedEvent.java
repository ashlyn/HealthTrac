package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Group;

/**
 * Created by Mike C on 2/19/2015.
 */
public class GroupObtainedEvent implements IEvent {

    private Group mGroup;
    private String mRequester;

    public GroupObtainedEvent(Group group) {
        mGroup = group;
    }

    public GroupObtainedEvent(Group group, String requester) {
        mGroup = group;
        mRequester = requester;
    }

    public Group getGroup() {
        return mGroup;
    }

    public String getRequester() {
        return mRequester;
    }
}
