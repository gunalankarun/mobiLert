package com.minionhut.michael.mdp_hack;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.minionhut.michael.mdp_hack.CalSQL.CalSqlAdapter;
import com.minionhut.michael.mdp_hack.CalSQL.CalSQLObj;

public class NthSense extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccel;
    private Sensor mProximity;
    private Sensor mLight;
    private Sensor mStep;
    boolean prox;

    private IBinder mBinder = new NthBinder();

    private float[] accelVals = new float[3];
    private int proximityVal = 0;
    private float proxMax;
    private float lux;
    private float numSteps = 0;
    CalSqlAdapter calSqlAdapter;

    private float epsilon = 0.0000001f;

    private boolean isWalking, isTakingData;
    private int sampleCount = 0;
    private static final int sampleBinSize = 32;

    dataService sensorService;

    public NthSense() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        calSqlAdapter = MainActivity.getAdapter();
        if(calSqlAdapter==null)
            calSqlAdapter = MainActivity.setAdapter(new CalSqlAdapter(this));

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mStep = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        try{proxMax = mProximity.getMaximumRange();}catch(Exception e){prox=true;}                  //Will treat this value as binary close, not-close
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);                                //See Android Proximity Sensor Documentation
        isWalking = false;
        isTakingData = false;

        mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);

        Intent intent = new Intent(this, dataService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.d("NthSense", "On Create done");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximityVal = (Math.abs(sensorEvent.values[0] - proxMax) < epsilon) ? 0 : 1;
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelVals = sensorEvent.values;
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            lux = sensorEvent.values[0];
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            numSteps = sensorEvent.values[0] - numSteps;
        }

        float x = accelVals[0];
        float y = accelVals[1];
        float z = accelVals[2];

        int d_isWalking = (isWalking) ? 1 : 0;                                                      //converts boolean to 1 or 0 for storage in database
        int d_isTrainingData = (isTakingData) ? 1 : 0;
        Log.d("NthSense", "" + x);
        long timestamp = System.currentTimeMillis();
        Log.d("x =", x + "");
        calSqlAdapter.insertData(new CalSQLObj(x,y,z,proximityVal,lux,d_isWalking,d_isTrainingData,numSteps,timestamp)); //Insert data into local db

        sampleCount = (sampleCount + 1) % sampleBinSize;                                            //Package N samples and automatically send to server

        if(is_window_filled()) sensorService.submitData();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        unbindService(mConnection);
        super.onDestroy();
    }


    public class NthBinder extends Binder {
        NthSense getService() {
            return NthSense.this;
        }
    }

    public void set_is_walking(boolean is_walking) {
        this.isWalking = is_walking;
        Log.i("NthSense", "Setting is Walking " + String.valueOf(this.isWalking));
    }

    public void set_is_takingData(boolean is_takingData) {
        this.isTakingData = is_takingData;
        Log.i("NthSense", "Setting is Taking Data " + String.valueOf(this.isTakingData));
    }

    public boolean is_window_filled(){
        return (sampleCount == 0) ? true: false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            dataService.dataBinder binder = (dataService.dataBinder) service;
            sensorService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

}
