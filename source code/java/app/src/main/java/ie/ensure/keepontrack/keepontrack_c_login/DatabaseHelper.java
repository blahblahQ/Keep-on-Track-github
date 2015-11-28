package ie.ensure.keepontrack.keepontrack_c_login;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "User.db";
    public static final String TABLE_NAME = "Journey_table";
    public static final String COL_1 = "JOURNEYID";
    public static final String COL_2 = "SAMPLEID";
    public static final String COL_3 = "TIMESTAMP";
    public static final String COL_4 = "LATITUDE";
    public static final String COL_5 = "LONGITUDE";
    public static final String COL_6 = "MAXx";
    public static final String COL_7 = "MAXy";
    public static final String COL_8 = "MAXz";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(JOURNEYID INTEGER, SAMPLEID INTEGER PRIMARY KEY AUTOINCREMENT, TIMESTAMP FLOAT, LATITUDE FLOAT, LONGITUDE FLOAT, MAXx FLOAT, MAXy FLOAT, MAXz FLOAT)"); //PRIMARY KEY AUTOINCREMENT
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String timestamp, String latitude, String longitude, float XMax, float YMax, float ZMax)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        Cursor res = db.rawQuery("SELECT JOURNEYID FROM " + TABLE_NAME + " LIMIT 1" ,null);
        res.moveToFirst();
        String place = res.getString(0);

        contentValues.put(COL_1, place);
        contentValues.put(COL_3, timestamp);
        contentValues.put(COL_4, latitude);
        contentValues.put(COL_5, longitude);
        contentValues.put(COL_6, XMax);
        contentValues.put(COL_7, YMax);
        contentValues.put(COL_8, ZMax);
        long result = db.insert(TABLE_NAME,null,contentValues);

        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }

    public void insertJourneyId(String journeyid){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, journeyid);
        db.insert(TABLE_NAME, null, contentValues);
        }

    public String getJourneyId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor ID = db.rawQuery("SELECT JOURNEYID FROM " + TABLE_NAME + " LIMIT 1", null);
        ID.moveToFirst();
        String JourneyId = ID.getString(0);
        return JourneyId;
    }
    public void deleteJourneyData() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}