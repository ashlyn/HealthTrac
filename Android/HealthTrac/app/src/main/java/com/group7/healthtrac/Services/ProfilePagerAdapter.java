package com.group7.healthtrac.services;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.group7.healthtrac.fragments.UserGoalFragment;
import com.group7.healthtrac.fragments.UserGroupFragment;
import com.group7.healthtrac.fragments.UserInfoFragment;
import com.group7.healthtrac.models.User;

public class ProfilePagerAdapter extends FragmentPagerAdapter {

    private static final int COUNT = 3;
    private User mUser;
    private User mCurrentUser;
    private boolean mIsCurrentUser;

    public ProfilePagerAdapter(FragmentManager fm, User user, User currentUser, boolean isCurrentUser) {
        super(fm);
        mUser = user;
        mCurrentUser = currentUser;
        mIsCurrentUser = isCurrentUser;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        Bundle args = new Bundle();

        switch (position) {
            case 0:
                fragment = new UserInfoFragment();
                break;
            case 1:
                fragment = new UserGroupFragment();
                break;
            case 2:
                fragment = new UserGoalFragment();
                break;
            default:
                fragment = new UserInfoFragment();
                break;
        }

        args.putBoolean("isCurrentUser", mIsCurrentUser);
        args.putParcelable("userToDisplay", mUser);
        args.putParcelable("currentUser", mCurrentUser);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case 0:
                title = "Information";
                break;
            case 1:
                title = "Groups";
                break;
            case 2:
                title = "Goals";
                break;
            default:
                title = "Information";
                break;
        }
        return title;
    }
}
