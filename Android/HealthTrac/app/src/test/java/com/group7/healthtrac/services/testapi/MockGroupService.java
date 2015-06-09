package com.group7.healthtrac.services.testapi;

import com.group7.healthtrac.events.groupevents.AllGroupsObtainedEvent;
import com.group7.healthtrac.events.groupevents.CreateGroupEvent;
import com.group7.healthtrac.events.groupevents.GroupCreatedEvent;
import com.group7.healthtrac.events.groupevents.GroupObtainedEvent;
import com.group7.healthtrac.events.groupevents.ObtainAllGroupsEvent;
import com.group7.healthtrac.events.groupevents.ObtainGroupEvent;
import com.group7.healthtrac.events.groupevents.ObtainUserGroupsEvent;
import com.group7.healthtrac.events.groupevents.UserGroupsObtainedEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.Login;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.models.UserBadge;
import com.group7.healthtrac.services.api.HealthTracApi;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mike C on 2/19/2015.
 */
public class MockGroupService {

    private final static String TAG = "MockGroupService";
    private HealthTracApi mApi;
    private Bus mBus;

    public MockGroupService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onCreateGroupEvent(CreateGroupEvent event) {
        mBus.post(new GroupCreatedEvent(event.getGroup()));
    }


    @Subscribe
    public void onObtainGroupEvent(ObtainGroupEvent event) {
        Group group = new Group(new ArrayList<Membership>(), new ArrayList<User>(), 1, "Test Group", "testing", "fakeImage.png");

        mBus.post(new GroupObtainedEvent(group));
    }

    @Subscribe
    public void onObtainAllGroupsEvent(ObtainAllGroupsEvent event) {
        ArrayList<Group> groups = new ArrayList<>();
        groups.add(new Group("Test Group", "testing", "fakeImage.png"));
        mBus.post(new AllGroupsObtainedEvent(groups));
    }

    @Subscribe
    public void onObtainUserGroupsEvent(ObtainUserGroupsEvent event) {
        ArrayList<Group> groups = new ArrayList<>();
        ArrayList<User> users = new ArrayList<>();
        ArrayList<Login> logins = new ArrayList<>();
        ArrayList<Membership> groupMembership = new ArrayList<>();
        ArrayList<Activity> activities = new ArrayList<>();
        ArrayList<Goal> goals = new ArrayList<>();
        ArrayList<UserBadge> badges = new ArrayList<>();

        logins.add(new Login("37757277-697b-4662-bee7-d7d3067ac2b0", "Facebook", "1069090856450340"));
        groupMembership.add(new Membership(1, "37757277-697b-4662-bee7-d7d3067ac2b0", Membership.MEMBER));
        users.add(new User("Michael Casper", "Mike", 6, 0, 150, "Lincoln, Nebraska", new Date(), "Male", "iamdefinatlynotyou@gmail.com", groupMembership, logins, activities, goals, badges, "37757277-697b-4662-bee7-d7d3067ac2b0", "fakeImage.png"));

        groups.add(new Group(groupMembership, users, 1, "Test Group", "testing", "fakeImage.png"));
        mBus.post(new UserGroupsObtainedEvent(groups));
    }

}
