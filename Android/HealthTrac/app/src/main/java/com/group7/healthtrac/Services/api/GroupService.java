package com.group7.healthtrac.services.api;

import android.util.Log;

import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.groupevents.AllGroupsObtainedEvent;
import com.group7.healthtrac.events.groupevents.CreateGroupEvent;
import com.group7.healthtrac.events.groupevents.DeleteGroupEvent;
import com.group7.healthtrac.events.groupevents.GroupCreatedEvent;
import com.group7.healthtrac.events.groupevents.GroupDeletedEvent;
import com.group7.healthtrac.events.groupevents.GroupObtainedEvent;
import com.group7.healthtrac.events.groupevents.GroupUpdatedEvent;
import com.group7.healthtrac.events.groupevents.LeaderBoardObtainedEvent;
import com.group7.healthtrac.events.groupevents.ObtainAllGroupsEvent;
import com.group7.healthtrac.events.groupevents.ObtainGroupEvent;
import com.group7.healthtrac.events.groupevents.ObtainLeaderBoardEvent;
import com.group7.healthtrac.events.groupevents.ObtainUserGroupsEvent;
import com.group7.healthtrac.events.groupevents.ObtainUserInvitesEvent;
import com.group7.healthtrac.events.groupevents.UpdateGroupEvent;
import com.group7.healthtrac.events.groupevents.UserGroupsObtainedEvent;
import com.group7.healthtrac.events.groupevents.UserInvitesObtainedEvent;
import com.group7.healthtrac.models.Activity;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.Login;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.models.UserBadge;
import com.group7.healthtrac.services.utilities.Tuple;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GroupService {

    private final static String TAG = "GroupService";
    private HealthTracApi mApi;
    private Bus mBus;
    private ObtainGroupEvent mObtainGroupEvent;
    private ObtainLeaderBoardEvent mObtainLeaderBoardEvent;

    public GroupService(HealthTracApi api, Bus bus) {
        mApi = api;
        mBus = bus;
    }

    @Subscribe
    public void onCreateGroupEvent(CreateGroupEvent event) {
        mBus.post(new GroupCreatedEvent(event.getGroup()));
//        mApi.createGroup(event.getGroup(), new Callback<Group>() {
//            @Override
//            public void success(Group group, Response response) {
//                mBus.post(new GroupCreatedEvent(group));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not create group", ApiErrorEvent.Cause.CREATE));
//            }
//        });
    }

    @Subscribe
    public void onUpdateGroupEvent(UpdateGroupEvent event) {
        mApi.updateGroup(event.getGroupId(), event.getGroup(), new Callback<Group>() {
            @Override
            public void success(Group group, Response response) {
                mBus.post(new GroupUpdatedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not update group", ApiErrorEvent.Cause.UPDATE));
            }
        });
    }

    @Subscribe
    public void onObtainGroupEvent(ObtainGroupEvent event) {
        mObtainGroupEvent = event;
        mApi.getGroupById(event.getGroupId(), new Callback<Group>() {
            @Override
            public void success(Group group, Response response) {
                if (mObtainGroupEvent.getRequester() != null) {
                    mBus.post(new GroupObtainedEvent(group, mObtainGroupEvent.getRequester()));
                } else {
                    mBus.post(new GroupObtainedEvent(group));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain information of group", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onObtainAllGroupsEvent(ObtainAllGroupsEvent event) {
        mApi.getAllGroups(new Callback<List<Group>>() {
            @Override
            public void success(List<Group> groups, Response response) {
                mBus.post(new AllGroupsObtainedEvent(groups));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain group information", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onObtainUserGroupsEvent(ObtainUserGroupsEvent event) {
        List<Login> logins = new ArrayList<>();
        logins.add(new Login("test", "Facebook", "testId"));
        List<User> users = new ArrayList<>();
        users.add(new User("Nikolas Leger", "Nik", 6, 2, 180, "Lincoln, Nebraska", new Date(),"male", "nik@gmail.com", logins, "https://graph.facebook.com/100001060356791/picture?type=large"));
        users.add(new User("Joshua Dunne", "Josh", 6, 0, 169, "Naperville, Illinois", new Date(),"male", "josh@gmail.com", logins, "https://graph.facebook.com/1658206151/picture?type=large"));
        users.add(new User("Michael Casper", "Mike", 6, 0, 150, "Lincoln, Nebraska", new Date(),"male", "mike@gmail.com", logins, "https://graph.facebook.com/100000483062766/picture?type=large"));

        List<Membership> memberships = new ArrayList<>();
        memberships.add(new Membership(1, "testing", Membership.MEMBER));

        List<Group> groups = new ArrayList<>();
        groups.add(new Group(memberships, users, 1, "Best Group", "The best group there is", "http://i.imgur.com/SqfKgdv.png"));
        groups.add(new Group(memberships, users, 1, "Another Group", "It's pretty alright", "http://i.imgur.com/i2JyKRx.png"));
        groups.add(new Group(memberships, users, 1, "Yet Another Group", "Eh, could be better", "http://i.imgur.com/Ebi3KnG.png"));

        mBus.post(new UserGroupsObtainedEvent(groups));
//        mApi.getGroupsByUserId(event.getUserId(), new Callback<List<Group>>() {
//            @Override
//            public void success(List<Group> groups, Response response) {
//                mBus.post(new UserGroupsObtainedEvent(groups));
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                if (error != null && error.getMessage() != null) {
//                    Log.e(TAG, error.getMessage());
//                }
//                mBus.post(new ApiErrorEvent("Could not obtain group member information", ApiErrorEvent.Cause.OBTAIN));
//            }
//        });
    }

    @Subscribe
    public void onDeleteGroupEvent(DeleteGroupEvent event) {
        mApi.deleteGroup(event.getGroupId(), new Callback<Group>() {
            @Override
            public void success(Group group, Response response) {
                mBus.post(new GroupDeletedEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not delete group", ApiErrorEvent.Cause.DELETE));
            }
        });
    }

    @Subscribe
    public void onObtainLeaderBoardEvent(ObtainLeaderBoardEvent event) {
        mObtainLeaderBoardEvent = event;
        mApi.obtainLeaderBoard(event.getGroupId(), event.getCategory(), new Callback<List<Tuple>>() {
            @Override
            public void success(List<Tuple> tuples, Response response) {
                mBus.post(new LeaderBoardObtainedEvent(tuples, mObtainLeaderBoardEvent.getCategory()));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain " + mObtainLeaderBoardEvent.getCategory() + " leader board.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }

    @Subscribe
    public void onObtainUserInvites(ObtainUserInvitesEvent event) {
        mApi.obtainUserInvites(event.getUserId(), new Callback<List<Group>>() {
            @Override
            public void success(List<Group> groups, Response response) {
                mBus.post(new UserInvitesObtainedEvent(groups));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain invites.", ApiErrorEvent.Cause.OBTAIN));
            }
        });
    }
}
