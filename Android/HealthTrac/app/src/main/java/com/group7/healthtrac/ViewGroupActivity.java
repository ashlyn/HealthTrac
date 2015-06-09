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

import com.astuetz.PagerSlidingTabStrip;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.badgeevents.BadgeCreatedEvent;
import com.group7.healthtrac.events.badgeevents.CreateBadgeEvent;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.models.UserBadge;
import com.group7.healthtrac.services.GroupPagerAdapter;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class ViewGroupActivity extends RoboActionBarActivity {

    private final static String TAG = "ViewGroupActivity";
    private final static int ADMIN_BADGE = 11;
    @InjectView(R.id.loading_screen) private LinearLayout mLayout;
    @InjectView(R.id.banned_layout) private LinearLayout mBannedPage;
    @InjectView(R.id.view_group_pager) private ViewPager mViewPager;
    @InjectView(R.id.group_tabs) private PagerSlidingTabStrip mTabs;
    @InjectView(R.id.view_group_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.view_group_recycler_view) private RecyclerView mRecycler;
    @InjectView(R.id.view_group_tool_bar) private Toolbar mToolbar;
    @InjectView(R.id.error_page) private LinearLayout mErrorPage;
    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;
    private GroupPagerAdapter mGroupAdapter;
    private User mCurrentUser;
    private int mGroupId;
    private int mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        mCurrentUser = ((HealthTracApplication) getApplication()).getCurrentUser();
        if (mCurrentUser != null) {
            mLayout.setVisibility(View.VISIBLE);
            setSupportActionBar(mToolbar);
            mApiCaller.setContext(this);

            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, ViewGroupActivity.this, "viewGroup", mCurrentUser);
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
        mGroupId = intent.getIntExtra("groupId", 0);
        mStatus = intent.getIntExtra("userStatus", -1);

        if (intent.hasExtra("justCreated")) {
            Crouton.makeText(ViewGroupActivity.this, "Group successfully created", CustomCroutonStyle.CONFIRM)
                    .show();
            boolean hasBadge = false;
            for (UserBadge u : mCurrentUser.getBadges()) {
                if (u.getBadgeId() == ADMIN_BADGE) {
                    hasBadge = true;
                }
            }

            if (!hasBadge) {
                Log.i(TAG, "creating badge");
                mApiCaller.requestData(new CreateBadgeEvent(new UserBadge(ADMIN_BADGE, mCurrentUser.getId())));
            }
        }

        displayGroup();
    }

    @Subscribe
    public void onBadgeCreated(BadgeCreatedEvent event) {
        Crouton.makeText(this, "Congratulations! You have obtained a new badge!", CustomCroutonStyle.CONFIRM);
        mCurrentUser.addBadge(event.getUserBadge());
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
        Crouton.clearCroutonsForActivity(ViewGroupActivity.this);

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

        mMenuService.handleMenuSelection(id, ViewGroupActivity.this);

        return super.onOptionsItemSelected(item);
    }

    private void displayGroup() {
        mLayout.setVisibility(View.GONE);
        if (mStatus == Membership.BANNED) {
            mBannedPage.setVisibility(View.VISIBLE);
        } else {
            mGroupAdapter = new GroupPagerAdapter(getSupportFragmentManager(), mStatus, mGroupId);

            mViewPager.setAdapter(mGroupAdapter);
            mTabs.setViewPager(mViewPager);
        }
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
