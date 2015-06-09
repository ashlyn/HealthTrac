package com.group7.healthtrac.events.activityevents;

import com.group7.healthtrac.events.IEvent;
import com.group7.healthtrac.models.Activity;

/**
 * Created by Mike C on 4/7/2015.
 */
public class ClassifyActivityEvent implements IEvent {

    private Activity mActivity;

    public ClassifyActivityEvent(Activity activity) {
        mActivity = activity;
    }

    public Activity getActivity() {
        return mActivity;
    }
}
