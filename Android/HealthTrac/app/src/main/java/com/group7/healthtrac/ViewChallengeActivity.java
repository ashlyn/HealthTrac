package com.group7.healthtrac;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.accountevents.ObtainUserEvent;
import com.group7.healthtrac.events.accountevents.UserObtainedEvent;
import com.group7.healthtrac.models.Challenge;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.ChallengePagerAdapter;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;


public class ViewChallengeActivity extends RoboActionBarActivity {

    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;
    @InjectView(R.id.view_challenge_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.view_challenge_recycler) private RecyclerView mRecycler;
    @InjectView(R.id.view_challenge_toolbar) private Toolbar mToolbar;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingScreen;
    @InjectView(R.id.error_page) private LinearLayout mErrorPage;
    @InjectView(R.id.view_challenge_tabs) private PagerSlidingTabStrip mTabs;
    @InjectView(R.id.view_challenge_pager) private ViewPager mViewPager;
    @InjectView(R.id.challenge_goal_timeframe) private TextView mGoalTimeFrame;
    @InjectView(R.id.challenge_goal_type) private TextView mGoalType;
    @InjectView(R.id.challenge_goal_value) private TextView mGoalTarget;
    private static final String TAG = "ViewChallengeActivity";
    private User mCurrentUser;
    private Challenge mChallenge;
    private Goal mGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_challenge);

        mCurrentUser = ((HealthTracApplication) getApplication()).getCurrentUser();

        if (mCurrentUser != null) {
            setSupportActionBar(mToolbar);
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, this, "viewChallenge", mCurrentUser);
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

    @Override
    public void onResume() {
        super.onResume();

        if (mCurrentUser != null) {
            mApiCaller.setContext(this);

            Log.i(TAG, "registered");
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

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        mMenuService.handleMenuSelection(id, ViewChallengeActivity.this);

        return super.onOptionsItemSelected(item);
    }

    private void handleIntent(Intent intent) {
        mChallenge = intent.getParcelableExtra("challenge");
        mGoal = mChallenge.getChallengerGoal();

        Log.i(TAG, "calling obtain user");
        mApiCaller.requestData(new ObtainUserEvent((mChallenge.getChallengerId().equals(mCurrentUser.getId())) ? mChallenge.getFriendId() : mChallenge.getChallengerId()));
    }

    @Subscribe
    public void onUserObtained(UserObtainedEvent event) {
        Log.i(TAG, "User obtained");
        ChallengePagerAdapter adapter = new ChallengePagerAdapter(getSupportFragmentManager(), mCurrentUser, event.getUser(), mChallenge);
        mViewPager.setAdapter(adapter);

        mTabs.setViewPager(mViewPager);
        setChallengeInfo();
    }

    private void setChallengeInfo() {
        mGoalTimeFrame.setText("Repetition: " + Goal.GoalTimeFrame.fromInt(mGoal.getTimeFrame()));
        mGoalType.setText("Goal type: " + Goal.GoalType.fromInt(mGoal.getType()));
        String info = "Target: ";

        switch (mGoal.getType()) {
            case 0:
                info += (double)(Math.round(mGoal.getTarget() / 6)) / 10 + " minutes";
                break;
            case 1:
                info += mGoal.getTarget() + " meters";
                break;
            case 2:
                info += mGoal.getTarget() + " steps";
                break;
        }

        mGoalTarget.setText(info);
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        Log.i(TAG, "An error occurred");
        mLoadingScreen.setVisibility(View.GONE);
        if (event.getCause() == ApiErrorEvent.Cause.OBTAIN) {
            mErrorPage.setVisibility(View.VISIBLE);
        }
        Crouton.clearCroutonsForActivity(this);
        Crouton.makeText(this, event.getErrorMessage(), CustomCroutonStyle.ALERT).show();
    }
}
