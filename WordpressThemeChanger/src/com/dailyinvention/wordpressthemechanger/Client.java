package com.dailyinvention.wordpressthemechanger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import net.sqlcipher.database.SQLiteDatabase;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;



public class Client extends Activity {
	String url;
	String username;
	String password;
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        
        InitializeSQLCipher();	
        Log.i("Info","Reset");
        
        
    }
    
    protected void onResume() {
    	super.onResume();
    	
        getBlogs();
        
        Button add_blog = (Button) findViewById(R.id.add_blog);
        
        add_blog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View add_button) {
				startActivity(new Intent(Client.this, AddBlogs.class));
				
			}
		});
        
        addButtons();
        
	    
	    // Normal case behavior follows
	}
    
    private String getActiveTheme(String url, String username, String password) {
    	String theme_name = null;
    	try {
			XMLRPCClient client = new XMLRPCClient(new URL(url));
			theme_name = (String) client.call("themes.getActiveTheme",username, password);
			
    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return theme_name;
    		
    }
    
    private void checkButton(View button, String status) {
    	if (status.equals("active")) {
    		Drawable image = getBaseContext().getResources().getDrawable( R.drawable.check );
            if (image == null) {
                throw new AssertionError();
            }
            image.setBounds( 0, 0, 30, 30 );
    		((TextView) button).setCompoundDrawables( null, null, image, null );
    	}
    	else {
    		((TextView) button).setCompoundDrawables( null, null, null, null );
    	}
    }
    
    private void switchTheme(String themeName, String url, String username, String password) {
    	try {
			XMLRPCClient client = new XMLRPCClient(new URL(url));
			String[] params = {username,password,themeName};
			String result = (String) client.call("themes.switchThemes",params);
    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	public String[][] getThemes() {
        String[][] keys = null;
        
        try {
        	XMLRPCClient client = new XMLRPCClient(new URL(url));
            Map<String,Object> result = (Map<String, Object>) client.call("themes.getThemes",username,password);
            //keys = result.values().toArray(new String[result.values().toArray().length]);
            String[][] arr = new String[result.size()][2];
            Set<Map.Entry<String,Object>> entries = result.entrySet();
            Iterator<Map.Entry<String,Object>> entriesIterator = entries.iterator();

            int i = 0;
            while(entriesIterator.hasNext()){

                Map.Entry<String, Object> mapping = entriesIterator.next();

                arr[i][0] = mapping.getKey();

                Log.i("Key", mapping.getKey());

                arr[i][1] = mapping.getValue().toString();

                i++;
            }
            keys = arr;
        } catch (XMLRPCException e) {
            //keys = new String[1][1];
            Log.i("Error", "XMLRPC error" + e);
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return keys;
    }

    private void getBlogs(){
        List<String> blogs = new ArrayList<String>();
         
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Globals.SETTINGS_TABLE + " WHERE selected='0'";
        String selectFirstQuery = "SELECT  * FROM " + Globals.SETTINGS_TABLE + " WHERE selected='1'";
        final EventDataSQLHelper eventsData = new EventDataSQLHelper(this);
        SQLiteDatabase dbase = eventsData.getReadableDatabase(Globals.PASSWORD_SECRET);
        Cursor cursor = dbase.rawQuery(selectQuery, null);
        Cursor first = dbase.rawQuery(selectFirstQuery, null);
      
        // looping through all rows and adding to list
        if (first.moveToFirst()) {
        	blogs.add(first.getString(3));
        	username = first.getString(4);
        	password = first.getString(5);
        	url = first.getString(2);
        }
        
        if (cursor.moveToFirst()) {
            do {
                blogs.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }
         
        // closing connection
        first.close();
        cursor.close();
        
        dbase.close();
         
        // returning lables
        //return blogs;
        
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, blogs);
     
           
        
        Spinner spinner = (Spinner) findViewById(R.id.blog_selector);
        spinner.setAdapter(dataAdapter);
        
        
        
	
        
	}


	
	public void addButtons() {
	ViewGroup linear = (ViewGroup) findViewById(R.id.button_layout);
    linear.removeAllViews();
    //LinearLayout linear = new LinearLayout(this);
	  //linear.setOrientation(LinearLayout.VERTICAL);
	  //linear.setPadding(0, 50, 0, 0);



	  int count = getThemes().length;




      int i = 0;
	  
	  final Button[] buttons = new Button[count];
	  
	  for(final String[] key: getThemes()) {
	  		buttons[i] = new Button(this);
	  		String active_theme = getActiveTheme(url, username, password);
	  		
	  		if (key[1].equals(active_theme)) {
	  			
	  			checkButton(buttons[i],"active");
	  		}
	  		
	  		buttons[i].setText(key[1]);	
	  		buttons[i].setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View clicked_button) {
                    Drawable image = getBaseContext().getResources().getDrawable(R.drawable.check);
                    image.setBounds(0, 0, 30, 30);
                    for (int n = 0; n < (buttons.length); ++n) {
                        checkButton(buttons[n], "inactive");
                    }

                    checkButton(clicked_button, "active");
                    switchTheme(key[0], url, username, password);

                }
            });
	  		
	  		linear.addView(buttons[i]);
	  		++i;
    }

	}
	
	private void InitializeSQLCipher() {
        SQLiteDatabase.loadLibs(this);
        File databaseFile = getDatabasePath(Globals.DATABASE_NAME);
        databaseFile.mkdirs();
        databaseFile.delete();
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, Globals.PASSWORD_SECRET, null);
        
        
        	database.execSQL("create table accounts (id integer primary key autoincrement, url text, homeURL text, blogName text, username text, password text, blogID integer, selected integer);");
        	database.execSQL("insert into accounts (url,homeURL,blogName,username,password,blogID,selected) values('http://blog.dailyinvention.com/','http://blog.dailyinvention.com/xmlrpc.php','Theme Changrr Site','bobafett','letmein59208xpq',1,1)");
        
       database.close(); 
   
	}
}