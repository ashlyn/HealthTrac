package com.group7.healthtrac.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.inject.Inject;
import com.group7.healthtrac.CreateGroupActivity;
import com.group7.healthtrac.R;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.groupevents.ObtainUserGroupsEvent;
import com.group7.healthtrac.events.groupevents.UserGroupsObtainedEvent;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.GroupAdapter;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class UserGroupFragment extends RoboFragment {

    private static final String IS_CURRENT_USER = "isCurrentUser";
    private static final String USER_TO_DISPLAY = "userToDisplay";
    private static final String CURRENT_USER = "currentUser";
    private static final String TAG = "UserGroupFragment";
    private View mRootView;
    @Inject private IApiCaller mApiCaller;
    @InjectView(R.id.add_group) private FloatingActionButton mAddGroupButton;
    @InjectView(R.id.groupListView) private  ListView mGroupListView;
    @InjectView(R.id.user_group_list_title) private TextView mListTitle;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingLayout;
    private User mCurrentUser;
    private User mUserToDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_user_group, container, false);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);

        Bundle args = getArguments();
        mUserToDisplay = args.getParcelable(USER_TO_DISPLAY);
        mCurrentUser = args.getParcelable(CURRENT_USER);
        boolean isCurrentUser = args.getBoolean(IS_CURRENT_USER);

        mListTitle.setText(isCurrentUser ? getString(R.string.user_group_current) : getString(R.string.user_group_other_person_current));

        displayCorrectObjects(isCurrentUser);
    }

    @Override
    public void onResume() {
        super.onResume();

        mApiCaller.setContext(getActivity());
        mApiCaller.registerObject(this);
        mApiCaller.requestData(new ObtainUserGroupsEvent(mUserToDisplay.getId()));
        //mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mApiCaller.unregisterObject(this);
    }

    private void displayCorrectObjects(boolean isCurrentUser) {
        if (isCurrentUser) {
            mAddGroupButton.setVisibility(View.VISIBLE);
            mAddGroupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent groupIntent = new Intent(getActivity(), CreateGroupActivity.class);
                    startActivity(groupIntent);
                }
            });
        }
    }

    private void updateGroupLists(List<Group> userGroups) {
        List<Group> currentGroups = new ArrayList<>();
        int statusInGroup;

        for (Group g : userGroups) {
            statusInGroup = g.getStatusInGroup(mUserToDisplay.getId());

            if (statusInGroup == Membership.MEMBER || statusInGroup == Membership.ADMIN) {
                currentGroups.add(g);
            }
        }

        ArrayAdapter<Group> currentGroupsAdapter = new GroupAdapter(mRootView.getContext(), currentGroups, getActivity(), R.id.groupListView, mCurrentUser);
        mGroupListView.setAdapter(currentGroupsAdapter);

        mLoadingLayout.setVisibility(View.GONE);
    }

    @Subscribe
    public void onUserGroupsObtained(UserGroupsObtainedEvent event) {
        mUserToDisplay.setGroups(event.getGroups());
        updateGroupLists(event.getGroups());
    }
}
