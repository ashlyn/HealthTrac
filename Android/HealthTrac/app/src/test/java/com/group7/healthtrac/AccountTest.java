package com.group7.healthtrac;

import android.widget.TextView;

import com.group7.healthtrac.events.accountevents.AccountCreatedEvent;
import com.group7.healthtrac.events.accountevents.CreateAccountEvent;
import com.group7.healthtrac.events.accountevents.ObtainUserByFacebookEvent;
import com.group7.healthtrac.events.accountevents.ObtainedUserByFacebookEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.Login;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.models.UserBadge;
import com.group7.healthtrac.services.testapi.MockAccountService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by Josh on 4/9/2015.
 */
@Config(emulateSdk = 16, reportSdk = 16)
@RunWith(CustomTestRunner.class)
public class AccountTest {

    private Bus bus;

    @Before
    public void setUp() {
        bus = new Bus(ThreadEnforcer.ANY);
    }

    @Test
    public void clickingCreateAccount_ShouldPostAccountEvent() {
        ArrayList<Login> logins = new ArrayList<>();
        ArrayList<Membership> groupMembership = new ArrayList<>();
        ArrayList<Activity> activities = new ArrayList<>();
        ArrayList<Goal> goals = new ArrayList<>();
        ArrayList<UserBadge> badges = new ArrayList<>();
        final AtomicBoolean testDone = new AtomicBoolean(false);

        logins.add(new Login("37757277-697b-4662-bee7-d7d3067ac2b0", "Facebook", "1069090856450340"));
        User user = new User("Michael Casper", "Mike", 6, 0, 150, "Lincoln, Nebraska", new Date(), "Male", "iamdefinatlynotyou@gmail.com", groupMembership, logins, activities, goals, badges, "37757277-697b-4662-bee7-d7d3067ac2b0", "fakeImage.png");

        bus.register(new Object() {
            @Subscribe
            public void onNewUserCreated(AccountCreatedEvent event) {
                assertEquals("Michael Casper", event.getUser().getFullName());
                testDone.set(true);
            }
        });

        bus.register(new MockAccountService(null, bus));

        bus.post(new CreateAccountEvent(user));

        while(!testDone.get());
    }

    @Test
    public void createAccountTest() {
        final AtomicBoolean testDone = new AtomicBoolean(false);
        String facebookId = "1069090856450340";

        bus.register(new Object() {
            @Subscribe
            public void onUserObtained(ObtainedUserByFacebookEvent event) {
                assertEquals("Michael Casper", event.getUser().getFullName());
                testDone.set(true);
            }
        });

        bus.register(new MockAccountService(null, bus));

        bus.post(new ObtainUserByFacebookEvent(facebookId));

        while(!testDone.get());
    }

}
