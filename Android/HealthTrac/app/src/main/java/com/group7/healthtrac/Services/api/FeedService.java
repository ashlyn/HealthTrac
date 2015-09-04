package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.PopulateFeedEventsEvent;
import com.group7.healthtrac.events.feedevents.EndOfDayReportObtainedEvent;
import com.group7.healthtrac.events.feedevents.FeedEventsObtainedEvent;
import com.group7.healthtrac.events.feedevents.ObtainEndOfDayReportEvent;
import com.group7.healthtrac.events.feedevents.ObtainFeedEventsByGroupIdEvent;
import com.group7.healthtrac.events.feedevents.ObtainFeedEventsByUserIdEvent;
import com.group7.healthtrac.models.EndOfDayReport;
import com.group7.healthtrac.models.FeedEvent;
import com.group7.healthtrac.services.utilities.GroupTuple;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FeedService {

    private final static String TAG = "FeedService";
    private HealthTracApi mApi;
    private Bus mBus;
    private ObtainFeedEventsByUserIdEvent mUserIdEvent;
    private ObtainEndOfDayReportEvent mEndOfDayReportEvent;
    private List<FeedEvent> feedEvents = new ArrayList<>();

    public FeedService(HealthTracApi api, Bus bus) {
        this.mApi = api;
        this.mBus = bus;
    }

    @Subscribe
    public void populateFeedEvents(PopulateFeedEventsEvent event) {
        feedEvents.clear();
        feedEvents.add(new FeedEvent(0, 1, 1, "test", Calendar.getInstance().getTime(), "Obtained the Join the Club! badge", event.getUser()));
        feedEvents.add(new FeedEvent(1, 2, 2, "test", Calendar.getInstance().getTime(), "Joined Best Group", event.getUser()));
        feedEvents.add(new FeedEvent(3, 3, 3, "test", Calendar.getInstance().getTime(), "Completed 25.00 minutes of Running", event.getUser()));
        feedEvents.add(new FeedEvent(6, 4, 4, "test", Calendar.getInstance().getTime(), "Achieved a Daily Steps goal: 5000 steps!", event.getUser()));
        feedEvents.add(new FeedEvent(7, 5, 5, "test", Calendar.getInstance().getTime(), "Feeling accomplished", event.getUser()));
        feedEvents.add(new FeedEvent(5, 6, 6, "test", Calendar.getInstance().getTime(), "Set a new Weekly Steps goal: 70000 steps", event.getUser()));
        feedEvents.add(new FeedEvent(3, 7, 7, "test", Calendar.getInstance().getTime(), "Completed 48.00 minutes of Walking", event.getUser()));
        feedEvents.add(new FeedEvent(4, 8, 8, "test", Calendar.getInstance().getTime(), "Ate 10 Grams of protein", event.getUser()));
    }

    @Subscribe
    public void onGetFeedEventsByUserId(ObtainFeedEventsByUserIdEvent event) {
        mUserIdEvent = event;
        mBus.post(new FeedEventsObtainedEvent(feedEvents, event.getRequester()));
//        mApi.getFeedEventsByUserId(event.getUserId(), new Callback<List<FeedEvent>>() {
//            @Override
//            public void success(List<FeedEvent> feedEvents, Response response) {
//                mBus.post(new FeedEventsObtainedEvent(feedEvents, mUserIdEvent.getRequester()));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not obtain feed events.", ApiErrorEvent.Cause.OBTAIN));
//            }
//        });
    }

    @Subscribe
    public void onGetFeedEventsByGroupId(ObtainFeedEventsByGroupIdEvent event) {
        mBus.post(new FeedEventsObtainedEvent(feedEvents, event.getRequester()));
//        mApi.getFeedEventsByGroupId(event.getGroupId(), new Callback<GroupTuple>() {
//            @Override
//            public void success(GroupTuple groupTuple, Response response) {
//                mBus.post(new FeedEventsObtainedEvent(groupTuple.getM_Item1(), groupTuple.getM_Item2()));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                    mBus.post(new ApiErrorEvent("Could not obtain feed events.", ApiErrorEvent.Cause.OBTAIN));
//            }
//        });
    }

    @Subscribe
    public void onObtainEndOfDayReport(ObtainEndOfDayReportEvent event) {
        mEndOfDayReportEvent = event;
        mApi.getEndOfDayReportById(event.getEndOfDayReportId(), new Callback<EndOfDayReport>() {
            @Override
            public void success(EndOfDayReport endOfDayReport, Response response) {
                mBus.post(new EndOfDayReportObtainedEvent(endOfDayReport, mEndOfDayReportEvent.getRequester()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                    mBus.post(new ApiErrorEvent("Could not obtain end of day report", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }
}
