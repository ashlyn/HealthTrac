package com.group7.healthtrac.services.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.IEvent;
import com.squareup.otto.Bus;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Singleton
public class ApiCaller implements IApiCaller {

    private static final String TAG = "ApiCaller";
    private RestAdapter mAdapter;
    private HealthTracApi mApi;
    private Bus mBus;
    private static final String ENDPOINT = "http://se7.azurewebsites.net";
    private AccountService mAccountService;
    private GroupService mGroupService;
    private SearchService mSearchService;
    private MembershipService mMembershipService;
    private ActivityService mActivityService;
    private FeedService mFeedService;
    private BadgeService mBadgeService;
    private GoalService mGoalService;
    private MoodService mMoodService;
    private FoodService mFoodService;
    private ChallengeService mChallengeService;
    private Context mContext;

    @Inject
    public ApiCaller() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        mAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setConverter(new GsonConverter(gson))
                .build();

        mApi = mAdapter.create(HealthTracApi.class);
        mBus = getBus();
    }

    public Bus getBus() {
        if (mBus == null) {
            mBus = BusProvider.getInstance();
        }

        return mBus;
    }

    public void setBus(Bus bus) {
        mBus = bus;
    }

    public HealthTracApi getApi() {
        return mApi;
    }

    @Override
    public void requestData(IEvent event) {
        if (true || checkForInternetConnection()) {
            mBus.post(event);
        } else {
            mBus.post(new ApiErrorEvent("You must have an internet connection to use this feature.",
                    ApiErrorEvent.Cause.OBTAIN));
        }
    }

    @Override
    public void registerObject(Object object) {
        try {
            mBus.register(object);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Could not register object, already registered");
        }
    }

    @Override
    public void unregisterObject(Object object) {
        try {
            mBus.unregister(object);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Could not unregister the object, it was not registered");
        }
    }

    private void createServices() {
        mAccountService = new AccountService(getApi(), getBus());
        mGroupService = new GroupService(getApi(), getBus());
        mSearchService = new SearchService(getApi(), getBus());
        mMembershipService = new MembershipService(getApi(), getBus());
        mActivityService = new ActivityService(getApi(), getBus());
        mFeedService = new FeedService(getApi(), getBus());
        mBadgeService = new BadgeService(getApi(), getBus());
        mGoalService = new GoalService(getApi(), getBus());
        mMoodService = new MoodService(getApi(), getBus());
        mFoodService = new FoodService(getApi(), getBus());
        mChallengeService = new ChallengeService(getApi(), getBus());
    }

    @Override
    public void setUpServices() {
        if (mAccountService == null) {
            createServices();
            getBus().register(mAccountService);
            getBus().register(mGroupService);
            getBus().register(mSearchService);
            getBus().register(mMembershipService);
            getBus().register(mActivityService);
            getBus().register(mFeedService);
            getBus().register(mBadgeService);
            getBus().register(mGoalService);
            getBus().register(mMoodService);
            getBus().register(mFoodService);
            getBus().register(mChallengeService);
        }
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }

    private boolean checkForInternetConnection() {
        if (mContext != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } else {
            return false;
        }
    }
}