package com.group7.healthtrac;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.group7.healthtrac.fragments.MainFragment;
import com.group7.healthtrac.services.utilities.CustomCroutonStyle;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import roboguice.activity.RoboFragmentActivity;

public class MainActivity extends RoboFragmentActivity {

    private MainFragment mainFragment;
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 8000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent(getIntent());

        if (savedInstanceState == null) {

            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();

        } else {

            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
    }

    private void handleIntent(Intent intent) {
        if (intent.hasExtra("justDeletedAccount")) {
            Crouton.makeText(this, "Account successfully deleted", CustomCroutonStyle.CONFIRM).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "In Activity's result");

        // Pass the activity result to the fragment, which will
        // then pass the result to the login button.
        if (mainFragment != null) {
            mainFragment.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.i(TAG, "fragment is null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkForPlayServices()) {

            // Logs 'install' and 'app activate' App Events.
            AppEventsLogger.activateApp(this);
            Session session = Session.getActiveSession();

            if (session != null && session.getState().isClosed()) {
                session.closeAndClearTokenInformation();
                Log.i(TAG, "Session is closed");
            } else if (session != null && session.getState().isOpened()) {
                Log.i(TAG, "Session is open");
            }
        } else {
            Log.i(TAG, "Does not have play services");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App FeedEvent.
        AppEventsLogger.deactivateApp(this);
    }

    private boolean checkForPlayServices() {
        int hasPlayServices;
        boolean result = true;

        if ((hasPlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.this)) != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(hasPlayServices, MainActivity.this, PLAY_SERVICES_RESOLUTION_REQUEST);
            result = false;
        }

        return result;
    }
}
