package com.group7.healthtrac.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Session;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.inject.Inject;
import com.group7.healthtrac.HealthTracApplication;
import com.group7.healthtrac.MainActivity;
import com.group7.healthtrac.R;
import com.group7.healthtrac.events.accountevents.AccountDeletedEvent;
import com.group7.healthtrac.events.accountevents.AccountUpdatedEvent;
import com.group7.healthtrac.events.accountevents.DeleteAccountEvent;
import com.group7.healthtrac.events.accountevents.UpdateAccountEvent;
import com.group7.healthtrac.events.badgeevents.ObtainUserBadgesEvent;
import com.group7.healthtrac.events.badgeevents.UserBadgesObtainedEvent;
import com.group7.healthtrac.models.Badge;
import com.group7.healthtrac.models.Login;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.Utility;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.BadgeAdapter;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class UserInfoFragment extends RoboFragment {

    private static final String TAG = "UserInfoFragment";
    private static final String IS_CURRENT_USER = "isCurrentUser";
    private static final String USER_TO_DISPLAY = "userToDisplay";
    private View mRootView;
    private User mUser;
    private List<Badge> mBadges;
    private boolean mIsCurrentUser;
    private boolean mIsEditing;
    private RelativeLayout.LayoutParams mButtonPositionBottomCenter;
    @Inject private IApiCaller mApiCaller;
    @InjectView(R.id.userFullName) private TextView mFullNameView;
    @InjectView(R.id.preferredName) private TextView mPreferredNameView;
    @InjectView(R.id.view_profile_birthday) private TextView mBirthdayView;
    @InjectView(R.id.view_profile_location) private TextView mLocationView;
    @InjectView(R.id.view_profile_gender) private TextView mGenderView;
    @InjectView(R.id.view_profile_email) private TextView mEmailView;
    @InjectView(R.id.view_profile_weight) private TextView mWeightView;
    @InjectView(R.id.view_profile_height_feet) private TextView mHeightFeetView;
    @InjectView(R.id.view_profile_height_inches) private TextView mHeightInchesView;
    @InjectView(R.id.view_profile_edit_fullname) private MaterialEditText mFullNameEdit;
    @InjectView(R.id.view_profile_edit_preferred_name) private MaterialEditText mPreferredNameEdit;
    @InjectView(R.id.view_profile_edit_birthday) private MaterialEditText mBirthdayEdit;
    @InjectView(R.id.view_profile_edit_location) private MaterialEditText mLocationEdit;
    @InjectView(R.id.view_profile_edit_gender) private MaterialEditText mGenderEdit;
    @InjectView(R.id.view_profile_edit_email) private MaterialEditText mEmailEdit;
    @InjectView(R.id.view_profile_edit_weight) private MaterialEditText mWeightEdit;
    @InjectView(R.id.view_profile_edit_height_feet) private MaterialEditText mHeightFeetEdit;
    @InjectView(R.id.view_profile_edit_height_inches) private MaterialEditText mHeightInchesEdit;
    @InjectView(R.id.view_profile_quote) private TextView mInchesMark;
    @InjectView(R.id.view_profile_apostrophe) private TextView mFeetMark;
    @InjectView(R.id.birthday) private TextView mBirthdayLabel;
    @InjectView(R.id.gender) private TextView mGenderLabel;
    @InjectView(R.id.height) private TextView mHeightLabel;
    @InjectView(R.id.weight) private TextView mWeightLabel;
    @InjectView(R.id.location) private TextView mLocationLabel;
    @InjectView(R.id.email) private TextView mEmailLabel;
    @InjectView(R.id.edit_button) private Button mEditButton;
    @InjectView(R.id.save_button) private Button mSaveButton;
    @InjectView(R.id.delete_button) private Button mDeleteButton;
    @InjectView(R.id.badge_list) private ListView mBadgeList;
    @InjectView(R.id.badge_section) private LinearLayout mBadgeSection;
    @InjectView(R.id.user_image) private ImageView mUserImage;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingLayout;
    @InjectView(R.id.info_section) private LinearLayout mInfoSection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_user_info, container, false);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);

        Bundle args = getArguments();
        mIsCurrentUser = args.getBoolean(IS_CURRENT_USER);
        mUser = args.getParcelable(USER_TO_DISPLAY);

        displayCorrectObjects(mIsCurrentUser);
    }

    @Override
    public void onResume() {
        super.onResume();

        mApiCaller.setContext(getActivity());
        mApiCaller.registerObject(this);
        mApiCaller.requestData(new ObtainUserBadgesEvent(mUser.getId()));
        //mLoadingLayout.setVisibility(View.VISIBLE);
        createButtonPositions();
        showInfoShowcaseView();
    }

    @Subscribe
    public void onBadgesObtained(UserBadgesObtainedEvent event) {
        //mBadges = event.getBadges();
        mBadges = new ArrayList<>();
        mBadges.add(new Badge(0, "Join the Club", "Created a profile!", "http://i.imgur.com/YIXDwLt.png"));
        mBadges.add(new Badge(0, "One of Us!", "Joined a group!", "http://i.imgur.com/eD27Qz1.png"));
        updateView(mUser, mBadges);
    }

    @Override
    public void onPause() {
        super.onPause();

        mApiCaller.unregisterObject(this);
    }

    private void createButtonPositions() {
        mButtonPositionBottomCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mButtonPositionBottomCenter.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mButtonPositionBottomCenter.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 70)).intValue();
        mButtonPositionBottomCenter.setMargins(margin / 5, 0, 0, margin);
    }

    private void showInfoShowcaseView() {
        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(mRootView.findViewById(R.id.user_image));
        ShowcaseView infoShowcase = new ShowcaseView.Builder(getActivity(), true)
                .setTarget(target)
                .setContentTitle("This is Your Profile")
                .setContentText("From this page, you can update your information and view earned badges! Swipe to the right to view your groups page and your goals page!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(6)
                .build();

        infoShowcase.setButtonPosition(mButtonPositionBottomCenter);
        infoShowcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                showGroupsShowcase();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }

    private void showGroupsShowcase() {
        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(R.id.profile_tabs, getActivity());
        ShowcaseView groupsShowcase = new ShowcaseView.Builder(getActivity(), true)
                .setTarget(target)
                .setContentTitle("View Your Groups")
                .setContentText("From this page, you can view all groups you are a member of or are invited to or create a new group!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(7)
                .build();

        groupsShowcase.setButtonPosition(mButtonPositionBottomCenter);
        groupsShowcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                showGoalsShowcase();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }

    private void showGoalsShowcase() {
        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(R.id.goal_focus_target, getActivity());
        ShowcaseView goalsShowcase = new ShowcaseView.Builder(getActivity(), true)
                .setTarget(target)
                .setContentTitle("View Your Goals")
                .setContentText("From this page, you can view all goals you have set or have been challenged to! You can also create a new goal or challenge a friend!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(8)
                .build();

        goalsShowcase.setButtonPosition(mButtonPositionBottomCenter);
    }

    private void displayCorrectObjects(boolean isCurrentUser) {
        Picasso.with(getActivity())
                .load(mUser.getImageUrl())
                .error(R.drawable.account_box)
                .noFade()
                .into(mUserImage);

        if (isCurrentUser) {
            mEditButton.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.VISIBLE);
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableEditMode();
                }
            });
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateProfile();
                }
            });
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAccount();
                }
            });
        }
    }

    private void enableEditMode() {
        getActivity().setTitle(R.string.title_edit_profile);
        mIsEditing = true;
        changeColorOfViews();

        mInfoSection.setBackgroundColor(getResources().getColor(R.color.default_color));
        mSaveButton.setVisibility(View.VISIBLE);
        mEditButton.setVisibility(View.GONE);
        mDeleteButton.setVisibility(View.GONE);

        mFullNameView.setVisibility(View.GONE);
        mPreferredNameView.setVisibility(View.GONE);
        mHeightFeetView.setVisibility(View.GONE);
        mHeightInchesView.setVisibility(View.GONE);
        mWeightView.setVisibility(View.GONE);
        mGenderView.setVisibility(View.GONE);
        mBirthdayView.setVisibility(View.GONE);
        mEmailView.setVisibility(View.GONE);
        mLocationView.setVisibility(View.GONE);
        mBadgeSection.setVisibility(View.GONE);

        mFullNameEdit.setVisibility(View.VISIBLE);
        mPreferredNameEdit.setVisibility(View.VISIBLE);
        mLocationEdit.setVisibility(View.VISIBLE);
        mEmailEdit.setVisibility(View.VISIBLE);
        mBirthdayEdit.setVisibility(View.VISIBLE);
        mHeightFeetEdit.setVisibility(View.VISIBLE);
        mHeightInchesEdit.setVisibility(View.VISIBLE);
        mGenderEdit.setVisibility(View.VISIBLE);
        mWeightEdit.setVisibility(View.VISIBLE);
        mFeetMark.setVisibility(View.VISIBLE);
        mInchesMark.setVisibility(View.VISIBLE);

        mFullNameEdit.setText(mUser.getFullName());
        mPreferredNameEdit.setText(mUser.getPreferredName());
        mLocationEdit.setText(mUser.getLocation());
        mEmailEdit.setText(mUser.getEmail());
        mHeightFeetEdit.setText(Integer.toString(mUser.getHeightFeet()));
        mHeightInchesEdit.setText(Integer.toString(mUser.getHeightInches()));
        mGenderEdit.setText(mUser.getGender());
        mWeightEdit.setText(Integer.toString(mUser.getWeight()));
        mBirthdayEdit.setText(Utility.displayDate(mUser.getBirthDate()));
    }

    private void updateView(User user, List<Badge> badges) {
        mSaveButton.setVisibility(View.GONE);
        mIsEditing = false;

        mInfoSection.setBackgroundColor(getResources().getColor(R.color.primary_color));
        getActivity().setTitle(user.getFullName());

        if (mIsCurrentUser) {
            mEditButton.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.VISIBLE);
        }

        changeColorOfViews();

        mFullNameView.setVisibility(View.VISIBLE);
        mPreferredNameView.setVisibility(View.VISIBLE);
        mHeightFeetView.setVisibility(View.VISIBLE);
        mHeightInchesView.setVisibility(View.VISIBLE);
        mWeightView.setVisibility(View.VISIBLE);
        mGenderView.setVisibility(View.VISIBLE);
        mBirthdayView.setVisibility(View.VISIBLE);
        mEmailView.setVisibility(View.VISIBLE);
        mLocationView.setVisibility(View.VISIBLE);
        mBadgeSection.setVisibility(View.VISIBLE);

        mFullNameEdit.setVisibility(View.GONE);
        mPreferredNameEdit.setVisibility(View.GONE);
        mLocationEdit.setVisibility(View.GONE);
        mEmailEdit.setVisibility(View.GONE);
        mBirthdayEdit.setVisibility(View.GONE);
        mHeightFeetEdit.setVisibility(View.GONE);
        mHeightInchesEdit.setVisibility(View.GONE);
        mGenderEdit.setVisibility(View.GONE);
        mWeightEdit.setVisibility(View.GONE);
        mFeetMark.setVisibility(View.GONE);
        mInchesMark.setVisibility(View.GONE);

        mFullNameView.setText(user.getFullName());
        mPreferredNameView.setText(user.getPreferredName());
        mHeightFeetView.setText(Integer.toString(user.getHeightFeet()) + "' ");
        mHeightInchesView.setText(Integer.toString(user.getHeightInches()) + "\"");
        mWeightView.setText(Integer.toString(user.getWeight()));
        mGenderView.setText(user.getGender());
        mBirthdayView.setText(user.getBirthDate().toString());
        mEmailView.setText(user.getEmail());
        mLocationView.setText(user.getLocation());
        mBirthdayView.setText(Utility.displayDate(user.getBirthDate()));

        populateBadgeList(badges);
    }

    private void populateBadgeList(List<Badge> badges) {
        ArrayAdapter<Badge> adapter = new BadgeAdapter(getActivity(), badges, R.id.badge_list,
                getActivity(), mUser.getId(), mRootView, mUser.getPreferredName());
        mBadgeList.setAdapter(adapter);
        mLoadingLayout.setVisibility(View.GONE);
    }

    private void changeColorOfViews() {
        mBirthdayLabel.setTextColor(mIsEditing ? getResources().getColor(R.color.black) : getResources().getColor(R.color.default_color));
        mGenderLabel.setTextColor(mIsEditing ? getResources().getColor(R.color.black) : getResources().getColor(R.color.default_color));
        mHeightLabel.setTextColor(mIsEditing ? getResources().getColor(R.color.black) : getResources().getColor(R.color.default_color));
        mWeightLabel.setTextColor(mIsEditing ? getResources().getColor(R.color.black) : getResources().getColor(R.color.default_color));
        mLocationLabel.setTextColor(mIsEditing ? getResources().getColor(R.color.black) : getResources().getColor(R.color.default_color));
        mEmailLabel.setTextColor(mIsEditing ? getResources().getColor(R.color.black) : getResources().getColor(R.color.default_color));
    }

    private User createUserFromInformation(List<Login> logins) {

        User user = null;

        try {

            if (validateInput()) {

                String fullName = mFullNameEdit.getText().toString();
                String preferredName = mPreferredNameEdit.getText().toString();

                int feet = Integer.parseInt(mHeightFeetEdit.getText().toString());
                int inches = Integer.parseInt(mHeightInchesEdit.getText().toString());

                int weight = Integer.parseInt(mWeightEdit.getText().toString());
                String location = mLocationEdit.getText().toString();
                Date birthday = Utility.parseDateFromDisplayToUtc(mBirthdayEdit.getText().toString());
                String gender = mGenderEdit.getText().toString();
                String email = mEmailEdit.getText().toString();

                user = new User(fullName, preferredName, feet, inches, weight, location, birthday, gender, email, mUser.getGroupMembership(), mUser.getGroups(), logins, mUser.getActivities(), mUser.getGoals(), mUser.getBadges(), mUser.getId(), mUser.getImageUrl());
            }
        } catch (NumberFormatException | ParseException e) {
            Log.e(TAG, "A parsing error occurred");
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

    private void updateProfile() {
        User user = createUserFromInformation(mUser.getLogins());

        if (user != null) {
            mUser = user;
            ((HealthTracApplication) getActivity().getApplication()).setCurrentUser(mUser);
            //mLoadingLayout.setVisibility(View.VISIBLE);
            mApiCaller.requestData(new UpdateAccountEvent(mUser));
        }
    }

    @Subscribe
    public void onProfileUpdated(AccountUpdatedEvent event) {
        getActivity().setTitle(R.string.title_activity_view_profile);
        Crouton.makeText(getActivity(), "Profile updated", CustomCroutonStyle.CONFIRM).show();
        updateView(mUser, mBadges);
    }

    public void deleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_delete_account);
        builder.setMessage(R.string.delete_account_message);
        builder.setPositiveButton(R.string.delete_account, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mApiCaller.requestData(new DeleteAccountEvent(mUser.getId()));
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Subscribe
    public void onAccountDeletedEvent(AccountDeletedEvent event) {
        Session session = Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
        }
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("justDeletedAccount", true);
        startActivity(intent);
    }
}
