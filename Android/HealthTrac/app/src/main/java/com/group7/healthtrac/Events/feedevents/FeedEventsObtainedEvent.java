package com.group7.healthtrac.events.feedevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.FeedEvent;

import java.util.List;

/**
 * Created by Mike C on 3/23/2015.
 */
public class FeedEventsObtainedEvent implements IEvent {

    private List<FeedEvent> mFeedEvents;
    private String mRequester;
    private int mRequestingGroup;

    public FeedEventsObtainedEvent(List<FeedEvent> feedEvents, String requester) {
        mFeedEvents = feedEvents;
        mRequester = requester;
    }

    public FeedEventsObtainedEvent(List<FeedEvent> feedEvents, int requestingGroup) {
        mFeedEvents = feedEvents;
        mRequestingGroup = requestingGroup;
    }

    public List<FeedEvent> getFeedEvents() {
        return mFeedEvents;
    }

    public String getRequester() {
        return mRequester;
    }

    public int getRequestingGroup() {
        return mRequestingGroup;
    }
}
