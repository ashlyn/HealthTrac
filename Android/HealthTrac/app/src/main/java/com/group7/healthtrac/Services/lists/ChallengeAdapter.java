package com.group7.healthtrac.services.lists;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.group7.healthtrac.R;
import com.group7.healthtrac.events.accountevents.ObtainUserEvent;
import com.group7.healthtrac.events.accountevents.UserObtainedEvent;
import com.group7.healthtrac.events.challengeevents.DeleteChallengeEvent;
import com.group7.healthtrac.events.challengeevents.UpdateChallengeEvent;
import com.group7.healthtrac.events.goalevents.CreateGoalEvent;
import com.group7.healthtrac.events.goalevents.GoalCreatedEvent;
import com.group7.healthtrac.models.Challenge;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.squareup.otto.Subscribe;

import java.util.List;

public class ChallengeAdapter extends ArrayAdapter<Challenge> {

    private List<Challenge> mChallenges;
    private Context mContext;
    private Activity mParentActivity;
    private LinearLayout mLoadingScreen;
    private Challenge mSelectedChallenge;
    private IApiCaller mApiCaller;

    public ChallengeAdapter(Context context, List<Challenge> challenges, Activity activity, LinearLayout loadingScreen, IApiCaller apiCaller) {
        super(context, R.layout.item_view, challenges);

        mChallenges = challenges;
        mContext = context;
        mParentActivity = activity;
        mLoadingScreen = loadingScreen;
        mApiCaller = apiCaller;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // Ensure that the view is not null
        if(itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
        }

        // Find the user to use
        Challenge challenge = mChallenges.get(position);
        Goal goal = challenge.getChallengerGoal();

        // Fill view
        ImageView imageView = (ImageView) itemView.findViewById(R.id.item_image_circle);
        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(R.drawable.trophy);

        // set the goal description
        TextView goalInfo = (TextView) itemView.findViewById(R.id.item_primary_information);
        String goalLabel;
        if (goal.getType() == 0) {
            goalLabel = " minutes of activity";
        } else if (goal.getType() == 1) {
            goalLabel = " meters moved";
        } else {
            goalLabel = " steps";
        }

        goalInfo.setText(Goal.GoalTimeFrame.fromInt(goal.getTimeFrame()) + " challenge of " + goal.getTarget() + goalLabel);

        // set the user location
        TextView groupDesc = (TextView) itemView.findViewById(R.id.item_secondary_information);
        groupDesc.setVisibility(View.GONE);

        return itemView;
    }

    /**
     * Changes the functionality of the list item's onClick to redirect a user to a page
     * displaying the user's information
     * @param id The id of the list view whose items' onClick property should be changed
     */
    public void setClickCallback(int id) {
        ListView listView = (ListView) mParentActivity.findViewById(id);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mLoadingScreen.setVisibility(View.VISIBLE);
                mSelectedChallenge = mChallenges.get(position);

                mApiCaller.requestData(new ObtainUserEvent(mSelectedChallenge.getChallengerId()));
            }
        });
    }

    @Subscribe
    public void onChallengerObtained(UserObtainedEvent event) {
        User challenger = event.getUser();
        Goal goal = mSelectedChallenge.getChallengerGoal();

        String goalLabel;
        if (goal.getType() == 0) {
            goalLabel = " minutes of activity?";
        } else if (goal.getType() == 1) {
            goalLabel = " meters moved?";
        } else {
            goalLabel = " steps?";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle("Accept Challenge")
                .setMessage("Do you want to accept the challenge from " + challenger.getFullName() + " of " + goal.getTarget() + goalLabel)
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLoadingScreen.setVisibility(View.GONE);
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Goal goal = new Goal(mSelectedChallenge.getChallengerGoal());
                        goal.setUserId(mSelectedChallenge.getFriendId());
                        mApiCaller.requestData(new CreateGoalEvent(goal));
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.decline, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mApiCaller.requestData(new DeleteChallengeEvent(mSelectedChallenge));
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Subscribe
    public void onGoalCreated(GoalCreatedEvent event) {
        mSelectedChallenge.setFriendGoal(event.getGoal());
        mSelectedChallenge.setAccepted(true);
        mSelectedChallenge.setFriendGoalId(event.getGoal().getId());

        Gson g = new GsonBuilder().create();

        String s = g.toJson(mSelectedChallenge);

        mApiCaller.requestData(new UpdateChallengeEvent(mSelectedChallenge));
    }
}
