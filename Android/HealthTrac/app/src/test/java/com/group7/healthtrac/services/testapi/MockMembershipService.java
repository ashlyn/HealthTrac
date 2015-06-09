package com.group7.healthtrac.services.testapi;

import com.group7.healthtrac.events.membershipevents.CreateMembershipEvent;
import com.group7.healthtrac.events.membershipevents.MembershipCreatedEvent;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.services.api.HealthTracApi;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Created by Mike C on 2/20/2015.
 */
public class MockMembershipService {

    private final static String TAG = "MockMembershipService";
    private HealthTracApi mApi;
    private Bus mBus;

    public MockMembershipService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onCreateMembershipEvent(CreateMembershipEvent event) {
         mBus.post(new MembershipCreatedEvent(new Membership(event.getGroupId(), event.getUserId(), event.getStatus())));
    }
}
