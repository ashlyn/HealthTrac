package com.group7.healthtrac.events.activityevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Activity;

/**
 * Created by Mike C on 3/9/2015.
 */
public class ActivityCreatedEvent implements IEvent {

    private Activity mActivity;

    public ActivityCreatedEvent(Activity activity) {
        mActivity = activity;
    }

    public Activity getActivity() {
        return mActivity;
    }
}
