package com.group7.healthtrac;

import com.group7.healthtrac.events.groupevents.CreateGroupEvent;
import com.group7.healthtrac.events.groupevents.GroupCreatedEvent;
import com.group7.healthtrac.events.groupevents.GroupObtainedEvent;
import com.group7.healthtrac.events.groupevents.ObtainGroupEvent;
import com.group7.healthtrac.events.groupevents.ObtainUserGroupsEvent;
import com.group7.healthtrac.events.groupevents.UserGroupsObtainedEvent;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.services.testapi.MockGroupService;
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
public class GroupTest {

    private Bus bus;

    @Before
    public void setUp() {
        bus = new Bus(ThreadEnforcer.ANY);
    }

    @Test
    public void createGroupTest() {
        Group group = new Group("Newest group", "It's the newest", "fakeImage.png");
        final AtomicBoolean testDone = new AtomicBoolean(false);

        bus.register(new Object() {
            @Subscribe
            public void onGroupCreated(GroupCreatedEvent event) {
                assertEquals("Newest group", event.getGroup().getGroupName());
                testDone.set(true);
            }
        });

        bus.register(new MockGroupService(null, bus));

        bus.post(new CreateGroupEvent(group));

        while(!testDone.get());
    }

    @Test
    public void getGroupTest() {
        int groupId = 1;
        final AtomicBoolean testDone = new AtomicBoolean(false);

        bus.register(new Object() {
            @Subscribe
            public void onGroupCreated(GroupObtainedEvent event) {
                assertEquals(1, event.getGroup().getId());
                assertEquals("testing", event.getGroup().getDescription());
                testDone.set(true);
            }
        });

        bus.register(new MockGroupService(null, bus));

        bus.post(new ObtainGroupEvent(groupId));

        while(!testDone.get());
    }

    @Test
    public void getUserGroupsTest() {
        String userId = "37757277-697b-4662-bee7-d7d3067ac2b0";
        final AtomicBoolean testDone = new AtomicBoolean(false);

        bus.register(new Object() {
            @Subscribe
            public void onUserGroupsObtained(UserGroupsObtainedEvent event) {
                assertEquals("Test Group", event.getGroups().get(0).getGroupName());
                assertEquals(1, event.getGroups().get(0).getId());
                assertEquals("37757277-697b-4662-bee7-d7d3067ac2b0", event.getGroups().get(0).getGroupMembers().get(0).getUserId());
                testDone.set(true);
            }
        });

        bus.register(new MockGroupService(null, bus));

        bus.post(new ObtainUserGroupsEvent(userId));

        while(!testDone.get());
    }
}
