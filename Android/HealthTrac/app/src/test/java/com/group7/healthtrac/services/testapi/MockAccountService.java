package com.group7.healthtrac.services.testapi;

import com.group7.healthtrac.events.accountevents.AccountCreatedEvent;
import com.group7.healthtrac.events.accountevents.CreateAccountEvent;
import com.group7.healthtrac.events.accountevents.GroupUsersObtainedEvent;
import com.group7.healthtrac.events.accountevents.ObtainGroupUsersEvent;
import com.group7.healthtrac.events.accountevents.ObtainUserByFacebookEvent;
import com.group7.healthtrac.events.accountevents.ObtainedUserByFacebookEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.Goal;
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
 * Created by Mike C on 2/18/2015.
 */
public class MockAccountService {

    private final static String TAG = "MockAccountService";
    private HealthTracApi mApi;
    private Bus mBus;

    public MockAccountService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onCreateAccount(CreateAccountEvent event) {
        mBus.post(new AccountCreatedEvent(event.getUser()));
    }

    @Subscribe
    public void onGetUserByFacebookId(ObtainUserByFacebookEvent event) {
        ArrayList<Login> logins = new ArrayList<>();
        ArrayList<Membership> groupMembership = new ArrayList<>();
        ArrayList<Activity> activities = new ArrayList<>();
        ArrayList<Goal> goals = new ArrayList<>();
        ArrayList<UserBadge> badges = new ArrayList<>();

        logins.add(new Login("37757277-697b-4662-bee7-d7d3067ac2b0", "Facebook", "1069090856450340"));
        User user = new User("Michael Casper", "Mike", 6, 0, 150, "Lincoln, Nebraska", new Date(), "Male", "iamdefinatlynotyou@gmail.com", groupMembership, logins, activities, goals, badges, "37757277-697b-4662-bee7-d7d3067ac2b0", "fakeImage.png");

        mBus.post(new ObtainedUserByFacebookEvent(user, null));
    }

    @Subscribe
    public void onObtainGroupUsersEvent(ObtainGroupUsersEvent event) {
        ArrayList<Login> logins = new ArrayList<>();
        ArrayList<Membership> groupMembership = new ArrayList<>();
        ArrayList<Activity> activities = new ArrayList<>();
        ArrayList<Goal> goals = new ArrayList<>();
        ArrayList<UserBadge> badges = new ArrayList<>();

        logins.add(new Login("37757277-697b-4662-bee7-d7d3067ac2b0", "Facebook", "1069090856450340"));
        User user = new User("Michael Casper", "Mike", 6, 0, 150, "Lincoln, Nebraska", new Date(), "Male", "iamdefinatlynotyou@gmail.com", groupMembership, logins, activities, goals, badges, "37757277-697b-4662-bee7-d7d3067ac2b0", "fakeImage.png");

        ArrayList<User> users = new ArrayList<>();
        users.add(user);

        mBus.post(new GroupUsersObtainedEvent(users));
    }
}
