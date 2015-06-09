package com.group7.healthtrac.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.inject.Inject;
import com.group7.healthtrac.CreateAccountActivity;
import com.group7.healthtrac.FeedActivity;
import com.group7.healthtrac.HealthTracApplication;
import com.group7.healthtrac.R;
import com.group7.healthtrac.events.ApiErrorEvent;
import com.group7.healthtrac.events.accountevents.ObtainUserByFacebookEvent;
import com.group7.healthtrac.events.accountevents.ObtainedUserByFacebookEvent;
import com.group7.healthtrac.services.api.IApiCaller;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;
import com.squareup.otto.Subscribe;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.Arrays;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.fragment.RoboFragment;

public class MainFragment extends RoboFragment {

    private static final String TAG = "MainFragment";
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    private UiLifecycleHelper uiHelper;
    private TwitterLoginButton mTwitterLoginButton;
    @Inject private IApiCaller mApiCaller;
    private LinearLayout mLayout;
    private String loginProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user session is not null,
        // the session state change notification may not be triggered. Trigger it if
        // it's open/closed.

        mApiCaller.setContext(getActivity());
        mApiCaller.registerObject(this);
        mLayout.setVisibility(View.GONE);

        Session session = Session.getActiveSession();
        TwitterSession twitterSession = Twitter.getSessionManager().getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        } else if (twitterSession != null) {
            Log.i(TAG, "Already logged in with Twitter");
            mLayout.setVisibility(View.VISIBLE);
            loginProvider = "Twitter";
            mApiCaller.requestData(new ObtainUserByFacebookEvent(Long.toString(twitterSession.getUserId())));
        }
        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

        mTwitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        Crouton.clearCroutonsForActivity(getActivity());

        mApiCaller.unregisterObject(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        mLayout = (LinearLayout) view.findViewById(R.id.loading_screen);
        mLayout.setBackgroundColor(getResources().getColor(R.color.default_color));

        mTwitterLoginButton = (TwitterLoginButton) view.findViewById(R.id.login_button);

        mTwitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                loginProvider = "Twitter";
                Log.i(TAG, "successful twitter login");
                mLayout.setVisibility(View.VISIBLE);
                mApiCaller.requestData(new ObtainUserByFacebookEvent(Long.toString(Twitter.getSessionManager().getActiveSession().getUserId())));
            }

            @Override
            public void failure(TwitterException e) {
                mLayout.setVisibility(View.GONE);
                Twitter.logOut();
                Twitter.getSessionManager().clearActiveSession();
            }
        });

        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "email"));

        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            mLayout.setVisibility(View.VISIBLE);
            makeMeRequest(session);
        } else if (state.isClosed()) {
            session = Session.getActiveSession();
            session.closeAndClearTokenInformation();
            mLayout.setVisibility(View.GONE);
        }
    }

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (response.getError() != null) {
                            mLayout.setVisibility(View.GONE);
                            session.closeAndClearTokenInformation();
                            Crouton.makeText(getActivity(), "Could not connect to Facebook log in", CustomCroutonStyle.ALERT)
                                    .show();
                        }
                        // If the response is successful
                        else if (session == Session.getActiveSession()) {
                            loginProvider = "Facebook";
                            mApiCaller.requestData(new ObtainUserByFacebookEvent(user.getId()));
                        }
                    }
                });
        request.executeAsync();
    }

    @Subscribe
    public void redirectUser(ObtainedUserByFacebookEvent event) {
        if (event.getUser() == null) {
            if (event.getError() != null) {
                mLayout.setVisibility(View.GONE);
                Log.i(TAG, "error was not null");
                Session.getActiveSession().closeAndClearTokenInformation();
                Crouton.clearCroutonsForActivity(getActivity());
                Crouton.makeText(getActivity(), "Could not connect to host network.\r\nPlease try again later.", CustomCroutonStyle.ALERT)
                        .show();
            } else {
                Log.i(TAG, "user was null");
                createNewAccount();
            }
        } else {
            Log.i(TAG, "user was not null");
            ((HealthTracApplication) getActivity().getApplication()).setCurrentUser(event.getUser());
            displayFeedPage();
        }
    }

    private void createNewAccount() {
        Intent intent = new Intent(getActivity(), CreateAccountActivity.class);
        intent.putExtra("loginProvider", loginProvider);
        startActivity(intent);
    }

    public void displayFeedPage() {
        Intent intent = new Intent(getActivity(), FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        mLayout.setVisibility(View.GONE);
        Crouton.makeText(getActivity(), event.getErrorMessage(), CustomCroutonStyle.ALERT).show();
    }
}
