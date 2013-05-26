package com.dailyinvention.wordpressthemechanger;

import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class AddBlogs extends Activity {
	public static final int DATABASE_VERSION = 16;

    public static final String CREATE_TABLE_SETTINGS = "create table accounts (id integer primary key autoincrement, url text, blogName text, username text, password text, selected integer)";
    
   // public static final String DATABASE_NAME = "wordpressthemechanger";
   
    // for capturing blogID, trac ticket #
    public static final String ADD_BLOGID = "alter table accounts add blogId integer;";
    public static final String UPDATE_BLOGID = "update accounts set blogId = 1;";

    // add field to store last used blog
    // private static final String ADD_LAST_BLOG_ID = "alter table eula add last_blog_id text;";

    // add home url to blog settings
    public static final String ADD_HOME_URL = "alter table accounts add homeURL text default '';";

	

    // private static final String ADD_BLOG_OPTIONS = "alter table accounts add blog_options text default '';";

    public SQLiteDatabase db;

    public EventDataSQLHelper eventsData;

    public String defaultBlog = "";

    public Context context = this;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addblogs);
        
	}
	
	protected void onResume() {
    	super.onResume();
    	
    	SQLiteDatabase.loadLibs(this);
        eventsData = new EventDataSQLHelper(this);
        
        SQLiteDatabase eventsDbase = eventsData.getReadableDatabase(Globals.PASSWORD_SECRET);
		   
	    Cursor cursor = getEvents(eventsDbase);
	    showEvents(cursor);
		
		Button cancel_add = (Button) findViewById(R.id.cancel);
        
        cancel_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View cancel_add) {
				Intent intent= new Intent(AddBlogs.this, Client.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
				startActivity(intent);
				
			    
				
			}
		});
        
        Button add = (Button) findViewById(R.id.add_blog_button);
       
        final EditText blogName = (EditText)findViewById(R.id.blog_name);
	    final Spinner httpSelect = (Spinner)findViewById(R.id.http_selector);
	    final EditText blogUrl = (EditText)findViewById(R.id.blog_url);
	    final EditText username = (EditText)findViewById(R.id.username);
	    final EditText password = (EditText)findViewById(R.id.password);
        
        
        add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View add_view) {
				addAccount(httpSelect.getSelectedItem().toString() + blogUrl.getText().toString(), httpSelect.getSelectedItem().toString() + blogUrl.getText().toString() + "/xmlrpc.php", blogName.getText().toString(), username.getText().toString(), password.getText().toString(), 1);
				
				SQLiteDatabase dbase = eventsData.getReadableDatabase(Globals.PASSWORD_SECRET);
				   
			    Cursor cursor = getEvents(dbase);
			    showEvents(cursor);
			    dbase.close();
			    eventsData.close();
			    Intent intent= new Intent(AddBlogs.this, Client.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			    startActivity(intent);
			    //ViewGroup mainFramework = (ViewGroup) findViewById (R.id.framework);
			    //mainFramework.invalidate();
			    //startActivity(new Intent(AddBlogs.this, Client.class));
			    
			    //dbase.close();
				//testAccount(httpSelect.getSelectedItem().toString() + blogUrl.getText().toString() + "/xmlrpc.php", httpSelect.getSelectedItem().toString() + blogUrl.getText().toString(), blogName.getText().toString(), username.getText().toString(), password.getText().toString(), 1);
				/* Log.v("Blog Name", blogName.getText().toString());
				Log.v("Blog Url", httpSelect.getSelectedItem().toString() + blogUrl.getText().toString());
				Log.v("Username", username.getText().toString());
				Log.v("Password", password.getText().toString());
				*/
				
			}
		});
    	
	}	
	
	@SuppressWarnings("deprecation")
	private Cursor getEvents(SQLiteDatabase db) {
	    
	    Cursor cursor = db.query(Globals.SETTINGS_TABLE, null, null, null, null,
	        null, null);
	    
	    startManagingCursor(cursor);
	    return cursor;
	  } 
	
	
	
	private void showEvents(Cursor cursor) {
	    StringBuilder ret = new StringBuilder("Saved Events:\n\n");
	    while (cursor.moveToNext()) {
	      long id = cursor.getLong(0);
	      String url = cursor.getString(1);
	      String homeURL = cursor.getString(2);
	      String blogName = cursor.getString(3);
	      String username = cursor.getString(4);
	      String password = cursor.getString(5);
	      String selected = cursor.getString(7);
	      ret.append(id + ": " + url + ": " + homeURL + ": " + blogName + ": " + username + ": " + password +  ": " + selected + "\n");
	    }
	    
	    Log.i("sqldemo",ret.toString());
	  }

	
	
	
	private void addAccount(String url, String homeURL, String blogName, String username,
            String password, int blogId) {
		
		
        
        SQLiteDatabase db = eventsData.getWritableDatabase(Globals.PASSWORD_SECRET);
        
        
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("homeURL", homeURL);
        values.put("blogName", blogName);
        values.put("username", username);
        values.put("password", password);
        values.put("blogId", blogId);
        values.put("selected", 1);
        //values.put("wpVersion", wpVersion);
        db.execSQL("UPDATE " + Globals.SETTINGS_TABLE + " SET selected='0' WHERE selected='1'");
        db.insert(Globals.SETTINGS_TABLE, null, values);
        db.close();
       
		//db.execSQL("insert into accounts (url,homeURL,blogName,username,password,blogID,selected) values('" + url + "','" + homeURL + "','" + blogName + "','" + username + "','" + password + "',1,1)");
         
        
        
    }
	
	
	
	
	
	
	static DialogInterface dialog = null;
	
}
