package com.group7.healthtrac.services.testapi;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.group7.healthtrac.services.api.IApiCaller;

/**
 * Created by Josh on 4/9/2015.
 */
public class TestApiCallerProvider implements Provider<IApiCaller> {

    @Inject
    public TestApiCallerProvider() {

    }

    @Override
    public IApiCaller get() {
        return new TestApiCaller();
    }
}
