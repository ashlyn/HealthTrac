package com.group7.healthtrac;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.inject.Inject;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.goalevents.CreateGoalEvent;
import com.group7.healthtrac.events.goalevents.GoalCreatedEvent;
import com.group7.healthtrac.models.Goal;
import com.group7.healthtrac.models.User;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.group7.healthtrac.services.utilities.MenuService;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.otto.Subscribe;

import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.InjectView;

public class CreateGoalActivity extends RoboActionBarActivity {

    private final static String TAG = "CreateGoalActivity";
    private RelativeLayout.LayoutParams mButtonPositionBottomCenter;
    @Inject private IApiCaller mApiCaller;
    @Inject private MenuService mMenuService;
    @InjectView(R.id.goal_timeframe) private Spinner mTimeFrame;
    @InjectView(R.id.goal_type) private Spinner mGoalType;
    @InjectView(R.id.goal_target_edit) private MaterialEditText mGoalTarget;
    @InjectView(R.id.goal_target_label) private TextView mTargetLabel;
    @InjectView(R.id.goal_create) private ButtonRectangle mCreateGoalButton;
    @InjectView(R.id.create_goal_drawer) private DrawerLayout mDrawer;
    @InjectView(R.id.create_goal_tool_bar) private Toolbar mToolbar;
    @InjectView(R.id.create_goal_recycler_view) private RecyclerView mRecycler;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_goal);
        mCreateGoalButton.setBackgroundColor(getResources().getColor(R.color.accent));

        mGoalType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mTargetLabel.setText("minutes");
                        break;
                    case 1:
                        mTargetLabel.setText("miles");
                        break;
                    case 2:
                        mTargetLabel.setText("steps");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        mApiCaller.setContext(this);
        mUser = ((HealthTracApplication)getApplication()).getCurrentUser();
        if (mUser != null) {
            setSupportActionBar(mToolbar);

            mMenuService.setUpNavigationDrawer(mRecycler, mDrawer, mToolbar, CreateGoalActivity.this, "createGoal", mUser);
            displayGoalShowcaseView();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        // close the drawer first and if the drawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen(mRecycler)) {
            mDrawer.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mUser != null) {
            mApiCaller.setContext(this);
            mApiCaller.registerObject(this);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mApiCaller.unregisterObject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        mMenuService.handleMenuSelection(id, CreateGoalActivity.this);

        return super.onOptionsItemSelected(item);
    }

    private void createButtonPositions() {
        mButtonPositionBottomCenter = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mButtonPositionBottomCenter.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mButtonPositionBottomCenter.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 60)).intValue();
        mButtonPositionBottomCenter.setMargins(0, 0, 0, margin);
    }

    private void displayGoalShowcaseView() {
        createButtonPositions();

        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(R.id.goal_create, this);
        ShowcaseView infoShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(target)
                .setContentTitle("Create a Goal")
                .setContentText("From this page, you can create a new goal. You may select the time frame for repetition, the goal type and the target.")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(20)
                .build();

        infoShowcase.setButtonPosition(mButtonPositionBottomCenter);
        infoShowcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                displayTimeFrameShowcaseView();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }

    private void displayTimeFrameShowcaseView() {
        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(R.id.goal_timeframe, this);
        ShowcaseView infoShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(target)
                .setContentTitle("Time Frame")
                .setContentText("The choices for repetition time frames are daily, weekly, and yearly.")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(21)
                .build();

        infoShowcase.setButtonPosition(mButtonPositionBottomCenter);
        infoShowcase.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                displayTypeShowcaseView();
            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

            }

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {

            }
        });
    }

    private void displayTypeShowcaseView() {
        com.github.amlcurran.showcaseview.targets.Target target = new ViewTarget(R.id.goal_type, this);
        ShowcaseView infoShowcase = new ShowcaseView.Builder(this, true)
                .setTarget(target)
                .setContentTitle("Goal Type")
                .setContentText("The choices for goal type are duration, distance, and steps.")
                .setStyle(R.style.CustomShowcaseViewTheme)
                .singleShot(22)
                .build();

        infoShowcase.setButtonPosition(mButtonPositionBottomCenter);
    }

    public void onCreateGoal(View view) {
        int type = mGoalType.getSelectedItemPosition();
        int timeFrame = mTimeFrame.getSelectedItemPosition();
        double target;

        try {
            if (!mGoalTarget.getText().toString().equals("")) {
                if (type == 0) {
                    target = Double.parseDouble(mGoalTarget.getText().toString()) * 60;
                } else {
                    target = Double.parseDouble(mGoalTarget.getText().toString());
                }

                if (target <= 0) {
                    mGoalTarget.setError("Goal target cannot be less than or equal to 0.");
                } else {
                    Goal goal = new Goal(type, timeFrame, new Date(), false, 0, target, mUser.getId());
                    mApiCaller.requestData(new CreateGoalEvent(goal));
                }
            } else {
                mGoalTarget.setError("Goal target cannot be blank.");
            }
        } catch (NumberFormatException e) {
            mGoalTarget.setError("Goal target must be a valid number.");
        }
    }

    public void onCancel(View view) {
        super.onBackPressed();
    }

    @Subscribe
    public void onGoalCreated(GoalCreatedEvent event) {
        Crouton.clearCroutonsForActivity(this);
        Crouton.makeText(this, "Goal successfully created", CustomCroutonStyle.CONFIRM).show();
        mUser.addGoal(event.getGoal());
        mGoalTarget.setText("");
        mGoalType.setSelection(0);
        mTimeFrame.setSelection(0);
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        Crouton.makeText(this, event.getErrorMessage(), CustomCroutonStyle.ALERT).show();
    }
}
