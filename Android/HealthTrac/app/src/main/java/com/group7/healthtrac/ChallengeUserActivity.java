package com.group7.healthtrac;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.accountevents.ObtainUserFriendsEvent;
import com.group7.healthtrac.events.accountevents.UserFriendsObtainedEvent;
import com.group7.healthtrac.events.challengeevents.ChallengeCreatedEvent;
import com.group7.healthtrac.events.challengeevents.CreateChallengeEvent;
import com.group7.healthtrac.models.Challenge;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.FriendAdapter;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;


public class ChallengeUserActivity extends RoboActionBarActivity {

    private final static String TAG = "ChallengeUserActivity";
    private final static String GOAL = "goal";
    @InjectView(R.id.challenge_goal_type) private TextView mGoalType;
    @InjectView(R.id.challenge_goal_value) private TextView mGoalValue;
    @InjectView(R.id.challenge_goal_time_frame) private TextView mGoalTimeFrame;
    @InjectView(R.id.challenge_friend_spinner) private Spinner mFriendSpinner;
    @InjectView(R.id.challenge_confirm) private ButtonRectangle mChallengeButton;
    @InjectView(R.id.challenge_tool_bar) private Toolbar mToolBar;
    @InjectView(R.id.challenge_recycler_view) private RecyclerView mRecycler;
    @InjectView(R.id.challenge_group_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.error_page) private LinearLayout mErrorPage;
    private Goal mGoal;
    private User mCurrentUser;
    private User mChallengedUser;
    private List<User> mFriends;
    private ArrayAdapter<User> mFriendAdapter;
    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_user);

        mCurrentUser = ((HealthTracApplication) getApplication()).getCurrentUser();
        if (mCurrentUser != null) {
            mChallengeButton.setBackgroundColor(getResources().getColor(R.color.accent));
            setSupportActionBar(mToolBar);

            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolBar, ChallengeUserActivity.this, "challengeUser", mCurrentUser);
            mApiCaller.setContext(this);

            handleIntent(getIntent());
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        // close the drawer first and if the drawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen(mRecycler)) {
            mDrawer.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private void handleIntent(Intent intent) {
        mGoal = intent.getParcelableExtra(GOAL);
        mApiCaller.requestData(new ObtainUserFriendsEvent(mCurrentUser.getId()));
        displayGoalInfo();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCurrentUser != null) {
            mApiCaller.setContext(this);
            mApiCaller.registerObject(this);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mApiCaller.unregisterObject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        mMenuService.handleMenuSelection(id, ChallengeUserActivity.this);

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onUserFriendsObtainedEvent(UserFriendsObtainedEvent event) {
        mFriends = event.getUserFriends();

        if (mFriendAdapter == null) {
            mFriendAdapter = new FriendAdapter(this, mFriends);
        }

        mFriendSpinner.setAdapter(mFriendAdapter);
        mErrorPage.setVisibility(View.GONE);
    }

    private void displayGoalInfo() {
        mGoalType.setText(Goal.GoalType.fromInt(mGoal.getType()).toString());
        mGoalValue.setText(Double.toString(mGoal.getTarget()));
        mGoalTimeFrame.setText(Goal.GoalTimeFrame.fromInt(mGoal.getTimeFrame()).toString());
    }

    public void onCancel(View view) {
        super.onBackPressed();
    }

    public void onConfirmChallenge(View view) {
        mChallengedUser = mFriends.get(mFriendSpinner.getSelectedItemPosition());
        mApiCaller.requestData(new CreateChallengeEvent(new Challenge(mCurrentUser.getId(), mGoal.getId(), mChallengedUser.getId(), false)));
    }

    @Subscribe
    public void onChallengeCreated(ChallengeCreatedEvent event) {
        Crouton.makeText(this, "You have successfully challenged " + mChallengedUser.getFullName(), CustomCroutonStyle.CONFIRM).show();
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        if (event.getCause() == ApiErrorEvent.Cause.OBTAIN) {
            mErrorPage.setVisibility(View.VISIBLE);
        }
        Crouton.clearCroutonsForActivity(this);
        Crouton.makeText(this, event.getErrorMessage(), CustomCroutonStyle.ALERT).show();
    }
}
