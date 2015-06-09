package com.group7.healthtrac.services.testapi;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.group7.healthtrac.services.api.IApiCaller;

/**
 * Created by Josh on 4/9/2015.
 */
public class TestApiModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(IApiCaller.class).toProvider(TestApiCallerProvider.class);
    }
}
