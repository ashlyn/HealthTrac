package com.group7.healthtrac.events;

public class ApiErrorEvent implements IEvent {

    public enum Cause {
        UPDATE,
        OBTAIN,
        CREATE,
        DELETE,
        SEARCH,
        INTERNET
    }

    private String mErrorMessage;
    private Cause mCause;

    public ApiErrorEvent(String errorMessage, Cause cause) {
        mErrorMessage = errorMessage;
        mCause = cause;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public Cause getCause() {
        return mCause;
    }
}
