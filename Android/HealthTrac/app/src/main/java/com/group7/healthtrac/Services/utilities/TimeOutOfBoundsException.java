package com.group7.healthtrac.services.utilities;

/**
 * Created by Courtney on 4/24/2015.
 */
public class TimeOutOfBoundsException extends Exception {

    String mMessage;

    public TimeOutOfBoundsException(String message) {
        mMessage = message;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

}
