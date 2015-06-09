package com.group7.healthtrac.events.feedevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.EndOfDayReport;

/**
 * Created by Mike C on 4/6/2015.
 */
public class EndOfDayReportObtainedEvent implements IEvent {

    private EndOfDayReport mEndOfDayReport;
    private String mRequester;

    public EndOfDayReportObtainedEvent(EndOfDayReport endOfDayReport, String requester) {
        mEndOfDayReport = endOfDayReport;
        mRequester = requester;
    }

    public EndOfDayReport getEndOfDayReport() {
        return mEndOfDayReport;
    }

    public String getRequester() {
        return mRequester;
    }
}
