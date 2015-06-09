package com.group7.healthtrac.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.group7.healthtrac.HealthTracApplication;
import com.group7.healthtrac.R;
import com.group7.healthtrac.ViewFeedItemActivity;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.activityevents.DaysActivitiesObtainedEvent;
import com.group7.healthtrac.events.activityevents.ObtainDaysActivitiesEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.ActivityAdapter;
import com.group7.healthtrac.services.utilities.Utility;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.List;

import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class ViewTodaysActivitiesFragment extends RoboFragment {

    private final static String TAG = "ViewTodaysActivities";
    @Inject private IApiCaller mApiCaller;
    @InjectView(R.id.summary_activity_list) private ListView mActivityList;
    @InjectView(R.id.summary_activity_type) private TextView mActivityType;
    @InjectView(R.id.summary_distance) private TextView mTotalDistance;
    @InjectView(R.id.summary_steps) private TextView mTotalSteps;
    @InjectView(R.id.summary_longest_activity) private TextView mLongestActivity;
    @InjectView(R.id.error_page) private LinearLayout mErrorPage;
    private User mCurrentUser;
    private ActivityAdapter mActivityAdapter;
    private List<Activity> mDaysActivities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentUser = ((HealthTracApplication) getActivity().getApplication()).getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_todays_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mApiCaller.setContext(getActivity());
        mApiCaller.registerObject(this);

        mApiCaller.requestData(new ObtainDaysActivitiesEvent(mCurrentUser.getId(), new Date()));
    }

    @Override
    public void onPause() {
        super.onPause();

        mApiCaller.unregisterObject(this);
    }

    @Subscribe
    public void onActivitiesObtained(DaysActivitiesObtainedEvent event) {
        mErrorPage.setVisibility(View.GONE);
        mDaysActivities = event.getDaysActivities();

        mActivityAdapter = new ActivityAdapter(getActivity(), mDaysActivities);
        mActivityList.setAdapter(mActivityAdapter);

        displayInfo();

        mActivityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Activity activity = mDaysActivities.get(position);

                int[] durationPieces = new int[3];

                durationPieces[0] = (int) activity.getDuration() / 3600;
                durationPieces[1] = (int) (activity.getDuration() - durationPieces[0] * 3600) / 60;
                durationPieces[2] = (int) (activity.getDuration() - (durationPieces[0] * 3600) - (durationPieces[1] * 60));

                String[] textToDisplay = new String[4];
                textToDisplay[3] = "Steps: " + activity.getSteps();

                textToDisplay[2] = getPastTenseActivityType(activity.getType(), activity.getName());

                textToDisplay[2] += (Math.round(activity.getDistance() * 100) / 100.0) + "mi in "
                        + Math.round(durationPieces[0] * 10) / 10 + (durationPieces[0] == 1 ? " hour " : " hours ")
                        + Math.round(durationPieces[1] * 10) / 10 + (durationPieces[1] == 1 ? " minute and " : " minutes and ")
                        + Math.round(durationPieces[2] * 10) / 10 + (durationPieces[2] == 1 ? " second." : " seconds.");

                textToDisplay[1] = Utility.displayDateAndTime(activity.getStartTime());
                textToDisplay[0] = mCurrentUser.getPreferredName();

                Intent intent = new Intent(getActivity(), ViewFeedItemActivity.class);
                intent.putExtra("activity", activity);
                intent.putExtra("infoToDisplay", textToDisplay);
                intent.putExtra("title", "View Activity");
                startActivity(intent);
            }
        });
    }

    private void displayInfo() {
        int totalSteps = 0;
        double totalDistance = 0.0;
        double longestActivity = -1;
        String longestActivityType = "";

        for (Activity a : mDaysActivities) {
            totalSteps += a.getSteps();
            totalDistance += a.getDistance();
            if (longestActivity < a.getDuration()) {
                longestActivity = a.getDuration();
                longestActivityType = getPastTenseActivityType(a.getType(), a.getName());
            }
        }

        mTotalSteps.setText(Integer.toString(totalSteps));
        mTotalDistance.setText(Double.toString(Math.round(totalDistance * 100) / 100.0));

        if (longestActivity != -1) {
            mLongestActivity.setText(Double.toString(Math.round(longestActivity * 100) / 6000));
        }
        mActivityType.setText(longestActivityType);
    }

    private String getPastTenseActivityType(int activityType, String activityName) {
        String pastTense = "";

        switch (activityType) {
            case 0:
                pastTense = "Ran ";
                break;
            case 1:
                pastTense = "Biked ";
                break;
            case 2:
                pastTense = "Jogged ";
                break;
            case 3:
                pastTense = "Walked ";
                break;
            case 4:
                pastTense = "Activity: " + activityName + "\nInformation: ";
                break;
        }

        return pastTense;
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        if (event.getCause() == ApiErrorEvent.Cause.OBTAIN) {
            mErrorPage.setVisibility(View.VISIBLE);
        }
    }
}
