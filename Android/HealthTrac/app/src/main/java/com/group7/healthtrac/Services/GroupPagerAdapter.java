package com.group7.healthtrac.services;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.group7.healthtrac.fragments.GroupInfoFragment;
import com.group7.healthtrac.fragments.GroupLeaderBoardFragment;

public class GroupPagerAdapter extends FragmentPagerAdapter {

    private final static int MAX_PAGES = 4;
    private int mCurrentUserStatus;
    private int mGroupId;

    public GroupPagerAdapter(FragmentManager fm, int currentUserStatus, int groupId) {
        super(fm);

        mCurrentUserStatus = currentUserStatus;
        mGroupId = groupId;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        Bundle args = new Bundle();
        args.putInt("groupId", mGroupId);
        args.putInt("currentUserStatus", mCurrentUserStatus);

        switch (position) {
            case 0:
                fragment = new GroupInfoFragment();
                break;
            case 1:
                fragment = new GroupLeaderBoardFragment();
                args.putString("category", "distance");
                break;
            case 2:
                fragment = new GroupLeaderBoardFragment();
                args.putString("category", "duration");
                break;
            case 3:
                fragment = new GroupLeaderBoardFragment();
                args.putString("category", "steps");
                break;
            default:
                fragment = new GroupInfoFragment();
                break;
        }

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return MAX_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case 0:
                title = "Information";
                break;
            case 1:
                title = "Distance Leaders";
                break;
            case 2:
                title = "Duration Leaders";
                break;
            case 3:
                title = "Step Leaders";
                break;
            default:
                title = "Information";
                break;
        }
        return title;
    }
}
