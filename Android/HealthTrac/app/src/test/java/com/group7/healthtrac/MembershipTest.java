package com.group7.healthtrac;

import com.group7.healthtrac.events.membershipevents.CreateMembershipEvent;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.services.testapi.MockMembershipService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.TestCase.assertEquals;

@Config(emulateSdk = 16, reportSdk = 16)
@RunWith(CustomTestRunner.class)
public class MembershipTest {

    private Bus bus;

    @Before
    public void setUp() {
        bus = new Bus(ThreadEnforcer.ANY);
    }

    @Test
    public void createMembershipTest() {
        int groupId = 1;
        String userId = "37757277-697b-4662-bee7-d7d3067ac2b0";
        int status = Membership.INVITED;
        final AtomicBoolean testDone = new AtomicBoolean(false);

        bus.register(new Object() {
            @Subscribe
            public void onCreateMembership(CreateMembershipEvent event) {
                assertEquals("37757277-697b-4662-bee7-d7d3067ac2b0", event.getUserId());
                assertEquals(Membership.INVITED, event.getStatus());
                assertEquals(1, event.getGroupId());
                testDone.set(true);
            }
        });

        bus.register(new MockMembershipService(null, bus));

        bus.post(new CreateMembershipEvent(groupId, userId, status));

        while(!testDone.get());
    }
}
