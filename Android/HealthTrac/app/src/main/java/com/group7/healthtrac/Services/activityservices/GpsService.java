
package com.group7.healthtrac.services.activityservices;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.group7.healthtrac.models.GeoPoint;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GpsService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "GpsService";
    // The minimum distance in meters required for a location update
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATE = 1;
    // The minimum time in ms for a location update
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000;
    // The ideal amount of time between updates
    private static final long IDEAL_UPDATE_TIME = 2000;
    // The number of meters in a mile
    private static final double METERS_PER_MILE = 1609.34;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;
    private Context mContext;
    private Activity mActivity;
    private ArrayList<GeoPoint> mRoutePoints;

    public GpsService(Context context, Activity activity) {
        mRoutePoints = new ArrayList<>();
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        mContext = context;
        mActivity = activity;

        buildGoogleApiClient();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        Log.i(TAG, "request");
        mLocationRequest.setInterval(IDEAL_UPDATE_TIME);
        mLocationRequest.setFastestInterval(MINIMUM_TIME_BETWEEN_UPDATES);
        mLocationRequest.setSmallestDisplacement(MINIMUM_DISTANCE_CHANGE_FOR_UPDATE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    public void stopUpdates() {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi
                .removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startUpdates();
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mCurrentLocation = location;
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            mRoutePoints.add(new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));

            Log.i(TAG, "location updated");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed");
    }

    public void pauseUpdates() {
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    public void disconnectApi() {
        mGoogleApiClient.disconnect();
    }

    public void connectApi() {
        mGoogleApiClient.connect();
    }

    public void resumeUpdates() {
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public ArrayList<GeoPoint> getRoutePoints() {
        return mRoutePoints;
    }

    public double getDistance() {
        double totalDistanceMeters = 0;
        float[] temp = new float[3];
        for (int i = 1; i < mRoutePoints.size(); i++) {
            Location.distanceBetween(mRoutePoints.get(i - 1).getLatitude(), mRoutePoints.get(i - 1).getLongitude(),
                    mRoutePoints.get(i).getLatitude(), mRoutePoints.get(i).getLongitude(), temp);

            totalDistanceMeters += temp[0];
        }

        return totalDistanceMeters / METERS_PER_MILE;
    }
}
