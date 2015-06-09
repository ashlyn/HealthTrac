package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.ApiErrorEvent;
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

    public FeedService(HealthTracApi api, Bus bus) {
        this.mApi = api;
        this.mBus = bus;
    }

    @Subscribe
    public void onGetFeedEventsByUserId(ObtainFeedEventsByUserIdEvent event) {
        mUserIdEvent = event;
        mApi.getFeedEventsByUserId(event.getUserId(), new Callback<List<FeedEvent>>() {
            @Override
            public void success(List<FeedEvent> feedEvents, Response response) {
                mBus.post(new FeedEventsObtainedEvent(feedEvents, mUserIdEvent.getRequester()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain feed events.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onGetFeedEventsByGroupId(ObtainFeedEventsByGroupIdEvent event) {
        mApi.getFeedEventsByGroupId(event.getGroupId(), new Callback<GroupTuple>() {
            @Override
            public void success(GroupTuple groupTuple, Response response) {
                mBus.post(new FeedEventsObtainedEvent(groupTuple.getM_Item1(), groupTuple.getM_Item2()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                    mBus.post(new ApiErrorEvent("Could not obtain feed events.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
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
