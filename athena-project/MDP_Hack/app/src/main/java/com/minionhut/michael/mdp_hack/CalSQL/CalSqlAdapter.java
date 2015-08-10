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
public class CalSqlAdapter {

    CalSqlHelper helper;
    private String GoogleAccountEmail;

    public CalSqlAdapter(Context context){
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

    public long insertData(CalSQLObj SQLObj) {

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CalSqlHelper.ENTRY_TIMESTAMP, SQLObj.getTimestamp());
        contentValues.put(CalSqlHelper.ENTRY_XVAL, SQLObj.getxVal());
        contentValues.put(CalSqlHelper.ENTRY_YVAL, SQLObj.getyVal());
        contentValues.put(CalSqlHelper.ENTRY_ZVAL, SQLObj.getzVal());
        contentValues.put(CalSqlHelper.ENTRY_PROXVAL, SQLObj.getProxVal());
        contentValues.put(CalSqlHelper.ENTRY_LUXVAL, SQLObj.getLuxVal());
        contentValues.put(CalSqlHelper.ENTRY_ISWALKING, SQLObj.getIsWalking());
        contentValues.put(CalSqlHelper.ENTRY_ISTRAINING, SQLObj.getIsTraining());
        contentValues.put(CalSqlHelper.ENTRY_DELTASTEPS, SQLObj.getDeltaSteps());

        long id = db.insert(CalSqlHelper.TABLE_NAME, null, contentValues);
        db.close();
        if (id != SQLObj.getTimestamp()) {
            Log.e("CalSqlAdapter", "Error inserting row into NthSense! id returned is " + String.valueOf(id));
        }
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
    public CalSQLObj getSingleData(long timestamp) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + CalSqlHelper.TABLE_NAME + " WHERE " + CalSqlHelper.ENTRY_TIMESTAMP + "=" + timestamp + " LIMIT 1", null);
        if (c.moveToFirst()) { //if statement makes sure that cursor is not null
            //Get all of the column indexes to ensure that you do not mix up the data
            int timestampCol = c.getColumnIndex(CalSqlHelper.ENTRY_TIMESTAMP);
            int xValCol = c.getColumnIndex(CalSqlHelper.ENTRY_XVAL);
            int yValCol = c.getColumnIndex(CalSqlHelper.ENTRY_YVAL);
            int zValCol = c.getColumnIndex(CalSqlHelper.ENTRY_ZVAL);
            int proxValCol = c.getColumnIndex(CalSqlHelper.ENTRY_PROXVAL);
            int luxValCol = c.getColumnIndex(CalSqlHelper.ENTRY_LUXVAL);
            int isWalkingCol = c.getColumnIndex(CalSqlHelper.ENTRY_ISWALKING);
            int isTrainingCol = c.getColumnIndex(CalSqlHelper.ENTRY_ISTRAINING);
            int deltaStepsCol = c.getColumnIndex(CalSqlHelper.ENTRY_DELTASTEPS);
            CalSQLObj so = new CalSQLObj(c.getFloat(xValCol),c.getFloat(yValCol),c.getFloat(zValCol),
                    c.getFloat(proxValCol),c.getFloat(luxValCol),c.getInt(isWalkingCol),c.getInt(isTrainingCol),c.getFloat(deltaStepsCol),c.getLong(timestampCol));
            c.close();
            return so;
        }
        return null;
    }

    public CalSQLObj[] getRangeData(long startTimestamp, long endTimestamp) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] selctionArgs={Long.toString(startTimestamp), Long.toString(endTimestamp)}; //This allows variables to be "fed" into the SQL query easier
        Cursor c = db.rawQuery("SELECT * FROM " + CalSqlHelper.TABLE_NAME + " WHERE " + CalSqlHelper.ENTRY_TIMESTAMP +
                " BETWEEN ? AND ?", selctionArgs); //Insert ? where you want each value of selctionArgs to be inserted
        if (c.getCount() == 0) { //Select statement did not return any results
            c.close();
            return null;
        }
        else { //Select statement has results
            CalSQLObj[] returnArray = new CalSQLObj[c.getCount()];
            for (int arrayIndex = 0; arrayIndex < c.getCount(); arrayIndex++) {
                if (c.moveToNext()) {
                    int timestampCol = c.getColumnIndex(CalSqlHelper.ENTRY_TIMESTAMP);
                    int xValCol = c.getColumnIndex(CalSqlHelper.ENTRY_XVAL);
                    int yValCol = c.getColumnIndex(CalSqlHelper.ENTRY_YVAL);
                    int zValCol = c.getColumnIndex(CalSqlHelper.ENTRY_ZVAL);
                    int proxValCol = c.getColumnIndex(CalSqlHelper.ENTRY_PROXVAL);
                    int luxValCol = c.getColumnIndex(CalSqlHelper.ENTRY_LUXVAL);
                    int isWalkingCol = c.getColumnIndex(CalSqlHelper.ENTRY_ISWALKING);
                    int isTrainingCol = c.getColumnIndex(CalSqlHelper.ENTRY_ISTRAINING);
                    int deltaStepsCol = c.getColumnIndex(CalSqlHelper.ENTRY_DELTASTEPS);

                    CalSQLObj SQLObj = new CalSQLObj(c.getFloat(xValCol), c.getFloat(yValCol), c.getFloat(zValCol),
                            c.getFloat(proxValCol), c.getFloat(luxValCol), c.getInt(isWalkingCol), c.getInt(isTrainingCol), c.getFloat(deltaStepsCol),c.getLong(timestampCol));
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
            //Output format: timeStamp, xVal, yVal, zVal, proxVal, luxVal, isWalking, isTraining, deltaSteps
            outputString = outputString + SQLObj.getTimestamp() + ", " + SQLObj.getxVal() + ", " + SQLObj.getyVal() + ", " + SQLObj.getzVal() +
                    ", " + SQLObj.getProxVal() + ", " + SQLObj.getLuxVal() + ", " + SQLObj.getIsWalking() + ", " + SQLObj.getIsTraining() + ", " + SQLObj.getDeltaSteps() + "/n";
        }
        return outputString;
    }

    public JSONArray createJSONObjWithEmail(CalSQLObj[] CalSQLObjArray) {
        JSONArray ja = new JSONArray();
        if (CalSQLObjArray != null) {
            for (CalSQLObj SQLObj : CalSQLObjArray) {
                //Output format: email, timestamp, xVal, yVal, zVal, proxVal, luxVal, isWalking, isTraining, deltaSteps

                JSONObject jo = new JSONObject();
                try {
                    jo.put("email", getGoogleAccountEmail());
                    jo.put("timestamp", SQLObj.getTimestamp());
                    jo.put("xVal", SQLObj.getxVal());
                    jo.put("yVal", SQLObj.getyVal());
                    jo.put("zVal", SQLObj.getzVal());
                    jo.put("deltaSteps", SQLObj.getDeltaSteps());
//                    jo.put("luxVal", SQLObj.getLuxVal());
//                    jo.put("proxVal", SQLObj.getProxVal());
//                    jo.put("isWalking", SQLObj.getIsWalking());
//                    jo.put("isTraining", SQLObj.getIsTraining());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ja.put(jo);
            }
        }
        return ja;
    }

    static class CalSqlHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "mdp.db";
        private static final String TABLE_NAME = "NthSense";

        private static final String ENTRY_TIMESTAMP = "timestamp";
        private static final String ENTRY_XVAL = "xVal";
        private static final String ENTRY_YVAL = "yVal";
        private static final String ENTRY_ZVAL = "zVal";
        private static final String ENTRY_PROXVAL = "proxVal";
        private static final String ENTRY_LUXVAL = "luxVal";
        private static final String ENTRY_ISWALKING = "isWalking";
        private static final String ENTRY_ISTRAINING = "isTraining";
        private static final String ENTRY_DELTASTEPS = "deltaSteps";

        private static final String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME + " (" +
                ENTRY_TIMESTAMP + " INTEGER PRIMARY KEY," +
                ENTRY_XVAL + " REAL" + ", " +
                ENTRY_YVAL + " REAL" + ", " +
                ENTRY_ZVAL + " REAL" + ", " +
                ENTRY_PROXVAL + " REAL" + ", " +
                ENTRY_LUXVAL + " REAL" + ", " +
                ENTRY_ISWALKING + " INTEGER" + ", " +
                ENTRY_ISTRAINING + " INTEGER"+", " +
                ENTRY_DELTASTEPS + " REAL " +
                ");";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private static final int DATABASE_VERSION = 5;

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
                context.deleteDatabase(TABLE_NAME) ;
                onCreate(sqLiteDatabase);
                if (newVersion > oldVersion) {
                    sqLiteDatabase.execSQL("ALTER TABLE NthSense ADD COLUMN deltaSteps REAL DEFAULT 0");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
