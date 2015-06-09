package com.group7.healthtrac.services.activityservices;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class PedometerService {

    private SensorManager mSensorManager;
    private String TAG = "Pedometer";
    private double mPreviousVal;
    private double mCurrentVal;
    private int mNumSteps;
    private final static double UPPER_THRESHOLD = 4.2;// 5 is ok with 1.5
    private final static double LOWER_THRESHOLD = 1.8; // 4.2 and 2 seems ok
    private TextView mStepsView;
    private Context mContext;
    private boolean mJustStepped;

    public PedometerService(Context context, TextView steps) {
        mPreviousVal = 0;
        mCurrentVal = 0;
        mNumSteps = 0;
        mContext = context;
        mJustStepped = false;
        this.mStepsView = steps;
    }

    public void resetSteps() {
        mNumSteps = 0;
        mPreviousVal = 0;
    }

    public void disableAccelerometerListening() {
        mSensorManager.unregisterListener(sensorEventListener);
    }

    public int getNumSteps() {
        return mNumSteps;
    }

    public void enableAccelerometerListening() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(sensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private SensorEventListener sensorEventListener
            = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    mCurrentVal = Math.abs(Math.sqrt((event.values[0] * event.values[0])
                            + (event.values[1] * event.values[1])
                            + (event.values[2] * event.values[2])));

                    if (Math.abs(mCurrentVal - mPreviousVal) > UPPER_THRESHOLD && !mJustStepped) {
                        mJustStepped = true;
                        mNumSteps++;
                        mStepsView.setText(String.valueOf(mNumSteps));
                    } else if (Math.abs(mCurrentVal - mPreviousVal) < LOWER_THRESHOLD && mJustStepped) {
                        mJustStepped = false;
                    }

                    mPreviousVal = mCurrentVal;
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
}
