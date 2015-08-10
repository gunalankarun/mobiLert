package com.minionhut.michael.mdp_hack;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.minionhut.michael.mdp_hack.CalSQL.CalSqlAdapter;

import java.util.Timer;

public class MainActivity extends ActionBarActivity {

    private static CalSqlAdapter calSqlAdapter;
    NthSense sensorService;
    Timer T = new Timer();
    Vibrator v;

    public static CalSqlAdapter getAdapter() {
        return calSqlAdapter;
    }

    public static  CalSqlAdapter setAdapter(CalSqlAdapter c) {
        return calSqlAdapter = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // is this correct placement of setContentView
        setContentView(R.layout.activity_main);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Intent intent = new Intent(this, NthSense.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        Log.d("Main", "On Create Done");
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
    /*
    public void onDestroy() {
        unbindService(mConnection);
        super.onDestroy();
    }
    */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NthSense.NthBinder binder = (NthSense.NthBinder) service;
            sensorService = binder.getService();
            Log.i("NthSense", "Service created");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


}
