package com.group7.healthtrac;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.accountevents.AccountCreatedEvent;
import com.group7.healthtrac.events.accountevents.CreateAccountEvent;
import com.group7.healthtrac.models.Login;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.Utility;
import com.group7.healthtrac.services.api.IApiCaller;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.services.AccountService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class CreateAccountActivity extends RoboActionBarActivity {

    private static final String TAG = "CreateAccountActivity";

    @InjectView(R.id.editName) private MaterialEditText mFullNameEdit;
    @InjectView(R.id.editPreferredName) private MaterialEditText mPreferredNameEdit;
    @InjectView(R.id.birthday) private TextView mBirthday;
    @InjectView(R.id.editLocation) private MaterialEditText mLocationEdit;
    @InjectView(R.id.editGender) private MaterialEditText mGenderEdit;
    @InjectView(R.id.editEmail) private MaterialEditText mEmailEdit;
    @InjectView(R.id.editWeight) private MaterialEditText mWeightEdit;
    @InjectView(R.id.editHeightFeet) private MaterialEditText mHeightFeetEdit;
    @InjectView(R.id.editHeightInches) private MaterialEditText mHeightInchesEdit;
    @InjectView(R.id.createAccountButton) private ButtonRectangle mCreateAccountButton;
    @InjectView(R.id.cancelButton) private ButtonRectangle mCancel;
    @InjectView(R.id.create_account_tool_bar) private Toolbar mToolBar;
    @Inject private IApiCaller mApiCaller;
    private String mUserId;
    private String mImageUrl;
    private String mProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Log.i(TAG, "created createAccountActivity");
        setSupportActionBar(mToolBar);

        mApiCaller.setContext(this);
        mCreateAccountButton.setBackgroundColor(getResources().getColor(R.color.accent));
        mCancel.setBackgroundColor(getResources().getColor(R.color.accent));

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        mProvider = intent.getStringExtra("loginProvider");

        if (mProvider.equals("Twitter")) {
            getTwitterValues();
        } else {
            if (Session.getActiveSession() != null) {
                getFacebookValues(Session.getActiveSession(), Session.getActiveSession().getState());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_account, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        mApiCaller.setContext(this);
        mApiCaller.registerObject(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Crouton.clearCroutonsForActivity(CreateAccountActivity.this);

        mApiCaller.unregisterObject(this);
    }

    private void getFacebookValues(Session session, SessionState state) {

        if (state.isOpened()) {
            // Request user data and show the results
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        try {
                            mUserId = user.getId();
                            mImageUrl = "https://graph.facebook.com/" + mUserId + "/picture?type=large";
                            mFullNameEdit.setText(user.getName());
                            mPreferredNameEdit.setText(user.getFirstName());
                            if (user.getBirthday() != null && !user.getBirthday().equals("")) {
                                mBirthday.setText(user.getBirthday());
                            } else {
                                mBirthday.setText(Utility.displayDate(new Date()));
                            }
                            if (user.getLocation() != null) {
                                mLocationEdit.setText(user.getLocation().getName());
                            }
                            if (user.asMap().get("gender") != null) {
                                mGenderEdit.setText(user.asMap().get("gender").toString());
                            }
                            if (user.asMap().get("email") != null) {
                                mEmailEdit.setText(user.asMap().get("email").toString());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }
            }).executeAsync();
        }
    }

    private void getTwitterValues() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();

        AccountService accountService = Twitter.getApiClient().getAccountService();
        accountService.verifyCredentials(false, false, new Callback<com.twitter.sdk.android.core.models.User>() {
            @Override
            public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                mFullNameEdit.setText(userResult.data.name);
                mLocationEdit.setText(userResult.data.location);
                mImageUrl = userResult.data.profileImageUrl;
                mBirthday.setText(Utility.displayDate(new Date()));
            }

            @Override
            public void failure(TwitterException e) {
                // can't populate data
                if(e != null && e.getMessage() != null) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
        mUserId = Long.toString(session.getUserId());
    }

    @Override
    public void onBackPressed() {

        // Log the user out and return them to the main screen since they
        // did not create an account
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            session.closeAndClearTokenInformation();
        } else {
            Twitter.logOut();
            Twitter.getSessionManager().clearActiveSession();
        }
        super.onBackPressed();
    }

    /**
     * Returns the user to the home screen and logs them out of facebook since they
     * did not want to create an account
     * @param view The view calling this method
     */
    public void onCancel(View view) {
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            session.closeAndClearTokenInformation();
        } else {
            Twitter.logOut();
            Twitter.getSessionManager().clearActiveSession();
        }
        super.onBackPressed();
    }

    @Subscribe
    public void onAccountCreated(AccountCreatedEvent event) {
        Intent intent = new Intent(this, FeedActivity.class);
        ((HealthTracApplication) getApplication()).setCurrentUser(event.getUser());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("justCreated", true);
        startActivity(intent);
    }

    /**
     * Creates a new account using the user's information
     * @param view The view calling this method
     */
    public void onCreateAccountPressed(View view) {
        User user = createAccountFromDetails();

        if (user != null) {
            mApiCaller.requestData(new CreateAccountEvent(user));
            mCreateAccountButton.setEnabled(false);
        }
    }

    private User createAccountFromDetails() {
        User user = null;
        try {
            if (validateInput()) {

                String fullName = mFullNameEdit.getText().toString();
                String preferredName = mPreferredNameEdit.getText().toString();
                int feet = Integer.parseInt(mHeightFeetEdit.getText().toString());
                int inches = Integer.parseInt(mHeightInchesEdit.getText().toString());
                int weight = Integer.parseInt(mWeightEdit.getText().toString());
                String location = mLocationEdit.getText().toString();
                if (location.startsWith("(")) {
                    location = location.substring(1);
                }
                if (location.endsWith(")")) {
                    location = location.substring(0, location.length() - 1);
                }
                Date birthday = Utility.parseDateFromDisplayToUtc(mBirthday.getText().toString());
                String gender = mGenderEdit.getText().toString();
                String email = mEmailEdit.getText().toString();

                List<Login> logins = new ArrayList<>();
                Login login = new Login("test", mProvider, mUserId);
                logins.add(login);

                user = new User(fullName, preferredName, feet, inches, weight, location,
                        birthday, gender, email, logins, mImageUrl);
            }
        } catch (NumberFormatException | ParseException e) {
            Log.e(TAG, "A parse error has occurred when creating the account");
        }

        return user;
    }

    private boolean validateInput() {
        boolean isValid = !checkIfFieldsAreBlank();

        if (isValid) {
            if (!mEmailEdit.getText().toString().contains("@")) {
                isValid = false;
                mEmailEdit.setError("Email is invalid.");
            }
            String[] locationPieces = mLocationEdit.getText().toString().split(",");
            if (locationPieces.length != 2) {
                isValid = false;
                mLocationEdit.setError("Location is invalid.");
            }
            try {
                int weight = Integer.parseInt(mWeightEdit.getText().toString());
                if (weight <= 0) {
                    isValid = false;
                    mWeightEdit.setError("Weight cannot be less than 1.");
                }
            } catch (NumberFormatException e) {
                isValid = false;
                mWeightEdit.setError("Weight must be an integer.");
            }
            try {
                int feet = Integer.parseInt(mHeightFeetEdit.getText().toString());
                int inches = Integer.parseInt(mHeightInchesEdit.getText().toString());

                if (inches < 0 || inches > 11) {
                    isValid = false;
                    mHeightInchesEdit.setError("Inches must be between 0 and 12 inclusive.");
                }
            } catch (NumberFormatException e) {
                isValid = false;
                mHeightFeetEdit.setError("Error.");
            }
        }

        return isValid;
    }

    private boolean checkIfFieldsAreBlank() {
        boolean fieldsAreBlank = false;
        if (mFullNameEdit.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mFullNameEdit.setError("Full name cannot be blank.");
        }
        if (mPreferredNameEdit.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mPreferredNameEdit.setError("Preferred name cannot be blank.");
        }
        if (mLocationEdit.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mLocationEdit.setError("Location cannot be blank.");
        }
        if (mGenderEdit.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mGenderEdit.setError("Gender cannot be blank.");
        }
        if (mHeightFeetEdit.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mHeightFeetEdit.setError("Blank.");
        }
        if (mHeightInchesEdit.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mHeightInchesEdit.setError("Blank.");
        }
        if (mWeightEdit.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mWeightEdit.setError("Weight cannot be blank.");
        }
        if (mEmailEdit.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mEmailEdit.setError("Email cannot be blank.");
        }

        return fieldsAreBlank;
    }

    public void onSelectDate(View view) {
        String[] datePieces = mBirthday.getText().toString().split("/");

        int month = Integer.parseInt(datePieces[0]) - 1;
        int day = Integer.parseInt(datePieces[1]);
        int year = Integer.parseInt(datePieces[2]);
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mBirthday.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
            }
        }, year, month, day);
        dialog.show();
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        Crouton.makeText(CreateAccountActivity.this, event.getErrorMessage(), CustomCroutonStyle.ALERT).show();
        mCreateAccountButton.setEnabled(true);
    }
}
