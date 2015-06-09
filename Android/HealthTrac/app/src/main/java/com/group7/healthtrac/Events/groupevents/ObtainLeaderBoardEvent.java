package com.group7.healthtrac.events.groupevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 4/4/2015.
 */
public class ObtainLeaderBoardEvent implements IEvent {

    private String mCategory;
    private int mGroupId;

    public ObtainLeaderBoardEvent(int groupId, String category) {
        mCategory = category;
        mGroupId = groupId;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public String getCategory() {
        return mCategory;
    }
}
