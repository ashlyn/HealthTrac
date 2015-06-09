package com.group7.healthtrac.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.doomonafireball.betterpickers.hmspicker.HmsPickerBuilder;
import com.doomonafireball.betterpickers.hmspicker.HmsPickerDialogFragment;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.inject.Inject;
import com.group7.healthtrac.HealthTracApplication;
import com.group7.healthtrac.R;
import com.group7.healthtrac.events.activityevents.ActivityCreatedEvent;
import com.group7.healthtrac.events.activityevents.CreateActivityEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.TimeOutOfBoundsException;
import com.group7.healthtrac.services.utilities.Utility;
import com.group7.healthtrac.services.api.IApiCaller;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class CreateActivityFragment extends RoboFragment implements HmsPickerDialogFragment.HmsPickerDialogHandler {

    private static final String TAG = "CreateActivityFragment";
    private User mUser;
    @InjectView(R.id.activity_type_spinner) private Spinner mActivityTypeSpinner;
    @InjectView(R.id.activityDate) private TextView mActivityDate;
    @InjectView(R.id.activityTime) private TextView mActivityTime;
    @InjectView(R.id.activityDuration) private TextView mActivityDuration;
    @InjectView(R.id.activityDistance) private MaterialEditText mActivityDistance;
    @InjectView(R.id.activityStepsTaken) private MaterialEditText mActivityStepsTaken;
    @InjectView(R.id.activity_other_name_layout) private LinearLayout mOtherNameLayout;
    @InjectView(R.id.activity_other_name) private MaterialEditText mActivityOtherName;
    @InjectView(R.id.create_activity_enter) private ButtonRectangle mEnter;
    @Inject private IApiCaller mApiCaller;
    private View mRootView;
    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = ((HealthTracApplication) getActivity().getApplication()).getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_create_activity, container, false);

        return mRootView;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);

        mActivityTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4) {
                    mOtherNameLayout.setVisibility(View.VISIBLE);
                } else {
                    mOtherNameLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createActivity();
            }
        });
        mEnter.setBackgroundColor(getResources().getColor(R.color.accent));
        mActivityDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDate();
            }
        });
        mActivityTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectTime();
            }
        });
        mActivityDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDuration();
            }
        });
        handleArguments();
    }

    private void handleArguments() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("activity")) {
            mActivity = args.getParcelable("activity");
            setEditTextInfo();
            disableEditTexts();
        } else {
            Calendar c = Calendar.getInstance();
            int minute = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            mActivityDate.setText(Utility.displayDate(new Date()));
            mActivityTime.setText(hour + ":" + (minute > 10 ? minute : "0" + minute));
            mActivityDuration.setText("00:00:00");
        }
    }

    private void createActivity(){
        if (mActivity == null) {
            Activity activity = generateActivity();
            if (activity != null) {
                mApiCaller.requestData(new CreateActivityEvent(activity));
            }
        } else if (!checkIfFieldsAreBlank()) {
            mActivity.setType(mActivityTypeSpinner.getSelectedItemPosition());

            mApiCaller.requestData(new CreateActivityEvent(mActivity));
        }
    }

    private Activity generateActivity(){

        Activity activity = null;
        try {
            if (!checkIfFieldsAreBlank()) {
                String[] dateComponents = mActivityDate.getText().toString().split("/");
                String[] timeComponents = mActivityTime.getText().toString().split(":");
                Calendar cal = Calendar.getInstance();

                cal.set(Integer.parseInt(dateComponents[2]), Integer.parseInt(dateComponents[0]) - 1, Integer.parseInt(dateComponents[1]), Integer.parseInt(timeComponents[0]), Integer.parseInt(timeComponents[1]));
                Date activityDate = cal.getTime();

                int durationInSeconds = Utility.parseTime(mActivityDuration.getText().toString());

                double distance = Double.parseDouble(mActivityDistance.getText().toString());
                int stepsTaken = Integer.parseInt(mActivityStepsTaken.getText().toString());

                int type = mActivityTypeSpinner.getSelectedItemPosition();
                String activityName;

                if (type == 4) {
                    activityName = mActivityOtherName.getText().toString();
                } else {
                    activityName = null;
                }

                activity = new Activity(type, durationInSeconds, mUser.getId(), activityName, activityDate, distance, stepsTaken);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Illegal argument (parsing) error");
        } catch (TimeOutOfBoundsException t) {
            Log.e(TAG, "Something went wrong with choosing a time");
            Crouton.makeText(getActivity(), t.getMessage(), CustomCroutonStyle.ALERT).show();
        }

        return activity;
    }

    private boolean checkIfFieldsAreBlank() {
        boolean fieldsAreBlank = false;
        if (mActivityDistance.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mActivityDistance.setError("Distance cannot be blank.");
        }
        if (mActivityStepsTaken.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mActivityStepsTaken.setError("Steps cannot be blank.");
        }
        if (mActivityTypeSpinner.getSelectedItemPosition() == 4
                && mActivityOtherName.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mActivityOtherName.setError("Activity name cannot be blank.");
        }

        return fieldsAreBlank;
    }

    private void setEditTextInfo() {
        mActivityTypeSpinner.setSelection(mActivity.getType());
        mActivityDistance.setText(Double.toString(Math.round(mActivity.getDistance() * 1000) / 1000.0));
        mActivityDuration.setText(Utility.displayTime(mActivity.getDuration()));
        mActivityDate.setText(Utility.displayDate(mActivity.getStartTime()));
        mActivityTime.setText(Utility.displayTime(mActivity.getStartTime()));
        mActivityStepsTaken.setText(Integer.toString(mActivity.getSteps()));
    }

    private void disableEditTexts() {
        mActivityStepsTaken.setEnabled(false);
        mActivityDistance.setEnabled(false);
        mActivityDuration.setEnabled(false);
        mActivityDate.setEnabled(false);
        mActivityTime.setEnabled(false);
    }

    @Subscribe
    public void onActivityCreated(ActivityCreatedEvent event) {
        Crouton.makeText(getActivity(), "Created activity", CustomCroutonStyle.CONFIRM).show();
        if (mActivity == null) {
            Calendar c = Calendar.getInstance();
            int minute = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            mActivityDate.setText(Utility.displayDate(new Date()));
            mActivityTime.setText(hour + ":" + (minute > 10 ? minute : "0" + minute));
            mActivityTypeSpinner.setSelection(0);
            mActivityDuration.setText("00:00:00");
            mActivityDistance.setText("");
            mActivityStepsTaken.setText("");
            mActivityOtherName.setText("");
        } else {
            getActivity().onBackPressed();
        }
    }

    private void onSelectDate() {
        String[] datePieces = mActivityDate.getText().toString().split("/");

        int month = Integer.parseInt(datePieces[0]) - 1;
        int day = Integer.parseInt(datePieces[1]);
        int year = Integer.parseInt(datePieces[2]);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mActivityDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
            }
        }, year, month, day);
        dialog.show();
    }

    private void onSelectTime() {
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);

        TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mActivityTime.setText(hourOfDay + ":" + (minute > 10 ? minute : "0" + minute));
            }
        }, hour, minute, true);
        dialog.show();
    }

    private void onSelectDuration() {
        HmsPickerBuilder hpb = new HmsPickerBuilder()
                .setFragmentManager(getActivity().getSupportFragmentManager())
                //.setStyleResId(R.style.BetterPickersDialogFragment);
                .setStyleResId(R.style.CustomBetterPickerTheme)
                .setTargetFragment(this);
        hpb.show();
    }

    @Override
    public void onDialogHmsSet(int reference, int hours, int minutes, int seconds) {
        Log.i(TAG, "minute = " + minutes);
        mActivityDuration.setText(hours + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds));
    }
}
