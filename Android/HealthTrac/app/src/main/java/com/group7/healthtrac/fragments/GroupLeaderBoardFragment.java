package com.group7.healthtrac.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.inject.Inject;
import com.group7.healthtrac.HealthTracApplication;
import com.group7.healthtrac.R;
import com.group7.healthtrac.ViewProfileActivity;
import com.group7.healthtrac.events.groupevents.LeaderBoardObtainedEvent;
import com.group7.healthtrac.events.groupevents.ObtainLeaderBoardEvent;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.LeaderBoardAdapter;
import com.group7.healthtrac.services.utilities.Tuple;
import com.squareup.otto.Subscribe;

import java.util.List;

import roboguice.RoboGuice;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectView;

public class GroupLeaderBoardFragment extends RoboListFragment {

    private final static String TAG = "GroupLeaderBoardFragment";
    private final static String GROUP_ID = "groupId";
    private final static String CATEGORY = "category";
    @Inject private IApiCaller mApiCaller;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingLayout;
    private List<Tuple> mLeaders;
    private User mCurrentUser;
    private int mGroupId;
    private String mCategory;
    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mRootView = inflater.inflate(R.layout.fragment_group_leader_board, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
        mCurrentUser = ((HealthTracApplication) getActivity().getApplication()).getCurrentUser();

        Bundle args = getArguments();
        mGroupId = args.getInt(GROUP_ID);
        mCategory = args.getString(CATEGORY);
    }

    @Override
    public void onResume() {
        super.onResume();

        mApiCaller.setContext(getActivity());
        mApiCaller.registerObject(this);
        obtainLeaderList();
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();

        mApiCaller.unregisterObject(this);
    }

    private void obtainLeaderList() {
        mApiCaller.requestData(new ObtainLeaderBoardEvent(mGroupId, mCategory));
    }

    @Subscribe
    public void onLeaderBoardListObtained(LeaderBoardObtainedEvent event) {
        if (event.getCategory().equals(mCategory)) {
            mLeaders = event.getLeaders();
            setListAdapter(new LeaderBoardAdapter(mRootView.getContext(), event.getLeaders(), mCategory));
            mLoadingLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        User clickedUser = mLeaders.get(position).getUser();
        Intent intent = new Intent(getActivity(), ViewProfileActivity.class);

        // if the requested user is the user that is currently using the app, redirect them
        // to their own page

        if (clickedUser.getId().equals(mCurrentUser.getId())) {
            intent.putExtra("isCurrentUser", true);
        } else {
            intent.putExtra("userToShowId", clickedUser.getId());
        }
        startActivity(intent);
    }
}
