package com.group7.healthtrac;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.challengeevents.ObtainUserChallengesEvent;
import com.group7.healthtrac.events.challengeevents.UserChallengesObtainedEvent;
import com.group7.healthtrac.models.Challenge;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.ChallengeAdapter;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class ViewActiveChallenges extends RoboActionBarActivity {

    @Inject private MenuService mMenuService;
    @Inject private IApiCaller mApiCaller;
    @InjectView(R.id.active_challenges_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.active_challenges_tool_bar) private Toolbar mToolbar;
    @InjectView(R.id.active_challenges_recycler) private RecyclerView mRecycler;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingScreen;
    @InjectView(R.id.error_page) private LinearLayout mErrorPage;
    @InjectView(R.id.active_challenges_list) private ListView mChallengeList;
    private User mCurrentUser;
    private List<Challenge> mAcceptedChallenges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_active_challenges);

        mCurrentUser = ((HealthTracApplication) getApplication()).getCurrentUser();
        if (mCurrentUser != null) {
            mApiCaller.setContext(this);
            mAcceptedChallenges = new ArrayList<>();

            setSupportActionBar(mToolbar);
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, this, "activeChallenges", mCurrentUser);
            displayChallengesShowcase();
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
            mApiCaller.registerObject(this);
            mApiCaller.setContext(this);
            mApiCaller.requestData(new ObtainUserChallengesEvent(mCurrentUser.getId()));
            mLoadingScreen.setVisibility(View.VISIBLE);
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

        mMenuService.handleMenuSelection(id, ViewActiveChallenges.this);

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onUserChallengesObtained(UserChallengesObtainedEvent event) {
        List<Challenge> eventChallenges = event.getChallenges();
        mAcceptedChallenges.clear();

        for (Challenge c : eventChallenges) {
            if (c.isAccepted()) {
                mAcceptedChallenges.add(c);
            }
        }

        ChallengeAdapter adapter = new ChallengeAdapter(this, mAcceptedChallenges, this, mLoadingScreen, mApiCaller);
        mChallengeList.setAdapter(adapter);

        mLoadingScreen.setVisibility(View.GONE);

        final Activity activity = this;

        mChallengeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, ViewChallengeActivity.class);
                intent.putExtra("challenge", mAcceptedChallenges.get(position));
                startActivity(intent);
            }
        });
    }


    private void displayChallengesShowcase() {
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 70)).intValue();
        lps.setMargins(0, 0, 0, margin);

        com.github.amlcurran.showcaseview.targets.Target viewTarget = new ViewTarget(R.id.active_challenges_tool_bar, this);
        ShowcaseView challengeShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(viewTarget)
                .setStyle(R.style.CustomShowcaseViewTheme)
                .setContentTitle("Active Challenges")
                .setContentText("If you have any active challenges with friend's you can find them on this page and click on them to see a comparison of how you and your friend are doing.")
                .singleShot(60)
                .build();

        challengeShowcase.setButtonPosition(lps);
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
