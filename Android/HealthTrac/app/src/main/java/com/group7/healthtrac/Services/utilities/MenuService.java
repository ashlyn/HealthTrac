package com.group7.healthtrac.services.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.widget.RecyclerView;

import com.facebook.Session;
import com.google.inject.Inject;
import com.group7.healthtrac.HealthTracApplication;
import com.group7.healthtrac.MainActivity;
import com.group7.healthtrac.R;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.lists.MenuAdapter;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

public class MenuService {

    private String[] mTitles;
    private int[] mIconIds;

    @Inject
    public MenuService() {
        populateLists();
    }

    private void populateLists() {
        mTitles = new String[]{"View Feeds", "View Profile", "Activities", "Add a Goal", "Create a Group", "Active Challenges", "Challenges and Invites"};
        mIconIds = new int[]{R.drawable.ic_newspaper, R.drawable.ic_account_circle, R.drawable.ic_create_activity, R.drawable.add_goal, R.drawable.ic_create_group, R.drawable.challenge_black, R.drawable.trophy};
    }

    public void handleMenuSelection(int id, Activity activity) {
        switch (id) {
            case R.id.action_logout:
                displayLogOutMessage(activity);
                break;
        }
    }

    public void setUpNavigationDrawer(RecyclerView recyclerView, DrawerLayout drawer, Toolbar toolbar, Activity activity, String source, User user) {

        recyclerView.setHasFixedSize(true);
        RecyclerView.Adapter adapter = new MenuAdapter(mTitles, mIconIds, user.getFullName(), user.getEmail(), ((HealthTracApplication)activity.getApplication()).getCurrentUserImage(), source, activity, drawer);

        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);

        recyclerView.setLayoutManager(layoutManager);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private void displayLogOutMessage(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.confirm_log_out);
        builder.setMessage(R.string.log_out_message);
        builder.setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Session session = Session.getActiveSession();
                if (session != null && session.isOpened()) {
                    session.closeAndClearTokenInformation();
                } else {
                    Twitter.logOut();
                    Twitter.getSessionManager().clearActiveSession();
                }
                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.startActivity(intent);
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
    }
}
