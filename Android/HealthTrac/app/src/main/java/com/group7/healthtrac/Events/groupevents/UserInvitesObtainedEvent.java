package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Group;

import java.util.List;

/**
 * Created by Courtney on 4/26/2015.
 */
public class UserInvitesObtainedEvent implements IEvent {

    private List<Group> mGroups;

    public UserInvitesObtainedEvent(List<Group> groups) {
        mGroups = groups;
    }

    public List<Group> getGroups() {
        return mGroups;
    }
}
