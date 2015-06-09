package com.group7.healthtrac;

import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.Login;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.models.UserBadge;
import com.group7.healthtrac.services.testapi.TestApiModule;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.TestLifecycleApplication;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import roboguice.RoboGuice;

/**
 * Created by Josh on 4/9/2015.
 */
public class TestHealthTracApplication extends HealthTracApplication implements TestLifecycleApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        TestApiModule module = new TestApiModule();
        RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(this), module);
    }

    @Override
    public User getCurrentUser() {
        ArrayList<Login> logins = new ArrayList<>();
        ArrayList<Membership> groupMembership = new ArrayList<>();
        ArrayList<Activity> activities = new ArrayList<>();
        ArrayList<Goal> goals = new ArrayList<>();
        ArrayList<UserBadge> badges = new ArrayList<>();

        logins.add(new Login("37757277-697b-4662-bee7-d7d3067ac2b0", "Facebook", "1069090856450340"));

        return new User("Michael Casper", "Mike", 6, 0, 150, "Lincoln, Nebraska", new Date(), "Male", "iamdefinatlynotyou@gmail.com", groupMembership, logins, activities, goals, badges, "37757277-697b-4662-bee7-d7d3067ac2b0", "fakeImage.png");
    }

    @Override
    public void setCurrentUser(User user) {

    }

    @Override
    public void beforeTest(Method method) {

    }

    @Override
    public void prepareTest(Object test) {
        TestHealthTracApplication testApp = (TestHealthTracApplication) Robolectric.application;
        RoboGuice.getOrCreateBaseApplicationInjector(testApp, RoboGuice.DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(testApp), new TestApiModule());
        RoboGuice.getInjector(testApp).injectMembers(test);
    }

    @Override
    public void afterTest(Method method) {

    }
}
