package com.group7.healthtrac.events.activityevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Activity;

public class ActivityObtainedEvent implements IEvent {

    private Activity mActivity;
    private String mRequester;

    public ActivityObtainedEvent(Activity activity, String requester) {
        mActivity = activity;
        mRequester = requester;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public String getRequester() {
        return mRequester;
    }
}
