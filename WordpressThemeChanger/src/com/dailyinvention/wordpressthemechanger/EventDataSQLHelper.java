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
            switch (db.getVersion()) {

                case 0:
                db.execSQL(ADD_BLOGID);
                db.execSQL(UPDATE_BLOGID);
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION); // set to latest revision
                break;
                case 1:
                db.execSQL(ADD_BLOGID);
                db.execSQL(UPDATE_BLOGID);
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 2:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 3:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 4:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 5:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 6:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 7:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 8:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 9:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 10:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 11:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 12:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 13:
                db.execSQL(ADD_HOME_URL);
                db.setVersion(DATABASE_VERSION);
                break;
                case 14:
                db.setVersion(DATABASE_VERSION);
                break;
                case 15:
                db.setVersion(DATABASE_VERSION);
                break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
		
		Log.d("EventsData", "onUpgrade	: " + sql);
		if (sql != null)
			db.execSQL(sql);	
		
	}
	
	
	

}
