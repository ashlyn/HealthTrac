package com.group7.healthtrac.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.inject.Inject;
import com.group7.healthtrac.HealthTracApplication;
import com.group7.healthtrac.R;
import com.group7.healthtrac.events.foodevents.CreateFoodEvent;
import com.group7.healthtrac.events.foodevents.FoodCreatedEvent;
import com.group7.healthtrac.models.Food;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.Utility;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class EnterFoodFragment extends RoboFragment {

    private static final String TAG = "EnterFoodFragment";
    @InjectView(R.id.food_name_edit) private MaterialEditText mFoodName;
    @InjectView(R.id.food_amount_edit) private MaterialEditText mFoodAmount;
    @InjectView(R.id.food_date) private TextView mFoodDate;
    @InjectView(R.id.food_time) private TextView mFoodTime;
    @InjectView(R.id.food_spinner) private Spinner mFoodUnit;
    @InjectView(R.id.enter_food_enter) private ButtonRectangle mEnter;
    @Inject private IApiCaller mApiCaller;
    private User mUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = ((HealthTracApplication) getActivity().getApplication()).getCurrentUser();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_enter_food, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);

        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        mFoodDate.setText(Utility.displayDate(c.getTime()));
        mFoodTime.setText(hour + ":" + (minute > 10 ? minute : "0" + minute));

        mEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateFood();
            }
        });
        mEnter.setBackgroundColor(getResources().getColor(R.color.accent));
        mFoodDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectDate();
            }
        });
        mFoodTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectTime();
            }
        });
    }

    private void onCreateFood() {
        if (!checkIfFieldsAreBlank()) {
            try {
                double amount = Double.parseDouble(mFoodAmount.getText().toString());

                if (amount > 0) {
                    String foodName = mFoodName.getText().toString();
                    int unit = mFoodUnit.getSelectedItemPosition();

                    Calendar cal = Calendar.getInstance();
                    String[] dateComponents = mFoodDate.getText().toString().split("/");
                    String[] timeComponents = mFoodTime.getText().toString().split(":");
                    cal.set(Integer.parseInt(dateComponents[2]), Integer.parseInt(dateComponents[0]) - 1,
                            Integer.parseInt(dateComponents[1]), Integer.parseInt(timeComponents[0]), Integer.parseInt(timeComponents[1]));
                    Date foodEatenDate = cal.getTime();

                    Food food = new Food(foodName, amount, unit, foodEatenDate, mUser.getId());

                    mApiCaller.requestData(new CreateFoodEvent(food));
                } else {
                    mFoodAmount.setError("Food amount cannot be less than or equal to 0.");
                }
            } catch (NumberFormatException e) {
                mFoodAmount.setError("Food amount must be a valid number.");
            }
        }
    }

    private boolean checkIfFieldsAreBlank() {
        boolean fieldsAreBlank = false;
        if (mFoodName.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mFoodName.setError("Food name cannot be blank.");
        }
        if (mFoodAmount.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mFoodAmount.setError("Food amount cannot be blank.");
        }

        return fieldsAreBlank;
    }

    @Subscribe
    public void onFoodCreated(FoodCreatedEvent event) {
        Crouton.makeText(getActivity(), "Food entered", CustomCroutonStyle.CONFIRM).show();
        mFoodName.setText("");
        mFoodAmount.setText("");
        Calendar c = Calendar.getInstance();
        int minute = c.get(Calendar.MINUTE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        mFoodDate.setText(Utility.displayDate(new Date()));
        mFoodTime.setText(hour + ":" + (minute > 10 ? minute : "0" + minute));
        mFoodUnit.setSelection(0);
    }

    private void onSelectDate() {
        String[] datePieces = mFoodDate.getText().toString().split("/");

        int month = Integer.parseInt(datePieces[0]) - 1;
        int day = Integer.parseInt(datePieces[1]);
        int year = Integer.parseInt(datePieces[2]);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mFoodDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
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
                mFoodTime.setText(hourOfDay + ":" + (minute > 10 ? minute : "0" + minute));
            }
        }, hour, minute, true);
        dialog.show();
    }
}
