package com.group7.healthtrac;

import android.app.Application;
import android.graphics.drawable.Drawable;

import com.google.inject.Inject;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.ApiModule;
import com.group7.healthtrac.services.api.IApiCaller;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import roboguice.RoboGuice;

public class HealthTracApplication extends Application {

    private User mCurrentUser;
    private Drawable mCurrentUserImage;
    @Inject private IApiCaller mApiCaller;

    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new ApiModule());

        RoboGuice.getInjector(this).injectMembers(this);

        TwitterAuthConfig authConfig = new TwitterAuthConfig("MdrMW9DSgKeija9L6orZKir7i", "fybs4RvphookfkuJWLBZzxATWCbaexH9kN47kKL2XwNhHV5b94");

        Fabric.with(this, new Twitter(authConfig));

        if (mApiCaller != null) {
            mApiCaller.setUpServices();
            mApiCaller.setContext(getApplicationContext());
        }
    }

    public void setCurrentUser(User user) {
        mCurrentUser = user;
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUserImage(Drawable drawable) {
        mCurrentUserImage = drawable;
    }

    public Drawable getCurrentUserImage() {
        return mCurrentUserImage;
    }
}
