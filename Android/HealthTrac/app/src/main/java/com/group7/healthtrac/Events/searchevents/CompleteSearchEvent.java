package com.group7.healthtrac.events.searchevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 2/19/2015.
 */
public class CompleteSearchEvent implements IEvent {

    String mQuery;

    public CompleteSearchEvent(String query) {
        mQuery = query;
    }

    public String getQuery() {
        return mQuery;
    }
}
