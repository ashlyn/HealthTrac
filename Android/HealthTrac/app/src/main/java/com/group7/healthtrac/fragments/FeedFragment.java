package com.group7.healthtrac.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.group7.healthtrac.R;
import com.group7.healthtrac.ViewFeedItemActivity;
import com.group7.healthtrac.ViewGroupActivity;
import com.group7.healthtrac.events.activityevents.ActivityObtainedEvent;
import com.group7.healthtrac.events.activityevents.ObtainActivityByIdEvent;
import com.group7.healthtrac.events.badgeevents.ObtainUserBadgeEvent;
import com.group7.healthtrac.events.badgeevents.UserBadgeObtainedEvent;
import com.group7.healthtrac.events.feedevents.EndOfDayReportObtainedEvent;
import com.group7.healthtrac.events.feedevents.FeedEventsObtainedEvent;
import com.group7.healthtrac.events.feedevents.ObtainEndOfDayReportEvent;
import com.group7.healthtrac.events.feedevents.ObtainFeedEventsByGroupIdEvent;
import com.group7.healthtrac.events.feedevents.ObtainFeedEventsByUserIdEvent;
import com.group7.healthtrac.events.foodevents.FoodObtainedEvent;
import com.group7.healthtrac.events.foodevents.ObtainFoodEvent;
import com.group7.healthtrac.events.goalevents.GoalObtainedEvent;
import com.group7.healthtrac.events.goalevents.ObtainGoalByIdEvent;
import com.group7.healthtrac.events.moodevents.ObtainUserMoodEvent;
import com.group7.healthtrac.events.moodevents.UserMoodObtainedEvent;
import com.group7.healthtrac.models.Badge;
import com.group7.healthtrac.models.EndOfDayReport;
import com.group7.healthtrac.models.FeedEvent;
import com.group7.healthtrac.models.Food;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.Group;
import com.group7.healthtrac.models.Membership;
import com.group7.healthtrac.models.Mood;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.utilities.Utility;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.lists.EventAdapter;
import com.squareup.otto.Subscribe;

