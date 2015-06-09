package com.group7.healthtrac.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.inject.Inject;
import com.group7.healthtrac.EnterActivityActivity;
import com.group7.healthtrac.HealthTracApplication;
import com.group7.healthtrac.R;
import com.group7.healthtrac.events.activityevents.ActivityClassifiedEvent;
import com.group7.healthtrac.events.activityevents.ClassifyActivityEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.utilities.Utility;
import com.group7.healthtrac.services.activityservices.GpsService;
import com.group7.healthtrac.services.activityservices.PedometerService;
import com.group7.healthtrac.services.api.IApiCaller;
import com.squareup.otto.Subscribe;

import java.text.ParseException;
import java.util.Date;

import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class RecordActivityFragment extends RoboFragment {

    private static final String TAG = "RecordActivities";
    private GpsService mGps;
    @InjectView(R.id.chronometer) private Chronometer mChronometer;
    @InjectView(R.id.stepNumber) private TextView mStepsTextView;
    @InjectView(R.id.record_button) private FloatingActionButton mRecordButton;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingPage;
    @Inject private IApiCaller mApiCaller;
    private Date mStartTime;
    private PedometerService mPedometer;
    private Activity mActivity;
    private boolean mIsRecording;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGps = new GpsService(getActivity(), getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_record_activity, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);

        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecordButtonPressed();
            }
        });
        resetStepCounter();
    }

    @Override
    public void onResume() {
        super.onResume();

        mApiCaller.setContext(getActivity());
        mApiCaller.registerObject(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        mApiCaller.unregisterObject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGps.connectApi();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGps.disconnectApi();
    }

    private boolean checkForGps() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = true;

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsEnabled = false;
        }

        return gpsEnabled;
    }

    private void startRecording() {
        if (checkForGps()) {
            mRecordButton.setIcon(R.drawable.stop);
            mIsRecording = true;
            mPedometer = new PedometerService(getActivity(), mStepsTextView);

            // starts the accelerometer to detect steps
            mPedometer.resetSteps();
            mPedometer.enableAccelerometerListening();

            // sets the start time of the activity and start updates for gps
            try {
                mStartTime = Utility.parseDateFromDisplayToUtc(new Date());
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
            }
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mGps.startUpdates();
        } else {
            showGpsMessage();
        }
    }

    private void stopRecording() {
        mRecordButton.setIcon(R.drawable.play);
        mIsRecording = false;
        mGps.stopUpdates();
        mChronometer.stop();
        mPedometer.disableAccelerometerListening();

        createActivity();
    }

    private void createActivity() {
        User user = ((HealthTracApplication) getActivity().getApplication()).getCurrentUser();

        int duration = (int) (SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000;

        mActivity = new Activity(0, duration, user.getId(), null, mStartTime, mGps.getDistance(), mPedometer.getNumSteps(), mGps.getRoutePoints());

        mApiCaller.requestData(new ClassifyActivityEvent(mActivity));
        mLoadingPage.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onActivityClassified(ActivityClassifiedEvent event) {
        mLoadingPage.setVisibility(View.GONE);
        mActivity.setType(event.getActivityType());

        Intent intent = new Intent(getActivity(), EnterActivityActivity.class);
        intent.putExtra("activity", mActivity);
        startActivity(intent);
    }

    private void resetStepCounter() {
        mStepsTextView.setText("0");
    }

    private void showGpsMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("GPS is disabled in your device and is required to record an activity." +
                " Would you like to enable it? (You will need to press your back button to return to this page)")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void onRecordButtonPressed() {
        if (mIsRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }
}
