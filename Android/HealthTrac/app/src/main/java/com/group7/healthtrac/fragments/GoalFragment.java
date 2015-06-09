package com.group7.healthtrac.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.group7.healthtrac.R;
import com.group7.healthtrac.models.Goal;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import roboguice.RoboGuice;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class GoalFragment extends RoboFragment {

    private static final String ARG_GOAL = "goal";
    private static final String TAG = "GoalFragment";
    @InjectView(R.id.goal_piechart) private PieChart mGoalPieChart;
    @InjectView(R.id.goal_value) private TextView mGoalValue;
    @InjectView(R.id.view_goal_type) private TextView mGoalType;
    @InjectView(R.id.view_goal_timeframe) private TextView mGoalTimeFrame;
    private Goal mGoal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mRootView = inflater.inflate(R.layout.fragment_goal, container, false);

        Bundle args = getArguments();

        mGoal = args.getParcelable(ARG_GOAL);

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
        mGoalPieChart.setUsePieRotation(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateGraph();
    }

    private void updateGraph() {
        if (mGoal.getProgress() < 1) {
            mGoalPieChart.addPieSlice(new PieModel((float) mGoal.getProgress() * 100, getResources().getColor(R.color.secondary_accent)));
            mGoalPieChart.addPieSlice(new PieModel((float) ((100 - (mGoal.getProgress() * 100))), Color.LTGRAY));
        } else {
            mGoalPieChart.addPieSlice(new PieModel((float)mGoal.getProgress(), getResources().getColor(R.color.goal_green)));
        }

        mGoalTimeFrame.setText("Repetition: " + Goal.GoalTimeFrame.fromInt(mGoal.getTimeFrame()));
        mGoalType.setText("Goal type: " + Goal.GoalType.fromInt(mGoal.getType()));
        String info = "Target: ";
        switch (mGoal.getType()) {
            case 0:
                mGoalPieChart.setInnerValueString((double) (Math.round(mGoal.getTarget() * mGoal.getProgress() * 100 / 6)) / 1000.0 + " minutes");
                info += (double)(Math.round(mGoal.getTarget() / 6)) / 10 + " minutes";
                break;
            case 1:
                mGoalPieChart.setInnerValueString(Math.round(mGoal.getTarget() * mGoal.getProgress() * 100.0) / 100.0 + " miles");
                info += mGoal.getTarget() + " miles";
                break;
            case 2:
                mGoalPieChart.setInnerValueString(Math.round(mGoal.getTarget() * mGoal.getProgress() * 100.0) / 100.0 + " steps");
                info += mGoal.getTarget() + " steps";
                break;
        }
        mGoalValue.setText(info);

        mGoalPieChart.startAnimation();
    }
}
