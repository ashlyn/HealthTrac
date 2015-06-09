package com.group7.healthtrac.events.feedevents;

import com.group7.healthtrac.events.IEvent;

/**
 * Created by Mike C on 4/6/2015.
 */
public class ObtainEndOfDayReportEvent implements IEvent {

    private String mRequester;
    private int mEndOfDayReportId;

    public ObtainEndOfDayReportEvent(int endOfDayReportId, String requester) {
        mEndOfDayReportId = endOfDayReportId;
        mRequester = requester;
    }

    public String getRequester() {
        return mRequester;
    }

    public int getEndOfDayReportId() {
        return mEndOfDayReportId;
    }
}
