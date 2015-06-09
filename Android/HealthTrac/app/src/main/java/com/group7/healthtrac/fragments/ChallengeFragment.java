package com.group7.healthtrac.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group7.healthtrac.R;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.User;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created by Courtney on 4/26/2015.
 */
public class ChallengeFragment extends RoboFragment {

    private static String GOAL = "goal";
    private View mRootView;
    private Goal mGoal;
    @InjectView(R.id.challenge_pie_chart) private PieChart mPieChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_challenge, container, false);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        mGoal = args.getParcelable(GOAL);
        
        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
        mPieChart.setUsePieRotation(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateGraph();
    }

    private void updateGraph() {
        if (mGoal.getProgress() < 1) {
            mPieChart.addPieSlice(new PieModel((float) mGoal.getProgress() * 100, getResources().getColor(R.color.secondary_accent)));
            mPieChart.addPieSlice(new PieModel((float) ((100 - (mGoal.getProgress() * 100))), Color.LTGRAY));
        } else {
            mPieChart.addPieSlice(new PieModel((float)mGoal.getProgress(), Color.parseColor("#008725")));
        }

        switch (mGoal.getType()) {
            case 0:
                mPieChart.setInnerValueString((double) (Math.round(mGoal.getTarget() * mGoal.getProgress() * 100 / 6)) / 1000.0 + " minutes");
                break;
            case 1:
                mPieChart.setInnerValueString(Math.round(mGoal.getTarget() * mGoal.getProgress() * 100.0) / 100.0 + " meters");
                break;
            case 2:
                mPieChart.setInnerValueString(Math.round(mGoal.getTarget() * mGoal.getProgress() * 100.0) / 100.0 + " steps");
                break;
        }

        mPieChart.startAnimation();
    }
}
