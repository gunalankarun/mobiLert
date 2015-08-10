package com.minionhut.michael.mdp_hack;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.minionhut.michael.mdp_hack.CalNetwork.PostData;
import com.minionhut.michael.mdp_hack.CalSQL.CalSqlAdapter;
import com.minionhut.michael.mdp_hack.CalSQL.CalSQLObj;

import org.apache.http.HttpResponse;
import java.util.concurrent.ExecutionException;

public class dataService extends Service {
    private IBinder mBinder = new dataBinder();
    CalSqlAdapter calSqlAdapter;

    public dataService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        calSqlAdapter = MainActivity.getAdapter();
        if(calSqlAdapter==null)
            calSqlAdapter = MainActivity.setAdapter(new CalSqlAdapter(this));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class dataBinder extends Binder {
        dataService getService() {
            return dataService.this;
        }
    }

    public void submitData() {
        CalSQLObj[] cso = calSqlAdapter.getRangeData(0, System.currentTimeMillis());                //Submits localDatabase to server
        String json = calSqlAdapter.createJSONObjWithEmail(cso).toString();
        Log.i("DataBase", "Attempting to send "+Integer.toString(json.split("\\}").length - 1)+" entries");
        try {
            HttpResponse httpr = new PostData.PostDataTask().execute(new PostData.PostDataObj("http://ljtrust.org:3000/data", json)).get();
            if (httpr != null) {
                if (httpr.getStatusLine().getStatusCode() == 200) {
                    Log.i("DataBase", "Sending Successful");                                        //Request successful
                    clearDatabase();
                    return;
                }
            }
            Log.i("DataBase:", "There was a problem with this HTTP requst.");                       //Request failed

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void clearDatabase(){
        Log.i("DataBase", "SIZE "+ Integer.toString(calSqlAdapter.getDbSize()));                    //Prints size of database (rows)
        calSqlAdapter.delDbData();                                                                  // clears local database
        Log.i("DataBase", "Deleted");
    }

}
