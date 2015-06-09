package com.group7.healthtrac;

import com.group7.healthtrac.events.accountevents.AccountCreatedEvent;
import com.group7.healthtrac.events.accountevents.CreateAccountEvent;
import com.group7.healthtrac.events.activityevents.ActivityCreatedEvent;
import com.group7.healthtrac.events.activityevents.CreateActivityEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.services.testapi.MockAccountService;
import com.group7.healthtrac.services.testapi.MockActivityService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Josh on 4/9/2015.
 */
@Config(emulateSdk = 16, reportSdk = 16)
@RunWith(CustomTestRunner.class)
public class ActivityTest {

    private Bus bus;

    @Before
    public void setUp() {
        bus = new Bus(ThreadEnforcer.ANY);
    }

    @Test
    public void createActivityTest() {
        final AtomicBoolean testDone = new AtomicBoolean(false);

        Activity activity = new Activity(0, 3000, "37757277-697b-4662-bee7-d7d3067ac2b0", "Walk", new Date(), 400, 20);
        bus.register(new Object() {
            @Subscribe
            public void onNewActivityCreated(ActivityCreatedEvent event) {
                assertEquals(3000.0, event.getActivity().getDuration());
                assertEquals(400.0, event.getActivity().getDistance());
                testDone.set(true);
            }
        });

        bus.register(new MockActivityService(null, bus));

        bus.post(new CreateActivityEvent(activity));

        while(!testDone.get());
    }

}
