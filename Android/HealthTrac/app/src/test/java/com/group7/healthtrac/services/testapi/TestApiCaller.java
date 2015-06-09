package com.group7.healthtrac.services.testapi;

import android.content.Context;

import com.google.inject.Inject;
import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.services.api.BusProvider;
import com.group7.healthtrac.services.api.HealthTracApi;
import com.group7.healthtrac.services.api.IApiCaller;
import com.squareup.otto.Bus;

import java.lang.Override;

import retrofit.RestAdapter;

public class TestApiCaller implements IApiCaller {

    private MockAccountService mMockAccountService;
    private MockActivityService mMockActivityService;
    private MockGroupService mMockGroupService;
    private MockMembershipService mMockMembershipService;
    private String MOCK_ENDPOINT = "http://se7test.azurewebsites.net";
    private RestAdapter mAdapter;
    private HealthTracApi mApi;
    private Bus mBus;
    private Context mContext;

    @Inject
    public TestApiCaller() {
        mAdapter = new RestAdapter.Builder()
                .setEndpoint(MOCK_ENDPOINT)
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

    @Override
    public void requestData(IEvent event) {
        getBus().post(event);
    }

    @Override
    public void registerObject(Object object) {
        getBus().register(object);
    }

    @Override
    public void unregisterObject(Object object) {
        getBus().unregister(object);
    }

    private void createServices() {
        mMockAccountService = new MockAccountService(mApi, getBus());
        mMockGroupService = new MockGroupService(mApi, getBus());
        mMockMembershipService = new MockMembershipService(mApi, getBus());
        mMockActivityService = new MockActivityService(mApi, getBus());
    }

    @Override
    public void setUpServices() {
        if (mMockAccountService == null) {
            createServices();
            getBus().register(mMockAccountService);
            getBus().register(mMockGroupService);
            getBus().register(mMockMembershipService);
            getBus().register(mMockActivityService);
        }
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
    }
}
