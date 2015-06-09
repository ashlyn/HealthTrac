package com.group7.healthtrac.services;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.group7.healthtrac.fragments.GoalFragment;
import com.group7.healthtrac.models.Goal;

import java.util.Date;
import java.util.List;

public class GoalPagerAdapter extends FragmentStatePagerAdapter {

    private List<Goal> mGoals;

    public GoalPagerAdapter(FragmentManager fm, List<Goal> goals) {
        super(fm);
        mGoals = goals;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new GoalFragment();

        Bundle args = new Bundle();
        if (mGoals.size() != 0) {
            args.putParcelable("goal", mGoals.get(position));
        } else {
            args.putParcelable("goal", new Goal(1, 1, new Date(), false, 0, 10, "sample"));
        }
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return (mGoals.size() == 0) ? 1 : mGoals.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (mGoals.size() == 0) ? "Sample Goal" : "Goal " + (position + 1);
    }
}
