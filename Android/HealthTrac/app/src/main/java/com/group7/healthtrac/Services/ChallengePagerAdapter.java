package com.group7.healthtrac.services;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.group7.healthtrac.fragments.ChallengeFragment;
import com.group7.healthtrac.models.Challenge;
import com.group7.healthtrac.models.User;

public class ChallengePagerAdapter extends FragmentStatePagerAdapter {

    private User mCurrentUser;
    private User mOpponent;
    private Challenge mChallenge;

    public ChallengePagerAdapter(FragmentManager fm, User currentUser, User opponent, Challenge challenge) {
        super(fm);

        mCurrentUser = currentUser;
        mOpponent = opponent;
        mChallenge = challenge;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ChallengeFragment();
        Bundle args = new Bundle();
        if (position == 0) {
            args.putParcelable("goal", (mChallenge.getChallengerId().equals(mOpponent.getId())
                    ? mChallenge.getChallengerGoal()
                    : mChallenge.getFriendGoal()));
        } else {
            args.putParcelable("goal", (mChallenge.getChallengerId().equals(mCurrentUser.getId())
                    ? mChallenge.getChallengerGoal()
                    : mChallenge.getFriendGoal()));
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        if (position  == 0) {
            title = mOpponent.getFullName() + "'s Progress";
        } else {
            title = "Your Progress";
        }
        return title;
    }
}
