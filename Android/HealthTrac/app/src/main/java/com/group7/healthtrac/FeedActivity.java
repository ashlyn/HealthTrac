package com.group7.healthtrac;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.badgeevents.BadgeCreatedEvent;
import com.group7.healthtrac.events.badgeevents.CreateBadgeEvent;
import com.group7.healthtrac.events.groupevents.ObtainUserGroupsEvent;
import com.group7.healthtrac.events.groupevents.UserGroupsObtainedEvent;
import com.group7.healthtrac.events.moodevents.AllMoodsObtainedEvent;
import com.group7.healthtrac.events.moodevents.MoodUpdatedEvent;
import com.group7.healthtrac.events.moodevents.ObtainAllMoodsEvent;
import com.group7.healthtrac.events.moodevents.UpdateMoodEvent;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.Mood;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.models.UserBadge;
import com.group7.healthtrac.models.UserMood;
import com.group7.healthtrac.services.FeedPagerAdapter;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.MoodAdapter;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class FeedActivity extends RoboActionBarActivity {

    private static final String TAG = "FeedActivity";
    private static final int CREATE_ACCOUNT_BADGE_ID = 1;
    private User mUser;
    @Inject private IApiCaller mApiCaller;
    @InjectView(R.id.feed_tabs) private PagerSlidingTabStrip mTabs;
    @InjectView(R.id.feed_tool_bar) private Toolbar mToolbar;
    @InjectView(R.id.feed_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.feed_recycler_view) private RecyclerView mRecycler;
    @InjectView(R.id.error_page) private LinearLayout mErrorPage;
    @InjectView(R.id.update_mood_button) private FloatingActionButton mMoodButton;
    private FeedPagerAdapter mFeedPagerAdapter;
    @InjectView(R.id.feed_pager) private ViewPager mViewPager;
    private List<Mood> mMoods;
    private MoodAdapter mMoodAdapter;
    private int mSelectedMood;
    private RelativeLayout.LayoutParams mButtonPositionLowerLeft;
    private RelativeLayout.LayoutParams mButtonPositionLowerCenter;
    @Inject private MenuService mMenuService;
    private Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ((HealthTracApplication) getApplication()).setCurrentUserImage(new BitmapDrawable(getResources(), bitmap));
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, FeedActivity.this, "feeds", mUser);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            ((HealthTracApplication) getApplication()).setCurrentUserImage(errorDrawable);
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, FeedActivity.this, "feeds", mUser);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            ((HealthTracApplication) getApplication()).setCurrentUserImage(placeHolderDrawable);
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, FeedActivity.this, "feeds", mUser);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        mUser = ((HealthTracApplication) getApplication()).getCurrentUser();

        if (mUser != null) {
            // set up the action bar
            setSupportActionBar(mToolbar);
            mApiCaller.setContext(this);

            Picasso.with(this)
                    .load(mUser.getImageUrl())
                    .placeholder(R.drawable.account_and_control)
                    .error(R.drawable.account_and_control)
                    .into(mTarget);

            handleIntent(getIntent());

            createButtonPositions();
            createFirstShowcase();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void createButtonPositions() {
        mButtonPositionLowerLeft = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mButtonPositionLowerLeft.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mButtonPositionLowerLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 70)).intValue();
        mButtonPositionLowerLeft.setMargins(margin / 5, 0, 0, margin);

        mButtonPositionLowerCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mButtonPositionLowerCenter.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mButtonPositionLowerCenter.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mButtonPositionLowerCenter.setMargins(0, 0, 0, margin);
    }

    private void createFirstShowcase() {
        com.github.amlcurran.showcaseview.targets.Target viewTarget = new ViewTarget(R.id.feed_tabs, this);
        ShowcaseView welcomeView = new ShowcaseView.Builder(this, true)
                .setTarget(viewTarget)
                .setStyle(R.style.CustomShowcaseViewTheme)
                .setContentTitle("Welcome to HealthTrac!")
                .setContentText("This is the feeds page. You can find updates on what you have accomplished here. Once you join some teams, you can swipe to the right to view the activity of your teammates as well!")
                .singleShot(1)
                .build();

        welcomeView.setButtonPosition(mButtonPositionLowerCenter);
        welcomeView.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                createSecondShowcase();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }

    private void createSecondShowcase() {
        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(R.id.feed_item_focus_point, this);
        ShowcaseView secondView = new ShowcaseView.Builder(this, true)
                .setTarget(target)
                .setStyle(R.style.CustomShowcaseViewTheme)
                .setContentTitle("Feed Items")
                .setContentText("Clicking on a feed item will display more details about the event!")
                .singleShot(2)
                .build();

        secondView.setButtonPosition(mButtonPositionLowerCenter);
        secondView.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                showMoodShowcase();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });

    }

    private void showMoodShowcase() {
        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(R.id.update_mood_button, this);
        ShowcaseView moodShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(target)
                .setContentTitle("Give status updates")
                .setContentText("Press this button to record new activities or give mood updates for friends to see!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(3)
                .build();

        moodShowcase.setButtonPosition(mButtonPositionLowerLeft);
        moodShowcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                showMenuShowcase();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }

    private void showMenuShowcase() {
        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(R.id.menu_focus_point, this);
        ShowcaseView moodShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(target)
                .setContentTitle("Navigation")
                .setContentText("This button opens the menu to navigate throughout the app. You can either press it to view the" +
                        " menu or start a swipe to the right from the left edge of your screen!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(4)
                .build();

        moodShowcase.setButtonPosition(mButtonPositionLowerCenter);
        moodShowcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                showLogOutShowcase();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }

    private void showLogOutShowcase() {
        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(R.id.action_logout, this);
        ShowcaseView logOutShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(target)
                .setContentTitle("Log Out")
                .setContentText("You can log out at any time by pressing this button!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(5)
                .build();

        logOutShowcase.setButtonPosition(mButtonPositionLowerCenter);
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra("justCreated")) {
            mApiCaller.requestData(new CreateBadgeEvent(new UserBadge(CREATE_ACCOUNT_BADGE_ID, mUser.getId())));
        }

        mMoodButton.setEnabled(false);
        mApiCaller.requestData(new ObtainAllMoodsEvent());
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

    @Subscribe
    public void onBadgeObtained(BadgeCreatedEvent event) {
        Crouton.makeText(this, "Congratulations! You have earned the Join the Club badge!", CustomCroutonStyle.CONFIRM).show();
        mUser.addBadge(event.getUserBadge());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mUser != null) {
            mApiCaller.setContext(this);
            mApiCaller.registerObject(this);
            determineUsersGroups();
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

        mMenuService.handleMenuSelection(id, FeedActivity.this);

        return super.onOptionsItemSelected(item);
    }

    private void showFeed() {
        mFeedPagerAdapter = new FeedPagerAdapter(getSupportFragmentManager(), mUser);
        mViewPager.setAdapter(mFeedPagerAdapter);

        mFeedPagerAdapter.updateGroups(determineGroups());
        mTabs.setViewPager(mViewPager);
    }

    private List<Group> determineGroups() {
        List<Group> groups = mUser.getGroups();
        List<Group> actualGroups = new ArrayList<>();
        for (Group g : groups) {
            if (g.getStatusInGroup(mUser.getId()) == Membership.ADMIN || g.getStatusInGroup(mUser.getId()) == Membership.MEMBER) {
                actualGroups.add(g);
            }
        }

        return actualGroups;
    }

    private void determineUsersGroups() {
        mApiCaller.requestData(new ObtainUserGroupsEvent(mUser.getId()));
    }

    @Subscribe
    public void onMoodsObtained(AllMoodsObtainedEvent event) {
        mMoods = event.getMoods();
        mMoodAdapter = new MoodAdapter(this, mMoods);
        mMoodButton.setEnabled(true);
    }

    @Subscribe
     public void onObtainedUserGroups(UserGroupsObtainedEvent event) {
        mUser.setGroups(event.getGroups());
        ((HealthTracApplication) getApplication()).setCurrentUser(mUser);

        showFeed();
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        if (event.getCause() == ApiErrorEvent.Cause.OBTAIN) {
            mErrorPage.setVisibility(View.VISIBLE);
        }
        Crouton.clearCroutonsForActivity(this);
        Crouton.makeText(this, event.getErrorMessage(), CustomCroutonStyle.ALERT).show();
    }

    public void addActivity(View view) {
        Intent intent = new Intent(FeedActivity.this, EnterActivityActivity.class);
        startActivity(intent);
    }

    public void updateMood(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(FeedActivity.this);
        builder.setTitle("I'm Feeling...");
        builder.setSingleChoiceItems(mMoodAdapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSelectedMood = which;
            }
        });
        builder.setPositiveButton(R.string.update_mood, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mApiCaller.requestData(new UpdateMoodEvent(new UserMood(mUser.getId(), mSelectedMood + 1, new Date())));
                dialog.cancel();
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
        alert.getWindow().setLayout(1000, 1200);
    }

    @Subscribe
    public void onMoodUpdated(MoodUpdatedEvent event) {
        Crouton.makeText(this, "You have updated your mood.", CustomCroutonStyle.CONFIRM).show();
        showFeed();
    }
}
