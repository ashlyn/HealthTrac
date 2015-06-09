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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.membershipevents.CreateMembershipEvent;
import com.group7.healthtrac.events.membershipevents.MembershipCreatedEvent;
import com.group7.healthtrac.events.membershipevents.MembershipUpdatedEvent;
import com.group7.healthtrac.events.membershipevents.UpdateMembershipEvent;
import com.group7.healthtrac.events.searchevents.CompleteSearchEvent;
import com.group7.healthtrac.events.searchevents.SearchCompletedEvent;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.UserAdapter;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class InviteUserActivity extends RoboActionBarActivity {

    private final static String TAG = "InviteUserActivity";
    private final static String GROUP = "group";
    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;
    @InjectView(R.id.invite_user_list) private ListView mUserList;
    @InjectView(R.id.invite_search_bar) private EditText mSearchBar;
    @InjectView(R.id.invite_search) private ButtonRectangle mSearchButton;
    @InjectView(R.id.invite_search_progress_bar) private ProgressBar mSearchProgress;
    @InjectView(R.id.invite_invite_progress_bar) private ProgressBar mInviteProgress;
    @InjectView(R.id.invite_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.invite_recycler_view) private RecyclerView mRecycler;
    @InjectView(R.id.invite_tool_bar) private Toolbar mToolbar;
    private UserAdapter mUserAdapter;
    private Group mGroup;
    private User mCurrentUser;
    private List<User> mUsers;
    private int mUserPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_user);

        mCurrentUser = ((HealthTracApplication) getApplication()).getCurrentUser();
        if (mCurrentUser != null) {
            mUsers = new ArrayList<>();
            mSearchButton.setBackgroundColor(getResources().getColor(R.color.accent));
            setSupportActionBar(mToolbar);

            mApiCaller.setContext(this);
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, InviteUserActivity.this, "inviteUser", mCurrentUser);
            handleIntent(getIntent());
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void handleIntent(Intent intent) {
        mGroup = intent.getParcelableExtra(GROUP);
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

        mMenuService.handleMenuSelection(id, InviteUserActivity.this);

        return super.onOptionsItemSelected(item);
    }

    public void onSearchClicked(View view) {
        if (mSearchBar.getText().toString().equals("")) {
            Crouton.makeText(this, "The search field cannot be blank", CustomCroutonStyle.ALERT).show();
        } else {
            mApiCaller.requestData(new CompleteSearchEvent(mSearchBar.getText().toString()));
            mSearchButton.setEnabled(false);
            mSearchProgress.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mSearchBar.getWindowToken(), 0);
        }
    }

    @Subscribe
    public void onSearchCompleted(SearchCompletedEvent event) {
        mUsers.clear();
        mSearchButton.setEnabled(true);
        mSearchProgress.setVisibility(View.GONE);
        List<User> resultingUsers = event.getUsers();
        int statusInGroup;

        for (User u : resultingUsers) {
            statusInGroup = mGroup.getStatusInGroup(u.getId());
            if (statusInGroup != Membership.BANNED && statusInGroup != Membership.ADMIN
                    && statusInGroup != Membership.INVITED && statusInGroup != Membership.MEMBER) {
                mUsers.add(u);
            }
        }

        updateList();
    }

    private void updateList() {
        if (mUserAdapter == null) {
            mUserAdapter = new UserAdapter(this, mUsers, this, R.id.invite_user_list, mCurrentUser, Membership.MEMBER, mApiCaller);
            mUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    view.setSelected(true);
                    inviteUser(position);
                }
            });
        }

        mUserAdapter.notifyDataSetChanged();
        mUserList.setAdapter(mUserAdapter);
    }

    private void inviteUser(int position) {
        mUserPosition = position;
        mInviteProgress.setVisibility(View.VISIBLE);
        mUserList.setEnabled(false);
        Membership membership = mGroup.getMembershipOfUser(mUsers.get(position).getId());
        if (membership == null) {
            mApiCaller.requestData(new CreateMembershipEvent(mGroup.getId(), mUsers.get(position).getId(), Membership.INVITED));
        } else {
            membership.setStatus(Membership.INVITED);
            mApiCaller.requestData(new UpdateMembershipEvent(membership, "invite"));
        }
    }

    @Subscribe
    public void onMembershipUpdated(MembershipUpdatedEvent event) {
        updateScreen();
    }

    @Subscribe
    public void onMembershipCreated(MembershipCreatedEvent event) {
        updateScreen();
        mGroup.addGroupMember(event.getMembership());
    }

    private void updateScreen() {
        Crouton.makeText(this, "Invited " + mUsers.get(mUserPosition).getFullName(), CustomCroutonStyle.CONFIRM).show();
        mSearchBar.setText("");
        mInviteProgress.setVisibility(View.GONE);
        mUserList.setEnabled(true);
        mUsers.remove(mUserPosition);
        updateList();
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        Crouton.makeText(this, event.getErrorMessage(), CustomCroutonStyle.ALERT).show();
        mSearchButton.setEnabled(true);
        mUserList.setEnabled(true);
        mInviteProgress.setVisibility(View.GONE);
        mSearchProgress.setVisibility(View.GONE);
    }
}
