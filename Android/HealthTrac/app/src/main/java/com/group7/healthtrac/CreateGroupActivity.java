package com.group7.healthtrac;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.groupevents.CreateGroupEvent;
import com.group7.healthtrac.events.groupevents.GroupCreatedEvent;
import com.group7.healthtrac.events.membershipevents.CreateMembershipEvent;
import com.group7.healthtrac.events.membershipevents.MembershipCreatedEvent;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.ImageAdapter;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class CreateGroupActivity extends RoboActionBarActivity {

    private static final String TAG = "CreateGroupActivity";
    private static final int ADMIN_STATUS = 1;
    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;
    @InjectView(R.id.create_group) private ButtonRectangle mCreateGroup;
    @InjectView(R.id.select_image) private ButtonRectangle mSelectButton;
    @InjectView(R.id.editGroupName) private MaterialEditText mGroupName;
    @InjectView(R.id.editGroupDescription) private MaterialEditText mGroupDesc;
    @InjectView(R.id.selected_image) private ImageView mSelectedImage;
    @InjectView(R.id.create_group_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.create_group_recycler_view) private RecyclerView mRecycler;
    @InjectView(R.id.create_group_tool_bar) private Toolbar mToolbar;
    private User mUser;
    private Group mGroup;
    private int mSelectedImagePosition;
    private ImageView mPreviousSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        mSelectedImagePosition = 0;
        mCreateGroup.setBackgroundColor(getResources().getColor(R.color.accent));
        mSelectButton.setBackgroundColor(getResources().getColor(R.color.accent));

        mUser = ((HealthTracApplication) getApplication()).getCurrentUser();
        if (mUser != null) {
            setSupportActionBar(mToolbar);

            mApiCaller.setContext(this);
            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, CreateGroupActivity.this, "createGroup", mUser);
            showButtonShowcaseView();
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

        mMenuService.handleMenuSelection(id, CreateGroupActivity.this);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mUser != null) {
            // register this activity on the bus so that subscribed methods can listen
            // for the appropriate event
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
        Crouton.clearCroutonsForActivity(CreateGroupActivity.this);

        // Unregister this activity whenever the activity is paused (app is paused, new activity starts, etc.)
        // so that it doesn't accidentally keep listening for events and cause multiple actions to occur
        mApiCaller.unregisterObject(this);
    }

    private void showButtonShowcaseView() {
        RelativeLayout.LayoutParams buttonPosition = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonPosition.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonPosition.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 70)).intValue();
        buttonPosition.setMargins(margin / 5, 0, 0, margin);

        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(R.id.select_image, this);
        ShowcaseView infoShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(target)
                .setContentTitle("Group Image")
                .setContentText("Press this button to select an image for your group!")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(100)
                .build();

        infoShowcase.setButtonPosition(buttonPosition);
    }

    /**
     * Whenever the create new group button is pressed, a group will be created from the provided information
     * and will create an event for the api to create the new group
     * @param view the view that called was clicked to call this method
     */
    public void createNewGroupPressed(View view) {
        Group group = createGroupFromData();
        // If the group was null, one of the required fields was left empty
        if (group != null) {
            mApiCaller.requestData(new CreateGroupEvent(group));
            mCreateGroup.setEnabled(false);
        }
    }

    @Subscribe
    public void onGroupCreated(GroupCreatedEvent event) {
        mGroup = event.getGroup();
        if (mUser.getGroups() == null) {
            mUser.setGroups(new ArrayList<Group>());
        }
        mUser.addGroup(mGroup);
        ((HealthTracApplication) getApplication()).setCurrentUser(mUser);
        mApiCaller.requestData(new CreateMembershipEvent(event.getGroup().getId(), mUser.getId(), ADMIN_STATUS));
    }

    @Subscribe
    public void onMembershipCreatedEvent(MembershipCreatedEvent event) {
        mUser.addMembership(event.getMembership());
        mGroup.addGroupMember(event.getMembership());
        viewNewGroup(mGroup);
    }

    private void viewNewGroup(Group group) {
        Intent intent = new Intent(CreateGroupActivity.this, ViewGroupActivity.class);
        intent.putExtra("groupId", group.getId());
        intent.putExtra("userStatus", ADMIN_STATUS);
        intent.putExtra("justCreated", true);
        startActivity(intent);
    }

    private Group createGroupFromData() {
        if (!checkIfFieldsAreBlank()) {
            String imageUrl = getGroupImageUrl();

            return new Group(mGroupName.getText().toString(), mGroupDesc.getText().toString(), imageUrl);
        } else {
            return null;
        }
    }

    private boolean checkIfFieldsAreBlank() {
        boolean fieldsAreBlank = false;
        if (mGroupName.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mGroupName.setError("Group name cannot be blank.");
        }
        if (mGroupDesc.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mGroupDesc.setError("Group description cannot be blank.");
        }

        return fieldsAreBlank;
    }

    private String getGroupImageUrl() {
        String imageUrl = "";
        switch (mSelectedImagePosition) {
            case 0:
                imageUrl = "http://i.imgur.com/RSJzwsM.png";
                break;
            case 1:
                imageUrl = "http://i.imgur.com/6VtVwV6.png";
                break;
            case 2:
                imageUrl = "http://i.imgur.com/SqfKgdv.png";
                break;
            case 3:
                imageUrl = "http://i.imgur.com/T9Rfozv.png";
                break;
            case 4:
                imageUrl = "http://i.imgur.com/i2JyKRx.png";
                break;
            case 5:
                imageUrl = "http://i.imgur.com/0Issqm9.png";
                break;
            case 6:
                imageUrl = "http://i.imgur.com/7P8CyBq.png";
                break;
            case 7:
                imageUrl = "http://i.imgur.com/Ebi3KnG.png";
                break;
            case 8:
                imageUrl = "http://i.imgur.com/M5VMYRw.png";
                break;
        }

        return imageUrl;
    }

    // If an api error occurs, display a message to the user stating the error
    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        Crouton.makeText(CreateGroupActivity.this, event.getErrorMessage(), CustomCroutonStyle.ALERT).show();
        mCreateGroup.setEnabled(true);
    }

    public void onSelectImage(View view) {
        GridView gridView = new GridView(CreateGroupActivity.this);
        gridView.setAdapter(new ImageAdapter(this));
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView tempImageView = (ImageView) view;

                if (mPreviousSelection != null) {
                    mPreviousSelection.setBackgroundResource(0);
                }

                mPreviousSelection = tempImageView;
                mPreviousSelection.setBackgroundResource(R.drawable.border);

                mSelectedImagePosition = position;
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        builder.setView(gridView);
        builder.setTitle(R.string.select_image);
        builder.setPositiveButton(R.string.select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mPreviousSelection != null) {
                    mSelectedImage.setImageDrawable(mPreviousSelection.getDrawable());
                }
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
    }
}
