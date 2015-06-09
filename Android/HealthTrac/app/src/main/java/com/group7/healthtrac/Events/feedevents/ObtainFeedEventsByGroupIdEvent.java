package com.group7.healthtrac.events.feedevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 3/23/2015.
 */
public class ObtainFeedEventsByGroupIdEvent implements IEvent {

    private int mGroupId;
    private String mRequester;

    public ObtainFeedEventsByGroupIdEvent(int groupId, String requester) {
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