import roboguice.RoboGuice;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectView;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends RoboListFragment {

    public static final String ARG_USER = "user";
    private static final String ARG_GROUP = "group";
    private static final String TAG = "FeedFragment";
    private View mRootView;
    @Inject private IApiCaller mApiCaller;
    @InjectView(R.id.loading_screen) private LinearLayout mLoadingScreen;
    private User mUser;
    private Group mGroup;
    private List<FeedEvent> mFeedEvents;
    private FeedEvent mClickedEvent;
    private String mRequesterId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFeedEvents = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_feed, container, false);

        Bundle args = getArguments();

        if (args.containsKey(ARG_USER)) {
            mUser = args.getParcelable(ARG_USER);
            mRequesterId = mUser.getFullName();
        } else {
            mGroup = args.getParcelable(ARG_GROUP);
            mRequesterId = mGroup.getGroupName();
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoboGuice.getInjector(getActivity()).injectViewMembers(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mApiCaller.setContext(getActivity());
        mApiCaller.registerObject(this);

        if (mUser != null) {
            mLoadingScreen.setVisibility(View.VISIBLE);
            mApiCaller.requestData(new ObtainFeedEventsByUserIdEvent(mUser.getId(), mUser.getFullName()));
        } else if (mGroup != null) {
            mLoadingScreen.setVisibility(View.VISIBLE);
            mApiCaller.requestData(new ObtainFeedEventsByGroupIdEvent(mGroup.getId(), mGroup.getGroupName()));
        } else {
            getActivity().findViewById(R.id.error_page).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mApiCaller.unregisterObject(this);
    }

    @Subscribe
    public void onFeedEventsObtained(FeedEventsObtainedEvent event) {
        if (event.getRequester() != null && event.getRequester().equals(mRequesterId)) {
            mFeedEvents = event.getFeedEvents();
            setListAdapter(new EventAdapter(mRootView.getContext(), mFeedEvents));
            mLoadingScreen.setVisibility(View.GONE);
            getActivity().findViewById(R.id.error_page).setVisibility(View.GONE);
        } else if (mGroup != null && event.getRequestingGroup() == mGroup.getId()) {
            mFeedEvents = event.getFeedEvents();

            setListAdapter(new EventAdapter(mRootView.getContext(), mFeedEvents));
            mLoadingScreen.setVisibility(View.GONE);
            getActivity().findViewById(R.id.error_page).setVisibility(View.GONE);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mClickedEvent = mFeedEvents.get(position);

        switch(mClickedEvent.getType()) {
            case 0:
                mApiCaller.requestData(new ObtainUserBadgeEvent(mClickedEvent.getEventId(), mRequesterId));
                break;
            case 1:
            case 2:
                Intent intent = new Intent(getActivity(), ViewGroupActivity.class);
                intent.putExtra("groupId", mClickedEvent.getEventId());
                startActivity(intent);
                break;
            case 3:
                mApiCaller.requestData(new ObtainActivityByIdEvent(mClickedEvent.getEventId(), mRequesterId));
                break;
            case 4:
                mApiCaller.requestData(new ObtainFoodEvent(mClickedEvent.getEventId(), mRequesterId));
                break;
            case 5:
            case 6:
                mApiCaller.requestData(new ObtainGoalByIdEvent(mClickedEvent.getEventId(), mRequesterId));
                break;
            case 7:
                mApiCaller.requestData(new ObtainUserMoodEvent(mClickedEvent.getEventId(), mRequesterId));
                break;
            case 8:
                mApiCaller.requestData(new ObtainEndOfDayReportEvent(mClickedEvent.getEventId(), mRequesterId));
                break;
        }
    }

    @Subscribe
    public void displayBadge(UserBadgeObtainedEvent event) {
        if (event.getRequester().equals(mRequesterId)) {
            String[] textToDisplay = new String[4];
            Badge badge = event.getBadge();

            textToDisplay[3] = badge.getDescription();
            textToDisplay[2] = "Obtained the " + badge.getName() + " badge!";
            textToDisplay[1] = Utility.displayDateAndTime(mClickedEvent.getDate());
            textToDisplay[0] = mClickedEvent.getUser().getPreferredName();

            Intent intent = new Intent(getActivity(), ViewFeedItemActivity.class);
            intent.putExtra("badge", badge);
            intent.putExtra("userId", mClickedEvent.getUser().getId());
            intent.putExtra("infoToDisplay", textToDisplay);
            intent.putExtra("title", badge.getName());
            startActivity(intent);
        }
    }

    @Subscribe
    public void displayActivity(ActivityObtainedEvent event) {
        if (event.getRequester().equals(mRequesterId)) {
            com.group7.healthtrac.models.Activity activity = event.getActivity();

            int[] durationPieces = new int[3];

            durationPieces[0] = (int) activity.getDuration() / 3600;
            durationPieces[1] = (int) (activity.getDuration() - (durationPieces[0] * 3600)) / 60;
            durationPieces[2] = (int) (activity.getDuration() - (durationPieces[0] * 3600) - (durationPieces[1] * 60));

            String[] textToDisplay = new String[4];
            textToDisplay[3] = "Steps: " + activity.getSteps();

            switch (activity.getType()) {
                case 0:
                    textToDisplay[2] = "Ran ";
                    break;
                case 1:
                    textToDisplay[2] = "Biked ";
                    break;
                case 2:
                    textToDisplay[2] = "Jogged ";
                    break;
                case 3:
                    textToDisplay[2] = "Walked ";
                    break;
                case 4:
                    textToDisplay[2] = "Activity: " + activity.getName() + "\nInformation: ";
                    break;
            }

            textToDisplay[2] += (Math.round(activity.getDistance() * 100) / 100.0) + "mi in "
                    + Math.round(durationPieces[0] * 10) / 10 + (durationPieces[0] == 1 ? " hour " : " hours ")
                    + Math.round(durationPieces[1] * 10) / 10 + (durationPieces[1] == 1 ? "minute" : " minutes ")
                    + Math.round(durationPieces[2] * 10) / 10 + (durationPieces[2] == 1 ? " second" : " seconds.");

            textToDisplay[1] = Utility.displayDateAndTime(mClickedEvent.getDate());
            textToDisplay[0] = mClickedEvent.getUser().getPreferredName();

            Intent intent = new Intent(getActivity(), ViewFeedItemActivity.class);
            intent.putExtra("activity", activity);
            intent.putExtra("infoToDisplay", textToDisplay);
            intent.putExtra("title", "View Activity");
            startActivity(intent);
        }
    }

    @Subscribe
    public void displayMood(UserMoodObtainedEvent event) {
        if (event.getRequester().equals(mRequesterId)) {
            String[] textToDisplay = new String[3];
            Mood mood = event.getMood();

            textToDisplay[2] = "Changed their mood to " + mood.getType() + ".";
            textToDisplay[1] = Utility.displayDateAndTime(mClickedEvent.getDate());
            textToDisplay[0] = mClickedEvent.getUser().getPreferredName();

            Intent intent = new Intent(getActivity(), ViewFeedItemActivity.class);
            intent.putExtra("infoToDisplay", textToDisplay);
            intent.putExtra("mood", mood);
            intent.putExtra("title", "View Mood Update");
            startActivity(intent);
        }
    }

    @Subscribe
    public void displayFoodEaten(FoodObtainedEvent event) {
        if (event.getRequester().equals(mRequesterId)) {
            String[] textToDisplay = new String[3];
            Food food = event.getFood();

            textToDisplay[2] = "Ate " + food.getAmount() + " " + Food.Measurement.fromInt(food.getUnit()) + " of " + food.getFoodName();
            textToDisplay[1] = Utility.displayDateAndTime(mClickedEvent.getDate());
            textToDisplay[0] = mClickedEvent.getUser().getPreferredName();

            Intent intent = new Intent(getActivity(), ViewFeedItemActivity.class);
            intent.putExtra("infoToDisplay", textToDisplay);
            intent.putExtra("title", "View Meal");
            startActivity(intent);
        }
    }

    @Subscribe
    public void displayGoalEvent(GoalObtainedEvent event) {
        if (event.getRequester().equals(mRequesterId)) {
            String[] textToDisplay = new String[5];
            Goal goal = event.getGoal();
            textToDisplay[4] = "Repetition period: " + Goal.GoalTimeFrame.fromInt(goal.getTimeFrame()).toString().toLowerCase();
            if (goal.getType() == 0) {
               textToDisplay[3] = "Goal target: " + goal.getTarget() / 60 + " minutes";
            } else {
                textToDisplay[3] = "Goal target: " + goal.getTarget() + ((goal.getType() == 1) ? " miles" : " steps");
            }
            textToDisplay[2] = (mClickedEvent.getType() == 5)
                    ? "Set a new " + Goal.GoalType.fromInt(goal.getType()).toString().toLowerCase() + " goal."
                    : "Completed a " + Goal.GoalType.fromInt(goal.getType()).toString().toLowerCase() + " goal.";
            textToDisplay[1] = Utility.displayDateAndTime(mClickedEvent.getDate());
            textToDisplay[0] = mClickedEvent.getUser().getPreferredName();

            Intent intent = new Intent(getActivity(), ViewFeedItemActivity.class);
            intent.putExtra("infoToDisplay", textToDisplay);
            intent.putExtra("goal", goal);
            intent.putExtra("title", "View Goal");
            startActivity(intent);
        }
    }

    @Subscribe
    public void displayEndOfDayReport(EndOfDayReportObtainedEvent event) {
        if (event.getRequester().equals(mRequesterId)) {
            String[] textToDisplay = new String[5];
            EndOfDayReport endOfDayReport = event.getEndOfDayReport();

            textToDisplay[4] = "Total steps: " + endOfDayReport.getTotalSteps();
            textToDisplay[3] = "Total Duration: " + endOfDayReport.getTotalDuration();
            textToDisplay[2] = "Total Distance: " + endOfDayReport.getTotalDistance();
            textToDisplay[1] = Utility.displayDateAndTime(mClickedEvent.getDate());
            textToDisplay[0] = mClickedEvent.getUser().getPreferredName();

            Intent intent = new Intent(getActivity(), ViewFeedItemActivity.class);
            intent.putExtra("infoToDisplay", textToDisplay);
            intent.putExtra("title", "View End of Day Report");
            startActivity(intent);
        }
    }
}
