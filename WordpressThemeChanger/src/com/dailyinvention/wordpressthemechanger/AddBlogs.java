package com.dailyinvention.wordpressthemechanger;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutionException;


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

        ImageButton info = (ImageButton) findViewById(R.id.info);
        Button cancel_add = (Button) findViewById(R.id.cancel);

        info.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View infoButton) {

                final TextView message = new TextView(AddBlogs.this);
                message.setTextColor(Color.WHITE);
                message.setPadding(10,10,10,10);

                // i.e.: R.string.dialog_message =>
                // "Test this dialog following the link to dtmilano.blogspot.com"
                final SpannableString s =
                        new SpannableString(AddBlogs.this.getText(R.string.about_message));
                Linkify.addLinks(s, Linkify.WEB_URLS);
                message.setText(s);
                message.setMovementMethod(LinkMovementMethod.getInstance());
                message.setLinkTextColor(Color.LTGRAY);

                AlertDialog.Builder popupBuilder = new AlertDialog.Builder(AddBlogs.this);
                popupBuilder.setTitle("About")
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();

                            }
                        })
                        .setView(message);
                AlertDialog aboutMessage =popupBuilder.create();
                aboutMessage.show();


            }
        });

        
        cancel_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View cancel_add) {
                finish();
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

                String success = "false";
                String checkSuccess = "false";
                try {
                    checkSuccess = new checkDuplicateName()
                            .execute(blogName.getText().toString())
                            .get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(checkSuccess == "false") {

                try {
                    success = new checkBlog()
                            .execute(httpSelect.getSelectedItem().toString(), blogUrl.getText().toString(), username.getText().toString(), password.getText().toString())
                            .get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                if(success == "true") {

				new addAccount().execute(httpSelect.getSelectedItem().toString() + blogUrl.getText().toString(), httpSelect.getSelectedItem().toString() + blogUrl.getText().toString() + "/xmlrpc.php", blogName.getText().toString(), username.getText().toString(), password.getText().toString(), "1");


                }
                else if (success == "false"){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddBlogs.this);
                    builder.setTitle("Cannot Connect to Blog")
                            .setMessage("Unable to connect to Theme Changrr plugin using the blog url of " + httpSelect.getSelectedItem() + blogUrl.getText() + ".  Make sure the Theme Changrr plugin is set up for this blog and the username and password are correct.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    blogName.setText("");
                                    blogUrl.setText("");
                                    username.setText("");
                                    password.setText("");

                                }
                            })
                            .setNegativeButton("Set up Plugin", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://themechangrr.dailyinvention.com/setup/"));
                                    startActivity(browserIntent);

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
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

                if(checkSuccess == "true") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddBlogs.this);
                    builder.setTitle("Duplicate Blog Name")
                            .setMessage("The blog name \"" + blogName.getText().toString() + "\" is already in your list.  Please enter a different blog name.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    blogName.setText("");
                                    dialog.cancel();

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
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

	
	
	
	private class addAccount extends AsyncTask<String,Void,String> {
        ProgressDialog dialog;
        protected void onPreExecute() {
            dialog = new ProgressDialog(AddBlogs.this);
            dialog.setTitle("Adding blog...");
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.show();
        }
        protected String doInBackground(String... blogValue) {
        final EventDataSQLHelper eventsData = new EventDataSQLHelper(AddBlogs.this);
        SQLiteDatabase db = eventsData.getWritableDatabase(Globals.PASSWORD_SECRET);
        Log.i("SQL Values", blogValue[0] + ":" + blogValue[1] + blogValue[2] + ":" + blogValue[3] + blogValue[4] + ":" + blogValue[5]);
        ContentValues values = new ContentValues();
        values.put("url", blogValue[0]);
        values.put("homeURL", blogValue[1]);
        values.put("blogName", blogValue[2]);
        values.put("username", blogValue[3]);
        values.put("password", blogValue[4]);
        values.put("blogId", Integer.parseInt(blogValue[5]));
        values.put("selected", 1);
        //values.put("wpVersion", wpVersion);
        db.execSQL("UPDATE " + Globals.SETTINGS_TABLE + " SET selected='0' WHERE selected='1'");
        db.insert(Globals.SETTINGS_TABLE, null, values);
        db.close();
            String result = "Completed!";
            return result;
        }

        protected void onPostExecute(String result) {
            dialog.dismiss();
            finish();
            Intent intent= new Intent(AddBlogs.this, Client.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            startActivity(intent);


        }


		//db.execSQL("insert into accounts (url,homeURL,blogName,username,password,blogID,selected) values('" + url + "','" + homeURL + "','" + blogName + "','" + username + "','" + password + "',1,1)");
        
    }


    private class checkDuplicateName extends AsyncTask<String,Void,String> {
        protected String doInBackground(String... blogName) {
            String success = "false";
            String blogNameReturn = null;
            String selectedQuery = "SELECT blogName FROM " + Globals.SETTINGS_TABLE + " WHERE blogName='" + blogName[0] + "'";
            final EventDataSQLHelper eventsData = new EventDataSQLHelper(AddBlogs.this);
            SQLiteDatabase dbase = eventsData.getReadableDatabase(Globals.PASSWORD_SECRET);
            Cursor selectedblogName = dbase.rawQuery(selectedQuery, null);
            if (selectedblogName.moveToFirst()) {
                blogNameReturn = selectedblogName.getString(0);
                Log.i("Blog Name", blogNameReturn);
            }

            if (blogNameReturn != null) {
                success = "true";
            }

            selectedblogName.close();
            dbase.close();
            String result = success;
            return result;
        }

        protected void onPostExecute(String result) {

        }
    }


    private class checkBlog extends AsyncTask<String,Void,String> {
        protected String doInBackground(String... accessValue) {
            String success = "false";

            try {
                String urlCombined = (String) accessValue[0] + accessValue[1] + "/xmlrpc.php";
                XMLRPCClient client = new XMLRPCClient(new URL(urlCombined));
                String themeCallback = (String) client.call("themes.getActiveTheme",accessValue[2], accessValue[3]);
                success = "true";
                Log.i("Username",accessValue[2]);


            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            } catch (XMLRPCException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }

            return success;
        }
        protected void onPostExecute(String result) {


        }

    }
	
	

	
	static DialogInterface dialog = null;
	
}
