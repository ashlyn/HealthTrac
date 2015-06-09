package com.group7.healthtrac.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.inject.Inject;
import com.group7.healthtrac.HealthTracApplication;
import com.group7.healthtrac.InviteUserActivity;
import com.group7.healthtrac.R;
import com.group7.healthtrac.events.accountevents.GroupUsersObtainedEvent;
import com.group7.healthtrac.events.accountevents.ObtainGroupUsersEvent;
import com.group7.healthtrac.events.badgeevents.BadgeCreatedEvent;
import com.group7.healthtrac.events.badgeevents.CreateBadgeEvent;
import com.group7.healthtrac.events.groupevents.BanMemberEvent;
import com.group7.healthtrac.events.groupevents.DeleteGroupEvent;
import com.group7.healthtrac.events.groupevents.GroupDeletedEvent;
import com.group7.healthtrac.events.groupevents.GroupObtainedEvent;
import com.group7.healthtrac.events.groupevents.GroupUpdatedEvent;
import com.group7.healthtrac.events.groupevents.ObtainGroupEvent;
import com.group7.healthtrac.events.groupevents.UpdateGroupEvent;
import com.group7.healthtrac.events.membershipevents.CreateMembershipEvent;
import com.group7.healthtrac.events.membershipevents.DeleteMembershipEvent;
import com.group7.healthtrac.events.membershipevents.MembershipCreatedEvent;
import com.group7.healthtrac.events.membershipevents.MembershipDeletedEvent;
import com.group7.healthtrac.events.membershipevents.MembershipUpdatedEvent;
import com.group7.healthtrac.events.membershipevents.UpdateMembershipEvent;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.models.UserBadge;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.UserAdapter;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class GroupInfoFragment extends RoboFragment {

    private final static String TAG = "GroupInfoFragment";
    private final static String BANNED = "banned";
    private final static String LEFT = "left";
    private final static String JOINED = "joined";
    private final static String UN_BAN = "unBan";
    private final static String GROUP_ID = "groupId";
    private final static String USER_STATUS = "currentUserStatus";
    private final static int BAN_BADGE = 12;
    private final static int JOIN_GROUP_BADGE = 10;
    private LinearLayout mBannedPage;
    @InjectView(R.id.group_description) private TextView mGroupDescription;
    @InjectView(R.id.group_members) private TextView mGroupMembers;
    @InjectView(R.id.view_group_edit_groupname) private MaterialEditText mGroupNameEdit;
    @InjectView(R.id.view_group_edit_description) private MaterialEditText mGroupDescriptionEdit;
    @InjectView(R.id.list_of_members) private ListView mMemberListView;
    @InjectView(R.id.join_group_button) private Button mJoinButton;
    @InjectView(R.id.leave_group_button) private Button mLeaveButton;
    @InjectView(R.id.edit_button) private Button mEditButton;
    @InjectView(R.id.save_button) private Button mSaveButton;
    @InjectView(R.id.decline_group_invite_button) private Button mDeclineButton;
    @InjectView(R.id.invite_button) private Button mInviteButton;
    @InjectView(R.id.group_icon) private ImageView mGroupImage;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingLayout;
    @InjectView(R.id.unban_button) private ButtonRectangle mUnbanButton;
    @Inject private IApiCaller mApiCaller;
    private Group mGroup;
    private User mUser;
    private List<User> mCurrentMembers;
    private List<User> mBannedMembers;
    private int mGroupId;
    private int mStatus;
    private boolean mIsEditing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsEditing = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mBannedPage = (LinearLayout) getActivity().findViewById(R.id.banned_layout);

        return inflater.inflate(R.layout.fragment_group_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);

        Bundle args = getArguments();
        mGroupId = args.getInt(GROUP_ID);
        mStatus = args.getInt(USER_STATUS);
        mUser = ((HealthTracApplication) getActivity().getApplication()).getCurrentUser();

        mUnbanButton.setBackgroundColor(getResources().getColor(R.color.accent));
        mUnbanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayUnbanPopup();
            }
        });
        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGroup();
            }
        });
        mLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveGroup();
            }
        });
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEditMode();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGroup();
            }
        });
        mDeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineInvite();
            }
        });
        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InviteUserActivity.class);
                intent.putExtra("group", mGroup);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        mApiCaller.setContext(getActivity());
        mApiCaller.registerObject(this);

        mApiCaller.requestData(new ObtainGroupEvent(mGroupId));
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();

        mApiCaller.unregisterObject(this);
    }

    @Subscribe
    public void onGroupObtained(GroupObtainedEvent event){
        mGroup = event.getGroup();
        getActivity().setTitle(mGroup.getGroupName());

        List<Membership> groupMemberships = mGroup.getGroupMembers();
        List<Membership> actualMembers = new ArrayList<>();

        for (Membership m : groupMemberships) {
            if (m.getStatus() == Membership.ADMIN || m.getStatus() == Membership.MEMBER) {
                actualMembers.add(m);
            }
        }

        if (actualMembers.size() == 0) {
            mApiCaller.requestData(new DeleteGroupEvent(mGroupId));
        } else {
            mApiCaller.requestData(new ObtainGroupUsersEvent(mGroupId));
        }

        Picasso.with(getActivity())
                .load(mGroup.getImageUrl())
                .error(R.drawable.group)
                .into(mGroupImage);
    }

    @Subscribe
    public void onGroupUsersObtained(GroupUsersObtainedEvent event) {
        List<User> allUsers = event.getUsers();
        mCurrentMembers = new ArrayList<>();
        mBannedMembers = new ArrayList<>();
        mStatus = mGroup.getStatusInGroup(mUser.getId());
        int currentUserStatus;
        for (User u : allUsers) {
            currentUserStatus = mGroup.getStatusInGroup(u.getId());
            if (currentUserStatus == Membership.ADMIN || currentUserStatus == Membership.MEMBER) {
                mCurrentMembers.add(u);
            } else if (currentUserStatus == Membership.BANNED) {
                mBannedMembers.add(u);
            }
        }

        if (mCurrentMembers.size() == 0) {
            mApiCaller.requestData(new DeleteGroupEvent(mGroupId));
        } else {
            updateView();
        }
    }

    @Subscribe
    public void onGroupDeleted(GroupDeletedEvent event) {
        getActivity().onBackPressed();
    }

    public void enableEditMode() {
        getActivity().setTitle(R.string.title_edit_group);
        mIsEditing = true;
        checkUserStatus();

        mGroupMembers.setVisibility(View.GONE);

        mGroupNameEdit.setVisibility(View.VISIBLE);
        mGroupDescriptionEdit.setVisibility(View.VISIBLE);

        mGroupNameEdit.setText(mGroup.getGroupName());
        mGroupDescriptionEdit.setText(mGroup.getDescription());

        mMemberListView.setVisibility(View.GONE);
    }

    private void updateView() {
        checkUserStatus();

        getActivity().setTitle(mGroup.getGroupName());
        mGroupDescription.setText(mGroup.getDescription());

        populateListView();

        if (!mIsEditing) {
            mGroupMembers.setVisibility(View.VISIBLE);
            mGroupNameEdit.setVisibility(View.GONE);
            mGroupDescriptionEdit.setVisibility(View.GONE);
            mMemberListView.setVisibility(View.VISIBLE);
        }
    }

    public void updateGroup() {
        mIsEditing = false;
        Group group = createGroupFromInformation();

        if (group != null) {
            mGroup = group;
            mLoadingLayout.setVisibility(View.VISIBLE);

            mApiCaller.requestData(new UpdateGroupEvent(mGroup, mGroup.getId()));
        }
    }

    private Group createGroupFromInformation(){
        Group group = new Group(mGroup);

        if (!checkIfFieldsAreBlank()) {
            group.setGroupName(mGroupNameEdit.getText().toString());
            group.setDescription(mGroupDescriptionEdit.getText().toString());
        } else {
            group = null;
        }

        return group;
    }

    private boolean checkIfFieldsAreBlank() {
        boolean fieldsAreBlank = false;
        if (mGroupNameEdit.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mGroupNameEdit.setError("Group name cannot be blank.");
        }
        if (mGroupDescriptionEdit.getText().toString().isEmpty()) {
            fieldsAreBlank = true;
            mGroupDescriptionEdit.setError("Group description cannot be blank.");
        }

        return fieldsAreBlank;
    }

    @Subscribe
    public void onGroupUpdated(GroupUpdatedEvent event){
        getActivity().setTitle(R.string.title_activity_view_group);
        updateView();
        Crouton.makeText(getActivity(), "Group updated", CustomCroutonStyle.CONFIRM).show();
    }

    private void populateListView(){
        ArrayAdapter<User> adapter = new UserAdapter(getActivity(), mCurrentMembers, getActivity(), R.id.list_of_members, mUser, mStatus, mApiCaller);
        ListView list = (ListView) getActivity().findViewById(R.id.list_of_members);
        list.setAdapter(adapter);
        mLoadingLayout.setVisibility(View.GONE);
    }

    private void joinGroup(){
        mJoinButton.setVisibility(View.GONE);
        mLeaveButton.setVisibility(View.VISIBLE);

        if (mStatus == Membership.INVITED || mStatus == Membership.LEFT) {
            Membership membership = mGroup.getMembershipOfUser(mUser.getId());
            membership.setStatus(Membership.MEMBER);
            UpdateMembershipEvent event = new UpdateMembershipEvent(membership, JOINED);
            mApiCaller.requestData(event);
        } else {
            CreateMembershipEvent event = new CreateMembershipEvent(mGroupId, mUser.getId(), Membership.MEMBER);
            mApiCaller.requestData(event);
        }
    }

    @Subscribe
    public void onMembershipCreated(MembershipCreatedEvent event) {
        mGroup.addGroupMember(event.getMembership());
        mUser.addGroup(mGroup);
        Crouton.makeText(getActivity(), "You have joined " + mGroup.getGroupName() + ".", CustomCroutonStyle.CONFIRM).show();

        boolean hasBadge = false;
        for (UserBadge u : mUser.getBadges()) {
            if (u.getBadgeId() == JOIN_GROUP_BADGE) {
                hasBadge = true;
            }
        }

        if (!hasBadge) {
            mApiCaller.requestData(new CreateBadgeEvent(new UserBadge(JOIN_GROUP_BADGE, mUser.getId())));
        }

        updateUsers();
    }

    public void leaveGroup(){
        mJoinButton.setVisibility(View.VISIBLE);
        mLeaveButton.setVisibility(View.GONE);

        if (mCurrentMembers.size() == 1){
            //delete the group if there is one person currently in the group who is now leaving the group
            mApiCaller.requestData(new DeleteGroupEvent(mGroupId));
        } else {

            mStatus = Membership.LEFT;
            Membership membership = mGroup.getMembershipOfUser(mUser.getId());
            membership.setStatus(mStatus);

            mApiCaller.requestData(new UpdateMembershipEvent(membership, LEFT));
        }
    }

    private void checkUserStatus() {
        Log.i(TAG, "Checking user's status in group");
        switch (mStatus) {
            case Membership.BANNED:
                mBannedPage.setVisibility(View.VISIBLE);
                break;
            case Membership.ADMIN:
                if (!mIsEditing){
                    mEditButton.setVisibility(View.VISIBLE);
                    mSaveButton.setVisibility(View.GONE);
                    mUnbanButton.setVisibility(View.GONE);
                }
                else {
                    mEditButton.setVisibility(View.GONE);
                    mSaveButton.setVisibility(View.VISIBLE);
                    mUnbanButton.setVisibility(View.VISIBLE);
                }
            case Membership.MEMBER:
                if (!mIsEditing) {
                    mLeaveButton.setVisibility(View.VISIBLE);
                }
                mJoinButton.setVisibility(View.GONE);
                mDeclineButton.setVisibility(View.GONE);
                mInviteButton.setVisibility((mIsEditing) ? View.GONE : View.VISIBLE);
                break;
            case Membership.INVITED:
                mDeclineButton.setVisibility(View.VISIBLE);
            case Membership.LEFT:
                mJoinButton.setVisibility(View.VISIBLE);
                mLeaveButton.setVisibility(View.GONE);
                mInviteButton.setVisibility(View.GONE);
            default:
                mJoinButton.setVisibility(View.VISIBLE);
                mLeaveButton.setVisibility(View.GONE);
                mInviteButton.setVisibility(View.GONE);
        }
    }


    private void updateUsers() {
        mApiCaller.requestData(new ObtainGroupUsersEvent(mGroupId));
    }

    @Subscribe
    public void onBanMemberEvent(BanMemberEvent event){
        Membership membership = mGroup.getMembershipOfUser(mCurrentMembers.get(event.getPosition()).getId());
        membership.setStatus(Membership.BANNED);
        mApiCaller.requestData(new UpdateMembershipEvent(membership, BANNED));
    }

    @Subscribe
    public void onMembershipUpdated(MembershipUpdatedEvent event){
        if (event.getMessage().equals(BANNED)) {
            Crouton.makeText(getActivity(), "User successfully banned.", CustomCroutonStyle.CONFIRM).show();

            boolean hasBadge = false;
            for (UserBadge u : mUser.getBadges()) {
                if (u.getBadgeId() == BAN_BADGE) {
                    hasBadge = true;
                }
            }

            if (!hasBadge) {
                mApiCaller.requestData(new CreateBadgeEvent(new UserBadge(BAN_BADGE, mUser.getId())));
            }
        } else if (event.getMessage().equals(LEFT)) {
            Crouton.makeText(getActivity(), "You have left " + mGroup.getGroupName() + ".", CustomCroutonStyle.CONFIRM).show();
        } else if (event.getMessage().equals(JOINED)) {
            Crouton.makeText(getActivity(), "You have joined " + mGroup.getGroupName() + ".", CustomCroutonStyle.CONFIRM).show();
            boolean hasBadge = false;
            for (UserBadge u : mUser.getBadges()) {
                if (u.getBadgeId() == JOIN_GROUP_BADGE) {
                    hasBadge = true;
                }
            }

            if (!hasBadge) {
                mApiCaller.requestData(new CreateBadgeEvent(new UserBadge(JOIN_GROUP_BADGE, mUser.getId())));
            }
        } else if (event.getMessage().equals(UN_BAN)) {
            Crouton.makeText(getActivity(), "You have successfully un-banned a member.", CustomCroutonStyle.CONFIRM).show();
        }
        updateUsers();
    }

    private void declineInvite() {
        mApiCaller.requestData(new DeleteMembershipEvent(mGroup.getMembershipOfUser(mUser.getId())));
    }

    private void displayUnbanPopup() {
        UserAdapter adapter = new UserAdapter(getActivity(), mBannedMembers, getActivity(), -1, mUser, Membership.MEMBER, mApiCaller);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Choose a User to Un-ban");
                if (mBannedMembers.size() != 0) {
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            User user = mBannedMembers.get(which);

                            Membership membership = mGroup.getMembershipOfUser(user.getId());
                            membership.setStatus(Membership.LEFT);
                            mApiCaller.requestData(new UpdateMembershipEvent(membership, UN_BAN));
                            dialog.cancel();
                        }
                    });
                } else {
                    builder.setMessage("There are no members to un-ban!");
                    builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                }
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Subscribe
    public void onBadgeCreated(BadgeCreatedEvent event) {
        Crouton.makeText(getActivity(), "Congratulations! You have obtained a new Badge!", CustomCroutonStyle.CONFIRM).show();
        mUser.addBadge(event.getUserBadge());
    }

    @Subscribe
    public void onInviteDeclined(MembershipDeletedEvent event) {
        Crouton.makeText(getActivity(), "Successfully declined invite", CustomCroutonStyle.CONFIRM).show();
        updateUsers();
    }
}
