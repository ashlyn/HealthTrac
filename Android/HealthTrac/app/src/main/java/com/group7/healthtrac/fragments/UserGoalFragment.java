package com.group7.healthtrac.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.inject.Inject;
import com.group7.healthtrac.ChallengeUserActivity;
import com.group7.healthtrac.CreateGoalActivity;
import com.group7.healthtrac.R;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.goalevents.ObtainUserGoalsEvent;
import com.group7.healthtrac.events.goalevents.UserGoalsObtainedEvent;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.GoalPagerAdapter;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.squareup.otto.Subscribe;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class UserGoalFragment extends RoboFragment {

    private static final String IS_CURRENT_USER = "isCurrentUser";
    private static final String USER_TO_DISPLAY = "userToDisplay";
    private static final String TAG = "UserGoalFragment";
    @Inject private IApiCaller mApiCaller;
    @InjectView(R.id.add_goal) private AddFloatingActionButton mAddGoalButton;
    @InjectView(R.id.challenge_button) private FloatingActionButton mChallengeButton;
    @InjectView(R.id.goal_fab) private FloatingActionsMenu mGoalFab;
    @InjectView(R.id.goal_pager) private  ViewPager mViewPager;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingLayout;
    private User mUser;
    private List<Goal> mGoals;
    private GoalPagerAdapter mGoalPagerAdapter;
    private boolean mIsCurrentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mUser = args.getParcelable(USER_TO_DISPLAY);
        mIsCurrentUser = args.getBoolean(IS_CURRENT_USER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_user_goal, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);

        displayCorrectObjects(mIsCurrentUser);
    }

    @Override
    public void onResume() {
        super.onResume();

        mApiCaller.setContext(getActivity());
        mApiCaller.registerObject(this);
        mApiCaller.requestData(new ObtainUserGoalsEvent(mUser.getId()));
        //mLoadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mApiCaller.unregisterObject(this);
    }

    private void displayCorrectObjects(boolean isCurrentUser) {
        if (isCurrentUser) {
            mGoalFab.setVisibility(View.VISIBLE);
            mAddGoalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CreateGoalActivity.class);
                    startActivity(intent);
                }
            });
            mChallengeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mGoals.size() != 0) {
                        Intent intent = new Intent(getActivity(), ChallengeUserActivity.class);
                        intent.putExtra("goal", mGoals.get(mViewPager.getCurrentItem()));
                        startActivity(intent);
                    } else {
                        Crouton.makeText(getActivity(), "You must have created a goal to challenge a friend.", CustomCroutonStyle.ALERT).show();
                    }
                }
            });
        }
    }

    @Subscribe
    public void onUserGoalsObtained(UserGoalsObtainedEvent event) {
        mGoals = event.getGoals();
        displayInformation();
    }

    private void displayInformation() {
        mGoalPagerAdapter = new GoalPagerAdapter(getChildFragmentManager(), mGoals);

        mViewPager.setAdapter(mGoalPagerAdapter);
        mLoadingLayout.setVisibility(View.GONE);
    }
}
