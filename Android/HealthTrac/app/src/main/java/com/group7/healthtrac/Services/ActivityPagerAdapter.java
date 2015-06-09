package com.group7.healthtrac.services;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.group7.healthtrac.fragments.CreateActivityFragment;
import com.group7.healthtrac.fragments.EnterFoodFragment;
import com.group7.healthtrac.fragments.RecordActivityFragment;
import com.group7.healthtrac.fragments.ViewTodaysActivitiesFragment;
import com.group7.healthtrac.models.Activity;

public class ActivityPagerAdapter extends FragmentPagerAdapter {

    private Activity mActivity;
    private static int COUNT = 4;

    public ActivityPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        mActivity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {

            case 0:
                fragment = new ViewTodaysActivitiesFragment();
                break;
            case 1:
                Bundle args = new Bundle();
                if (mActivity != null) {
                    args.putParcelable("activity", mActivity);
                }
                fragment = new CreateActivityFragment();
                fragment.setArguments(args);
                break;
            case 2:
                fragment = new RecordActivityFragment();
                break;
            case 3:
                fragment = new EnterFoodFragment();
                break;
            default:
                fragment = new CreateActivityFragment();
                break;
        }

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
                title = "Today's Report";
                break;
            case 1:
                title = "Enter Activity";
                break;
            case 2:
                title = "Record Activity";
                break;
            case 3:
                title = "Enter Meal";
                break;
            default:
                title = "Enter Activity";
                break;
        }
        return title;
    }
}
