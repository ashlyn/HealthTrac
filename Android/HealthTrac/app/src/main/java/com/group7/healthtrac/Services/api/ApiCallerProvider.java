package com.group7.healthtrac.services.api;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Provides an instance of the IApiCaller class. This allows for ApiCaller objects
 * to be swapped out very easily for testing purposes.
 */
public class ApiCallerProvider implements Provider<IApiCaller> {

    @Inject
    public ApiCallerProvider() {

    }

    @Override
    public IApiCaller get() {
        return new ApiCaller();
    }
}
