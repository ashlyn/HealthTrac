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
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.services.utilities.Tuple;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
        mApi.createGroup(event.getGroup(), new Callback<Group>() {
            @Override
            public void success(Group group, Response response) {
                mBus.post(new GroupCreatedEvent(group));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not create group", ApiErrorEvent.Cause.CREATE));
            }
        });
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
        mApi.getGroupsByUserId(event.getUserId(), new Callback<List<Group>>() {
            @Override
            public void success(List<Group> groups, Response response) {
                mBus.post(new UserGroupsObtainedEvent(groups));
            }

            @Override
            public void failure(RetrofitError error) {
                if (error != null && error.getMessage() != null) {
                    Log.e(TAG, error.getMessage());
                }
                mBus.post(new ApiErrorEvent("Could not obtain group member information", ApiErrorEvent.Cause.OBTAIN));
            }
        });
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
