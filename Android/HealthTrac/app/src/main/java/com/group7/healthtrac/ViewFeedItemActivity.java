package com.group7.healthtrac;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.inject.Inject;
import com.group7.healthtrac.events.accountevents.ObtainUserFriendsEvent;
import com.group7.healthtrac.events.accountevents.UserFriendsObtainedEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.Badge;
import com.group7.healthtrac.models.GeoPoint;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.Mood;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.UserAdapter;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class ViewFeedItemActivity extends RoboActionBarActivity {

    private final static String ACTIVITY = "activity";
    private final static String BADGE = "badge";
    private final static String MOOD = "mood";
    private final static String GOAL = "goal";
    private final static String USER_ID = "userId";
    private final static String INFO_TO_DISPLAY = "infoToDisplay";
    private final static String TAG = "ViewFeedItemActivity";
    private final static String TITLE = "title";
    @InjectView(R.id.view_feed_item_map_layout) private LinearLayout mMapLayout;
    @InjectView(R.id.view_feed_item_user_name) private TextView mName;
    @InjectView(R.id.view_feed_item_date) private TextView mDate;
    @InjectView(R.id.view_feed_item_primary_info) private TextView mPrimaryInfo;
    @InjectView(R.id.view_feed_item_secondary_info) private TextView mSecondaryInfo;
    @InjectView(R.id.view_feed_item_tertiary_info) private TextView mTertiaryInfo;
    @InjectView(R.id.no_friends_label) private TextView mNoFriendsLabel;
    @InjectView(R.id.view_feed_item_friends_list) private ListView mFriendsList;
    @InjectView(R.id.view_feed_item_friends_section) private LinearLayout mFriendsListSection;
    @InjectView(R.id.view_feed_item_image) private ImageView mImageView;
    @InjectView(R.id.view_feed_item_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.view_feed_item_tool_bar) private Toolbar mToolbar;
    @InjectView(R.id.view_feed_item_recycler_view) private RecyclerView mRecycler;
    @InjectView(R.id.view_feed_item_pie_chart) private PieChart mGoalPieChart;
    @InjectView(R.id.bottom_divider) private View mBottomDivider;
    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;
    private ArrayAdapter mFriendAdapter;
    private Badge mBadge;
    private User mCurrentUser;
    private Activity mActivity;
    private Goal mGoal;
    private String[] mInfoToDisplay;
    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_feed_item);

        mCurrentUser = ((HealthTracApplication) getApplication()).getCurrentUser();

        if (mCurrentUser != null) {
            setSupportActionBar(mToolbar);

            mApiCaller.setContext(this);
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, ViewFeedItemActivity.this, "feedItem", mCurrentUser);
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
        if (intent.hasExtra(ACTIVITY)) {
            mActivity = intent.getParcelableExtra(ACTIVITY);
            if (mActivity.getRoutePoints() != null && !mActivity.getRoutePoints().isEmpty()) {
                mMapLayout.setVisibility(View.VISIBLE);
                mBottomDivider.setVisibility(View.VISIBLE);
                initializeMap();
            }
        } else if (intent.hasExtra(BADGE)) {
            // obtain the id of the user whose friends need to be displayed from the intent
            String userId = intent.getStringExtra(USER_ID);

            // obtain the badge whose information is to be displayed from the intent
            mBadge = intent.getParcelableExtra(BADGE);

            // set the image of the badge to the correct image
            Picasso.with(this)
                    .load(mBadge.getImageUrl())
                    .error(R.drawable.checkmark_40px)
                    .into(mImageView);

            mImageView.setVisibility(View.VISIBLE);
            mBottomDivider.setVisibility(View.VISIBLE);

            mApiCaller.requestData(new ObtainUserFriendsEvent(userId));
        } else if (intent.hasExtra(MOOD)) {
            Mood mood = intent.getParcelableExtra(MOOD);
            Picasso.with(this)
                    .load(mood.getImageUrl())
                    .error(R.drawable.healthy_heart)
                    .into(mImageView);
            mImageView.setVisibility(View.VISIBLE);
        } else if (intent.hasExtra(GOAL)) {
            mGoal = intent.getParcelableExtra(GOAL);
            mBottomDivider.setVisibility(View.VISIBLE);
            updateGraph();
        }
        mInfoToDisplay = intent.getStringArrayExtra(INFO_TO_DISPLAY);
        setTitle(intent.getStringExtra(TITLE));
        displayInfo();
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

        mMenuService.handleMenuSelection(id, ViewFeedItemActivity.this);

        return super.onOptionsItemSelected(item);
    }

    private void initializeMap() {
        if (mGoogleMap == null) {
            mGoogleMap = ((MapFragment) getFragmentManager()
                    .findFragmentById(R.id.view_feed_item_map))
                    .getMap();
        }
        if (mGoogleMap == null) {
            Crouton.makeText(this, "Could not create map.", CustomCroutonStyle.ALERT).show();
        } else {
            mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    loadMap();
                }
            });
        }
    }

    private void loadMap() {
        try {
            PolylineOptions options = new PolylineOptions();

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (GeoPoint p : mActivity.getRoutePoints()) {
                options.add(p.convertToLatLng());
                builder.include(p.convertToLatLng());
            }

            options.width(5)
                    .color(Color.CYAN);
            LatLngBounds bounds = builder.build();

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));

            mGoogleMap.addPolyline(options);
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void displayInfo() {
        switch (mInfoToDisplay.length) {
            case 5:
                mTertiaryInfo.setText(mInfoToDisplay[4]);
                mTertiaryInfo.setVisibility(View.VISIBLE);
            case 4:
                mSecondaryInfo.setText(mInfoToDisplay[3]);
                mSecondaryInfo.setVisibility(View.VISIBLE);
            case 3:
                mPrimaryInfo.setText(mInfoToDisplay[2]);
                mDate.setText(mInfoToDisplay[1].replace(" ", "\n"));
                mName.setText(mInfoToDisplay[0]);
                break;
        }
    }

    @Subscribe
    public void onUserFriendsObtained(UserFriendsObtainedEvent event) {
        List<User> friendsWithBadge = new ArrayList<>();

        for (User u : event.getUserFriends()) {
            if (u.hasBadge(mBadge.getId())) {
                friendsWithBadge.add(u);
            }
        }

        updateFriendsList(friendsWithBadge);
    }

    private void updateFriendsList(List<User> friendsWithBadge) {
        mFriendAdapter = new UserAdapter(this, friendsWithBadge, this, R.id.view_feed_item_friends_list, mCurrentUser, -1, mApiCaller);

        mFriendsList.setAdapter(mFriendAdapter);
        mFriendsListSection.setVisibility(View.VISIBLE);
        mNoFriendsLabel.setVisibility(friendsWithBadge.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void updateGraph() {
        mGoalPieChart.setVisibility(View.VISIBLE);
        if (mGoal.getProgress() < 1) {
            mGoalPieChart.addPieSlice(new PieModel((float) mGoal.getProgress() * 100, getResources().getColor(R.color.secondary_accent)));
            mGoalPieChart.addPieSlice(new PieModel((float) ((100 - (mGoal.getProgress() * 100))), Color.LTGRAY));
        } else {
            mGoalPieChart.addPieSlice(new PieModel((float)mGoal.getProgress(), Color.parseColor("#008725")));
        }

        switch (mGoal.getType()) {
            case 0:
                mGoalPieChart.setInnerValueString((double) (Math.round(mGoal.getTarget() * mGoal.getProgress() * 100 / 6)) / 1000.0 + " minutes");
                break;
            case 1:
                mGoalPieChart.setInnerValueString(Math.round(mGoal.getTarget() * mGoal.getProgress() * 100.0) / 100.0 + " miles");
                break;
            case 2:
                mGoalPieChart.setInnerValueString(Math.round(mGoal.getTarget() * mGoal.getProgress() * 100.0) / 100.0 + " steps");
                break;
        }

        mGoalPieChart.startAnimation();
    }
}
