package com.group7.healthtrac.services;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.group7.healthtrac.fragments.FeedFragment;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.User;

import java.util.ArrayList;
import java.util.List;

public class FeedPagerAdapter extends FragmentStatePagerAdapter {

    private final static String TAG = "FeedPagerAdapter";
    private User mUser;
    private List<Group> mGroups;

    public FeedPagerAdapter(FragmentManager fm, User user) {
        super(fm);
        mUser = user;
        mGroups = new ArrayList<>();
    }

    public void updateGroups(List<Group> groups) {
        mGroups = new ArrayList<>(groups);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        if (position == 0) {
            args.putParcelable("user", mUser);
        } else {
            args.putParcelable("group", mGroups.get(position - 1));
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return mGroups.size() + 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        if (position  == 0) {
            title = mUser.getPreferredName();
        } else {
            title = mGroups.get(position - 1).getGroupName();
        }
        return title;
    }
}
