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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.searchevents.CompleteSearchEvent;
import com.group7.healthtrac.events.searchevents.SearchCompletedEvent;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.GroupAdapter;
import com.group7.healthtrac.services.lists.UserAdapter;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;


public class SearchResultsActivity extends RoboActionBarActivity {

    private final static String TAG = "SearchResultsActivity";
    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;
    @InjectView(R.id.search_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.search_tool_bar) private Toolbar mToolbar;
    @InjectView(R.id.search_recycler_view) private RecyclerView mRecycler;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingScreen;
    @InjectView(R.id.error_page) private LinearLayout mErrorPage;
    private ArrayAdapter<Group> mGroupAdapter;
    private ArrayAdapter<User> mUserAdapter;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        mUser = ((HealthTracApplication) getApplication()).getCurrentUser();
        if (mUser != null) {
            setSupportActionBar(mToolbar);

            mApiCaller.setContext(this);
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, SearchResultsActivity.this, "searchResults", mUser);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
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

        mMenuService.handleMenuSelection(id, SearchResultsActivity.this);

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
        Crouton.clearCroutonsForActivity(SearchResultsActivity.this);

        mApiCaller.unregisterObject(this);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mLoadingScreen.setVisibility(View.VISIBLE);
            String query = intent.getStringExtra(SearchManager.QUERY);

            mApiCaller.requestData(new CompleteSearchEvent(query));
        }
    }

    @Subscribe
    public void onSearchCompleted(SearchCompletedEvent event) {
        populateResults(event.getGroups(), event.getUsers());
    }

    private void populateResults(List<Group> groups, List<User> users) {
        mGroupAdapter = new GroupAdapter(SearchResultsActivity.this, groups, this, R.id.search_group_list, mUser);
        ListView groupList = (ListView) findViewById(R.id.search_group_list);
        groupList.setAdapter(mGroupAdapter);

        mUserAdapter = new UserAdapter(SearchResultsActivity.this, users, this, R.id.search_user_list, mUser, -1, mApiCaller);
        ListView userList = (ListView) findViewById(R.id.search_user_list);
        userList.setAdapter(mUserAdapter);

        mLoadingScreen.setVisibility(View.GONE);
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
