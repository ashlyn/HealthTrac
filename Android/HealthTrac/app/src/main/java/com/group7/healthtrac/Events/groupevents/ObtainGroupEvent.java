package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 2/19/2015.
 */
public class ObtainGroupEvent implements IEvent {

    private int mGroupId;
    private String mRequester;

    public ObtainGroupEvent(int groupId) {
        mGroupId = groupId;
    }

    public ObtainGroupEvent(int groupId, String requester) {
        mGroupId = groupId;
        mRequester = requester;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public String getRequester() {
        return mRequester;
    }
}
