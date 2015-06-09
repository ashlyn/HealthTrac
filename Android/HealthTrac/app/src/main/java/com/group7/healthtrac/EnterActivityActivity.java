package com.group7.healthtrac;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.astuetz.PagerSlidingTabStrip;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.ActivityPagerAdapter;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class EnterActivityActivity extends RoboActionBarActivity {

    private final static String TAG = "EnterActivityActivity";
    private final static String ACTIVITY = "activity";
    private ActivityPagerAdapter mActivityPagerAdapter;
    private int ENTER_ACTIVITY_PAGE_POSITION = 1;
    @InjectView(R.id.activity_pager) private ViewPager mViewPager;
    @InjectView(R.id.activities_tabs) private PagerSlidingTabStrip mTabs;
    @InjectView(R.id.activities_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.activities_recycler_view) private RecyclerView mRecycler;
    @InjectView(R.id.activities_tool_bar) private Toolbar mToolbar;
    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;
    private Activity mActivity;
    private RelativeLayout.LayoutParams mButtonPositionBottomCenter;
    private RelativeLayout.LayoutParams mButtonPositionBottomLeft;
    private com.github.amlcurran.showcaseview.targets.Target mTarget;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_activity);
        setSupportActionBar(mToolbar);

        mApiCaller.setContext(this);
        mUser = ((HealthTracApplication) getApplication()).getCurrentUser();
        if (mUser != null) {
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, EnterActivityActivity.this, "activities", mUser);
            handleIntent(getIntent());
            displayInitialShowcaseView();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra(ACTIVITY)) {
            mActivity = intent.getParcelableExtra(ACTIVITY);
            displayInfo(ENTER_ACTIVITY_PAGE_POSITION);
        } else {
            displayInfo(-1);
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

        mMenuService.handleMenuSelection(id, EnterActivityActivity.this);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mUser != null) {
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

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        Crouton.clearCroutonsForActivity(this);
        Crouton.makeText(this, event.getErrorMessage(), CustomCroutonStyle.ALERT).show();
    }

    private void displayInfo(int position) {
        mActivityPagerAdapter = new ActivityPagerAdapter(getSupportFragmentManager(), mActivity);

        mViewPager.setAdapter(mActivityPagerAdapter);
        mTabs.setViewPager(mViewPager);

        if (position != -1) {
            mViewPager.setCurrentItem(position);
        }
    }

    private void createButtonPositions() {
        mButtonPositionBottomCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mButtonPositionBottomCenter.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mButtonPositionBottomCenter.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 70)).intValue();
        mButtonPositionBottomCenter.setMargins(0, 0, 0, margin);

        mButtonPositionBottomLeft = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mButtonPositionBottomLeft.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mButtonPositionBottomLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        mButtonPositionBottomLeft.setMargins(margin / 5, 0, 0, margin);
    }

    private void displayInitialShowcaseView() {
        createButtonPositions();

        mTarget = new ViewTarget(R.id.activities_tabs, this);
        ShowcaseView summaryShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(mTarget)
                .setContentTitle("Today's Summary")
                .setContentText("This page displays your activity totals for today!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(30)
                .build();

        summaryShowcase.setButtonPosition(mButtonPositionBottomCenter);
        summaryShowcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                mViewPager.setCurrentItem(1, true);
                displayEnterActivityShowcase();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }

    private void displayEnterActivityShowcase() {
        ShowcaseView enterActivityShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(mTarget)
                .setContentTitle("Enter an Activity")
                .setContentText("You can enter an activity you performed on this page!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(31)
                .build();

        enterActivityShowcase.setButtonPosition(mButtonPositionBottomCenter);
        enterActivityShowcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                mViewPager.setCurrentItem(2, true);
                displayRecordActivityShowcase();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }

    private void displayRecordActivityShowcase() {
        ShowcaseView recordActivityShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(mTarget)
                .setContentTitle("Record an Activity")
                .setContentText("You can record an activity while you exercise on this page!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(32)
                .build();

        recordActivityShowcase.setButtonPosition(mButtonPositionBottomLeft);
        recordActivityShowcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                mViewPager.setCurrentItem(3, true);
                displayEnterMealShowcase();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }

    private void displayEnterMealShowcase() {
        ShowcaseView enterMealShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(mTarget)
                .setContentTitle("Enter a Meal")
                .setContentText("You can enter any meals you eat on this page!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(33)
                .build();

        enterMealShowcase.setButtonPosition(mButtonPositionBottomCenter);
        enterMealShowcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                mViewPager.setCurrentItem(0, true);
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }
}
