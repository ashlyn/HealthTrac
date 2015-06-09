package com.group7.healthtrac.services.testapi;

import com.group7.healthtrac.events.activityevents.ActivityCreatedEvent;
import com.group7.healthtrac.events.activityevents.CreateActivityEvent;
import com.group7.healthtrac.services.api.HealthTracApi;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by Mike C on 3/13/2015.
 */
public class MockActivityService {

    private final static String TAG = "MockActivityService";
    private HealthTracApi mApi;
    private Bus mBus;

    public MockActivityService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onCreateActivityEvent(CreateActivityEvent event) {
        mBus.post(new ActivityCreatedEvent(event.getActivity()));
    }
}
