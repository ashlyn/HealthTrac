package com.group7.healthtrac.services.api;

import com.google.inject.Binder;
import com.google.inject.Module;

public class ApiModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(IApiCaller.class).toProvider(ApiCallerProvider.class);
    }
}
