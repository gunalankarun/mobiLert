package com.minionhut.michael.mdp_hack.CalSQL;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by michael on 10/19/14.
 */
public class CalSqlAdapterWiFi {

    CalSqlHelper helper;
    private String GoogleAccountEmail;

    public CalSqlAdapterWiFi(Context context){
        helper = new CalSqlHelper(context);
    }

    public String getGoogleAccountEmail() {
        if (GoogleAccountEmail == null) {
            AccountManager am = (AccountManager) helper.context.getSystemService(helper.context.ACCOUNT_SERVICE);
            Account[] accounts = am.getAccounts();
            for (Account a : accounts) {
                if (a.type.equals("com.google")) {
                    GoogleAccountEmail = a.name;
                    return GoogleAccountEmail;
                }
            }
            return null;
        }
        return GoogleAccountEmail;
    }

    public long insertData(CalSQLObjWiFi SQLObj) {

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CalSqlHelper.ENTRY_SSID, SQLObj.getSSID());
        contentValues.put(CalSqlHelper.ENTRY_BSSID, SQLObj.getBSSID());
        contentValues.put(CalSqlHelper.ENTRY_CHANNEL, SQLObj.getChannel());
        contentValues.put(CalSqlHelper.ENTRY_RSSI, SQLObj.getRSSI());


        long id = db.insert(CalSqlHelper.TABLE_NAME, null, contentValues);
        db.close();
//        if (id != SQLObj.getTimestamp()) {
//            Log.e("CalSqlAdapter", "Error inserting row into NthSense! id returned is " + String.valueOf(id));
//        }
        return id;
    }

    public int getDbSize() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + CalSqlHelper.TABLE_NAME, null);
        int size = -1;
        if (c.moveToFirst()) {
            size = c.getInt(0);
        }
        c.close();
        return size;
    }

    public void delDbData() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM " + CalSqlHelper.TABLE_NAME);
        db.close();
    }
    public CalSQLObjWiFi getSingleData(int rowID) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + CalSqlHelper.TABLE_NAME + " WHERE " + CalSqlHelper.ENTRY_ROWID + "=" + rowID + " LIMIT 1", null);
        if (c.moveToFirst()) { //if statement makes sure that cursor is not null
            //Get all of the column indexes to ensure that you do not mix up the data
            //int rowIdCol = c.getColumnIndex(CalSqlHelper.ENTRY_ROWID);
            int SSIDCol = c.getColumnIndex(CalSqlHelper.ENTRY_SSID);
            int BSSIDCol = c.getColumnIndex(CalSqlHelper.ENTRY_BSSID);
            int ChannelCol = c.getColumnIndex(CalSqlHelper.ENTRY_CHANNEL);
            int RSSICol = c.getColumnIndex(CalSqlHelper.ENTRY_RSSI);

            CalSQLObjWiFi so = new CalSQLObjWiFi(c.getString(SSIDCol),c.getString(BSSIDCol),c.getInt(ChannelCol),
                    c.getInt(RSSICol));
            c.close();
            return so;
        }
        return null;
    }

    public CalSQLObjWiFi[] getRangeData(int startRow, int endRow) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] selctionArgs={Integer.toString(startRow), Integer.toString(endRow)}; //This allows variables to be "fed" into the SQL query easier
        Cursor c = db.rawQuery("SELECT * FROM " + CalSqlHelper.TABLE_NAME + " WHERE " + CalSqlHelper.ENTRY_ROWID +
                " BETWEEN ? AND ?", selctionArgs); //Insert ? where you want each value of selctionArgs to be inserted
        if (c.getCount() == 0) { //Select statement did not return any results
            c.close();
            return null;
        }
        else { //Select statement has results
            CalSQLObjWiFi[] returnArray = new CalSQLObjWiFi[c.getCount()];
            for (int arrayIndex = 0; arrayIndex < c.getCount(); arrayIndex++) {
                if (c.moveToNext()) {
                    int SSIDCol = c.getColumnIndex(CalSqlHelper.ENTRY_SSID);
                    int BSSIDCol = c.getColumnIndex(CalSqlHelper.ENTRY_BSSID);
                    int ChannelCol = c.getColumnIndex(CalSqlHelper.ENTRY_CHANNEL);
                    int RSSICol = c.getColumnIndex(CalSqlHelper.ENTRY_RSSI);

                    CalSQLObjWiFi SQLObj = new CalSQLObjWiFi(c.getString(SSIDCol),c.getString(BSSIDCol),c.getInt(ChannelCol),
                            c.getInt(RSSICol));
                    returnArray[arrayIndex] = SQLObj;
                }
            }
            c.close();
            return returnArray;
        }
    }
    //TODO: This function has not been tested, use with caution!
    public String createCSVString(CalSQLObj[] CalSQLObjArray) {
        String outputString = "";
        for (CalSQLObj SQLObj : CalSQLObjArray) {
            //TODO: While this data should be all numberical, it is best practice to have a function to parse data and escape special characters such as commas
            //Output format: timeStamp, xVal, yVal, zVal, proxVal, luxVal, isWalking, isTraining
            outputString = outputString + SQLObj.getTimestamp() + ", " + SQLObj.getxVal() + ", " + SQLObj.getyVal() + ", " + SQLObj.getzVal() +
                    ", " + SQLObj.getProxVal() + ", " + SQLObj.getLuxVal() + ", " + SQLObj.getIsWalking() + ", " + SQLObj.getIsTraining() + "/n";
        }
        return outputString;
    }

    public JSONArray createJSONObjWithEmail(CalSQLObjWiFi[] CalSQLObjArray) {
        JSONArray ja = new JSONArray();
        if (CalSQLObjArray != null) {
            for (CalSQLObjWiFi SQLObj : CalSQLObjArray) {
                //Output format: email, timestamp, xVal, yVal, zVal, proxVal, luxVal, isWalking, isTraining

                JSONObject jo = new JSONObject();
                try {
                    jo.put("email", getGoogleAccountEmail());
                    jo.put("SSID", SQLObj.getSSID());
                    jo.put("BSSID", SQLObj.getBSSID());
                    jo.put("channel", SQLObj.getChannel());
                    jo.put("RSSI", SQLObj.getRSSI());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ja.put(jo);
            }
        }
        return ja;
    }

    static class CalSqlHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "cal.db";
        private static final String TABLE_NAME = "netStat";

        private static final String ENTRY_ROWID = "rowid";
        private static final String ENTRY_SSID = "SSID";
        private static final String ENTRY_BSSID = "BSSID";
        private static final String ENTRY_CHANNEL = "channel";
        private static final String ENTRY_RSSI = "RSSI";


        private static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " (" +
                ENTRY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ENTRY_SSID + " TEXT, " +
                ENTRY_BSSID + " TEXT, " +
                ENTRY_CHANNEL + " INTEGER, " +
                ENTRY_RSSI + " INTEGER, " +
                ");";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private static final int DATABASE_VERSION = 1;

        private Context context;

        public CalSqlHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try {
                sqLiteDatabase.execSQL(CREATE_TABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            try {
                sqLiteDatabase.execSQL(DROP_TABLE);

                onCreate(sqLiteDatabase);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
