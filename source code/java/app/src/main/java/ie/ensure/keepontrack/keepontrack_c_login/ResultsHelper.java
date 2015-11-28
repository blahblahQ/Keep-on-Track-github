package ie.ensure.keepontrack.keepontrack_c_login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;


public class ResultsHelper extends SQLiteOpenHelper{

    private static final String TAG = ResultsHelper.class.getSimpleName();

    // Database Name
    private static final String DATABASE_NAME = "android_results";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Results Table Name
    private static final String TABLE_RESULTS = "results";

    // Results Table Column Names
    public static final String KEY_JOURNEY  = "jRjourney"; // Name
    public static final String KEY_SCORE    = "jRscore";   // username
    public static final String KEY_STATUS   = "jRstatus";  // policy number
    public static final String KEY_DATE     = "jRdate";    // Email


    public ResultsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RESULTS_TABLE = "CREATE TABLE " + TABLE_RESULTS + "("
                + KEY_JOURNEY + " INTEGER PRIMARY KEY," + KEY_SCORE + " INTEGER," + KEY_STATUS + " INTEGER," + KEY_DATE + " TEXT" + ")";

        db.execSQL(CREATE_RESULTS_TABLE);
        Log.d(TAG, "Results Database table created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        // Create tables again
        onCreate(db);
    }

    public void addResult(String jRjourney, String jRscore, String jRstatus, String jRdate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_JOURNEY, jRjourney);         // Journey ID
        values.put(KEY_SCORE, jRscore);             // Score
        values.put(KEY_STATUS, jRstatus);           // Status
        values.put(KEY_DATE, jRdate);               // Date

        Log.d(TAG, "Result to Store: " + values.toString());

        // Inserting Row
        long row = db.insert(TABLE_RESULTS, null, values);
        //db.close(); // Closing database connection

        Log.d(TAG, "New Result inserted: success:" + row + " " + values.toString());
    }

    public HashMap<String, String> getResultDetails() {
        HashMap<String, String> result = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_RESULTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            result.put("journeyid", cursor.getString(1));
            result.put("score", cursor.getString(2));
            result.put("status", cursor.getString(3));
            result.put("date", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching result from Sqlite: " + result.toString());

        return result;
    }

    public Cursor getAllResults() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_RESULTS, null);
        Log.d(TAG, "Results Query Sent:");
        return res;
    }
    public void deleteResults() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_RESULTS, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }
}
