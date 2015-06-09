package com.group7.healthtrac;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.challengeevents.ChallengeDeletedEvent;
import com.group7.healthtrac.events.challengeevents.ChallengeUpdatedEvent;
import com.group7.healthtrac.events.challengeevents.FriendChallengesObtainedEvent;
import com.group7.healthtrac.events.challengeevents.ObtainFriendChallengesEvent;
import com.group7.healthtrac.events.groupevents.ObtainUserInvitesEvent;
import com.group7.healthtrac.events.groupevents.UserInvitesObtainedEvent;
import com.group7.healthtrac.models.Challenge;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.ChallengeAdapter;
import com.group7.healthtrac.services.lists.GroupAdapter;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class InvitesAndChallengesActivity extends RoboActionBarActivity {

    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;
    @InjectView(R.id.user_invites_list) private ListView mInvitesList;
    @InjectView(R.id.challenges_list) private ListView mChallengesList;
    @InjectView(R.id.invites_tool_bar) private Toolbar mToolbar;
    @InjectView(R.id.invites_recycler) private RecyclerView mRecycler;
    @InjectView(R.id.invites_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingScreen;
    @InjectView(R.id.error_page) private LinearLayout mErrorPage;
    private ChallengeAdapter mChallengeAdapter;
    private User mCurrentUser;
    private List<Challenge> mGoalChallenges;
    private boolean mChallengesObtained;
    private boolean mInvitesObtained;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites_and_challenges);

        // Set up the action bar
        setSupportActionBar(mToolbar);
        mApiCaller.setContext(this);
        mGoalChallenges = new ArrayList<>();

        mCurrentUser = ((HealthTracApplication) getApplication()).getCurrentUser();

        if (mCurrentUser != null) {
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, this, "invites", mCurrentUser);

            displayChallengesShowcase();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCurrentUser != null) {
            mApiCaller.setContext(this);
            mApiCaller.registerObject(this);
            getInvitesAndChallenges();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        mApiCaller.unregisterObject(this);
        if (mChallengeAdapter != null) {
            mApiCaller.unregisterObject(mChallengeAdapter);
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

        mMenuService.handleMenuSelection(id, InvitesAndChallengesActivity.this);

        return super.onOptionsItemSelected(item);
    }

    private void displayChallengesShowcase() {
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 70)).intValue();
        lps.setMargins(0, 0, 0, margin);

        com.github.amlcurran.showcaseview.targets.Target viewTarget = new ViewTarget(R.id.focus_point, this);
        ShowcaseView challengeShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(viewTarget)
                .setStyle(R.style.CustomShowcaseViewTheme)
                .setContentTitle("Invites and Challenges")
                .setContentText("If you have any invites to groups or pending challenges from other users, you can find them here! Click on an item to be able to accent the invite or challenge.")
                .singleShot(50)
                .build();

        challengeShowcase.setButtonPosition(lps);
    }

    private void getInvitesAndChallenges() {
        mInvitesObtained = false;
        mChallengesObtained = false;
        mLoadingScreen.setVisibility(View.VISIBLE);

        mApiCaller.requestData(new ObtainUserInvitesEvent(mCurrentUser.getId()));
        mApiCaller.requestData(new ObtainFriendChallengesEvent(mCurrentUser.getId()));
    }

    @Subscribe
    public void onInvitesObtained(UserInvitesObtainedEvent event) {
        mInvitesObtained = true;
        List<Group> groups = event.getGroups();

        GroupAdapter adapter = new GroupAdapter(this, groups, this, R.id.user_invites_list, mCurrentUser);
        mInvitesList.setAdapter(adapter);

        checkIfBothObtained();
    }

    @Subscribe
    public void onChallengesObtained(FriendChallengesObtainedEvent event) {
        mChallengesObtained = true;
        List<Challenge> eventChallenges = event.getChallenges();
        mGoalChallenges.clear();

        for (Challenge c : eventChallenges) {
            if (!c.isAccepted()) {
                mGoalChallenges.add(c);
            }
        }

        mChallengeAdapter = new ChallengeAdapter(this, mGoalChallenges, this, mLoadingScreen, mApiCaller);
        mChallengesList.setAdapter(mChallengeAdapter);
        mChallengeAdapter.setClickCallback(R.id.challenges_list);
        mApiCaller.registerObject(mChallengeAdapter);

        checkIfBothObtained();
    }

    private void checkIfBothObtained() {
        if (mChallengesObtained && mInvitesObtained) {
            mLoadingScreen.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onChallengeDeleted(ChallengeDeletedEvent event) {
        getInvitesAndChallenges();
        Crouton.makeText(this, "You have successfully declined this challenge.", CustomCroutonStyle.CONFIRM).show();
    }

    @Subscribe
    public void onChallengeUpdated(ChallengeUpdatedEvent event) {
        getInvitesAndChallenges();
        Crouton.makeText(this, "You have accepted this challenge!", CustomCroutonStyle.CONFIRM).show();
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        mLoadingScreen.setVisibility(View.GONE);
        if (event.getCause() == ApiErrorEvent.Cause.OBTAIN) {
            mErrorPage.setVisibility(View.VISIBLE);
        }
        Crouton.clearCroutonsForActivity(this);
        Crouton.makeText(this, event.getErrorMessage(), CustomCroutonStyle.ALERT).show();
    }
}
