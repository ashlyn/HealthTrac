package com.group7.healthtrac;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.astuetz.PagerSlidingTabStrip;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.accountevents.ObtainUserEvent;
import com.group7.healthtrac.events.accountevents.UserObtainedEvent;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.ProfilePagerAdapter;
import com.group7.healthtrac.services.ProfileViewPager;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class ViewProfileActivity extends RoboActionBarActivity {

    private final static String TAG = "ViewProfileActivity";
    private final static String IS_CURRENT_USER = "isCurrentUser";
    private final static String USER_ID = "userToShowId";
    @InjectView(R.id.profile_pager) private ProfileViewPager mViewPager;
    @InjectView(R.id.profile_tabs) private PagerSlidingTabStrip mTabs;
    @InjectView(R.id.view_profile_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.view_profile_tool_bar) private Toolbar mToolbar;
    @InjectView(R.id.view_profile_recycler_view) private RecyclerView mRecycler;
    @InjectView(R.id.error_page) private LinearLayout mErrorPage;
    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;
    private User mCurrentUser;
    private User mUserToDisplay;
    private boolean mIsCurrentUser;
    private ProfilePagerAdapter mProfilePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_profile);

        mCurrentUser = ((HealthTracApplication) getApplication()).getCurrentUser();

        if (mCurrentUser != null) {
            mApiCaller.setContext(this);
            setSupportActionBar(mToolbar);

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
        if (intent.hasExtra(IS_CURRENT_USER)) {
            mIsCurrentUser = true;
            mUserToDisplay = mCurrentUser;
            displayInfo();
        } else {
            mIsCurrentUser = false;
            mApiCaller.requestData(new ObtainUserEvent(intent.getStringExtra(USER_ID)));
        }

        String source;

        if (!mIsCurrentUser) {
            source = "viewOtherProfile";
        } else {
            source = "viewOwnProfile";
        }

        mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, ViewProfileActivity.this, source, mCurrentUser);
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

        mMenuService.handleMenuSelection(id, ViewProfileActivity.this);

        return super.onOptionsItemSelected(item);
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

    private void displayInfo() {
        if (mProfilePagerAdapter == null) {
            mProfilePagerAdapter = new ProfilePagerAdapter(getSupportFragmentManager(), mUserToDisplay, mCurrentUser, mIsCurrentUser);
        }

        mViewPager.setAdapter(mProfilePagerAdapter);
        mTabs.setViewPager(mViewPager);
        setTitle(mUserToDisplay.getFullName());
    }

    public ProfileViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public void onPause() {
        super.onPause();
        Crouton.clearCroutonsForActivity(ViewProfileActivity.this);

        mApiCaller.unregisterObject(this);
    }

    @Subscribe
    public void onUserObtainedEvent(UserObtainedEvent event) {
        mUserToDisplay = event.getUser();
        displayInfo();
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
