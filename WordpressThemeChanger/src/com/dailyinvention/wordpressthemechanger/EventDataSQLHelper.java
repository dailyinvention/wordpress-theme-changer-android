package com.dailyinvention.wordpressthemechanger;

import java.io.File;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/** Helper to the database, manages versions and creation */
public class EventDataSQLHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = Globals.DATABASE_NAME;
	public static final int DATABASE_VERSION = 16;
	public static final String ADD_BLOGID = "alter table accounts add blogId integer;";
    public static final String UPDATE_BLOGID = "update accounts set blogId = 1;";
    public static final String ADD_HOME_URL = "alter table accounts add homeURL text default '';";

    

	public EventDataSQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
		if (oldVersion >= newVersion)
			return;

		String sql = null;
		try {
            if (db.getVersion() < 1) { // user is new install
                db.execSQL(ADD_BLOGID);
                db.execSQL(UPDATE_BLOGID);
                db.execSQL(ADD_HOME_URL);
                
                db.setVersion(DATABASE_VERSION); // set to latest revision
            } else if (db.getVersion() == 1) { // v1.0 or v1.0.1
                db.execSQL(ADD_BLOGID);
                db.execSQL(UPDATE_BLOGID);
                db.execSQL(ADD_HOME_URL);
                
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 2) {
                db.execSQL(ADD_HOME_URL);
                
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 3) {
                db.execSQL(ADD_HOME_URL);
                
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 4) {
                db.execSQL(ADD_HOME_URL);
                
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 5) {
                db.execSQL(ADD_HOME_URL);
                
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 6) {
                db.execSQL(ADD_HOME_URL);
                
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 7) {
                db.execSQL(ADD_HOME_URL);
                
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 8) {
                db.execSQL(ADD_HOME_URL);
                
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 9) {
                db.execSQL(ADD_HOME_URL);
                
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 10) {
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 11) {
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 12) {
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 13) {
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 14) {
                db.setVersion(DATABASE_VERSION);
            } else if (db.getVersion() == 15) {
                db.setVersion(DATABASE_VERSION);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
		Log.d("EventsData", "onUpgrade	: " + sql);
		if (sql != null)
			db.execSQL(sql);	
		
	}
	
	
	

}
