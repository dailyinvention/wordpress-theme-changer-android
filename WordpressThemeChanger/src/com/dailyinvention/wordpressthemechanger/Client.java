package com.dailyinvention.wordpressthemechanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
    String[][] themes = null;
    String activeTheme = null;
    ProgressDialog dialog;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        InitializeSQLCipher();



    }

    protected void onStop(){
        super.onStop();
        if (this.isFinishing()){
            finish();
        }
    }


    protected void onResume() {
    	super.onResume();



        if(isOnline()) {


         getBlogs();

        ImageButton info = (ImageButton) findViewById(R.id.info);
        Button add_blog = (Button) findViewById(R.id.add_blog);
        Button remove_blog = (Button) findViewById(R.id.remove_blog);
        Spinner theme_selector = (Spinner) findViewById(R.id.blog_selector);

        info.setBackgroundColor(Color.TRANSPARENT);
        info.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View infoButton) {

                    final TextView message = new TextView(Client.this);
                    message.setTextColor(Color.WHITE);
                    message.setPadding(10,10,10,10);

                    // i.e.: R.string.dialog_message =>
                    // "Test this dialog following the link to dtmilano.blogspot.com"
                    final SpannableString s =
                            new SpannableString(Client.this.getText(R.string.about_message));
                    Linkify.addLinks(s, Linkify.WEB_URLS);
                    message.setText(s);
                    message.setMovementMethod(LinkMovementMethod.getInstance());
                    message.setLinkTextColor(Color.LTGRAY);

                    AlertDialog.Builder popupBuilder = new AlertDialog.Builder(Client.this);
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


        add_blog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View add_button) {
                //resume = true;
                //selectValue = false;
				startActivity(new Intent(Client.this, AddBlogs.class));

			}
		});

        remove_blog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View add_button) {
                String[] selectedBlog = getSelectedBlog();
                int selectedId = Integer.parseInt(selectedBlog[0]);
                if(selectedId == 1) {
                    AlertDialog.Builder defaultBlogAlert = new AlertDialog.Builder(Client.this);
                    defaultBlogAlert.setTitle("Cannot Remove Blog")
                            .setMessage("You may only remove blogs you have added.  You cannot remove the \"Theme Changrr Site\" blog from your list.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();

                                }
                            });
                    AlertDialog alert = defaultBlogAlert.create();
                    alert.show();

                }
                else {
                AlertDialog.Builder removeAlert = new AlertDialog.Builder(Client.this);
                removeAlert.setTitle("Delete Blog")
                        .setMessage("Are you sure you want remove \"" + selectedBlog[3] + "\" from your blog list?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                removeBlog();
                                getBlogs();


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();

                            }
                        });
                AlertDialog alert = removeAlert.create();
                alert.show();
                }

            }
        });



        //if (selectValue == true) {
        /*
        theme_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getItemAtPosition(position).toString();
                setBlog(selected);

                    new getThemes().execute();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {


                //
            }

        });

        //}
        */
           // new getThemes().execute();







        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Data Connection")
                    .setMessage("This application requires at least a 3g or wifi data connection to use.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

	    // Normal case behavior follows
	}

    private class getActiveTheme extends AsyncTask<String,Void,String> {
        String theme_name;
        protected String doInBackground(String... urls) {

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
        activeTheme = theme_name;
		return theme_name;
        }

        protected void onPostExecute(String result) {
            addButtons();
        }

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

    private class switchTheme extends AsyncTask<String,Void,Void> {
        protected void onPreExecute() {

        }

        protected Void doInBackground(String... themeName) {
    	try {
			XMLRPCClient client = new XMLRPCClient(new URL(url));
			String[] params = {username,password,themeName[0]};
			String result = (String) client.call("themes.switchThemes",params);

    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

            return null;
        }

        protected void onPostExecute() {

        }
    }



    public class getThemes extends AsyncTask<String,Void,String> {



        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Client.this);
            dialog.setTitle("Getting blog themes...");
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(true);
            dialog.show();

        }

        protected String doInBackground(String...params) {

            Log.i("BlogName Parameters",params[0]);

            setBlog(params[0]);

        try {
        	XMLRPCClient client = new XMLRPCClient(new URL(url));
            Map<String,Object> returnedThemes = (Map<String, Object>) client.call("themes.getThemes",username,password);
            String[][] arr = new String[returnedThemes.size()][2];
            Set<Map.Entry<String,Object>> entries = returnedThemes.entrySet();
            Iterator<Map.Entry<String,Object>> entriesIterator = entries.iterator();
            int i = 0;
            while(entriesIterator.hasNext()){

                Map.Entry<String, Object> mapping = entriesIterator.next();

                arr[i][0] = mapping.getKey();

                Log.i("Key", mapping.getKey());

                arr[i][1] = mapping.getValue().toString();

                i++;
            }

            themes = arr;

            //keys = result.values().toArray(new String[result.values().toArray().length]);


        } catch (XMLRPCException e) {
            //keys = new String[1][1];
            Log.i("Error", "XMLRPC error" + e);
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            String result = "Task Completed";
            return result;
        }

        protected void onPostExecute(String result) {

            new getActiveTheme().execute();

        }




    }

    private String[] getSelectedBlog(){
        String selectedQuery = "SELECT  * FROM " + Globals.SETTINGS_TABLE + " WHERE selected='1'";
        final EventDataSQLHelper eventsData = new EventDataSQLHelper(this);
        SQLiteDatabase dbase = eventsData.getReadableDatabase(Globals.PASSWORD_SECRET);

        Cursor selectedRow = dbase.rawQuery(selectedQuery, null);

        Integer id = null;
        String url = null;
        String homeURL = null;
        String blogName = null;
        String username = null;
        String password = null;

        if (selectedRow.moveToFirst()) {
            id = selectedRow.getInt(0);
            url = selectedRow.getString(1);
            homeURL = selectedRow.getString(2);
            blogName = selectedRow.getString(3);
            username = selectedRow.getString(4);
            password = selectedRow.getString(5);

        }
       selectedRow.close();
       dbase.close();

       String[] result = {id.toString(),url,homeURL,blogName,username,password};

       return result;



    }

    private void getBlogs(){
        List<String> blogs = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Globals.SETTINGS_TABLE + " WHERE selected='0'";
        //String selectFirstQuery = "SELECT  * FROM " + Globals.SETTINGS_TABLE + " WHERE selected='1'";
        final EventDataSQLHelper eventsData = new EventDataSQLHelper(this);
        SQLiteDatabase dbase = eventsData.getReadableDatabase(Globals.PASSWORD_SECRET);
        Cursor cursor = dbase.rawQuery(selectQuery, null);
        //Cursor first = dbase.rawQuery(selectFirstQuery, null);
        String[] first = getSelectedBlog();

        // looping through all rows and adding to list

        blogs.add(first[3]);
        username = first[4];
        password = first[5];
        url = first[2];



        if (cursor.moveToFirst()) {
            do {
                blogs.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();

        dbase.close();

        // returning lables
        //return blogs;

        Spinner spinner = (Spinner) findViewById(R.id.blog_selector);



        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, blogs);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(null);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getItemAtPosition(position).toString();



                    new getThemes().execute(selected);



            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {


            }

        });




	}

    private void setBlog(String blogName) {
        String clearSelectedQuery = "UPDATE " + Globals.SETTINGS_TABLE + " SET selected='0' WHERE selected='1'";
        String setQuery = "UPDATE " + Globals.SETTINGS_TABLE + " SET selected='1' WHERE blogName='" + blogName + "'";
        String getQuery = "SELECT * FROM " + Globals.SETTINGS_TABLE + " WHERE blogName='" + blogName + "'";


        Log.i("Blogname",blogName);

        final EventDataSQLHelper eventsData = new EventDataSQLHelper(this);
        SQLiteDatabase dbase = eventsData.getWritableDatabase(Globals.PASSWORD_SECRET);

        dbase.execSQL(clearSelectedQuery);
        dbase.execSQL(setQuery);

        Cursor getURLFetch = dbase.rawQuery(getQuery, null);

        // looping through all rows and adding to list
        if (getURLFetch.moveToFirst()) {
            url = getURLFetch.getString(2);
            username = getURLFetch.getString(4);
            password = getURLFetch.getString(5);

        }

        getURLFetch.close();
        dbase.close();

    }

    private void removeBlog(){

        String removeQuery = "DELETE FROM " + Globals.SETTINGS_TABLE + " WHERE selected='1'";
        String lastIDQuery = "SELECT * FROM " + Globals.SETTINGS_TABLE + " ORDER by id DESC LIMIT 1";
        String[] selectedBlog = getSelectedBlog();
        int selectedId = Integer.parseInt(selectedBlog[0]) - 1;


        final EventDataSQLHelper eventsData = new EventDataSQLHelper(this);
        SQLiteDatabase dbase = eventsData.getWritableDatabase(Globals.PASSWORD_SECRET);

        dbase.execSQL(removeQuery);
        Cursor lastIdFetch = dbase.rawQuery(lastIDQuery, null);
        Integer lastID = null;
        // looping through all rows and adding to list
        if (lastIdFetch.moveToFirst()) {
            lastID = lastIdFetch.getInt(0);
            url = lastIdFetch.getString(2);
            username = lastIdFetch.getString(4);
            password = lastIdFetch.getString(5);
        }

        dbase.execSQL("UPDATE " + Globals.SETTINGS_TABLE + " SET selected='1' WHERE id='" + lastID + "'");


        // closing connection
        lastIdFetch.close();
        dbase.close();

    }


    private void addButtons() {



            ViewGroup linear = (ViewGroup) findViewById(R.id.button_layout);
            ScrollView linearScroll = (ScrollView) findViewById(R.id.scroll_layout);
            linear.removeAllViews();



            //LinearLayout linear = new LinearLayout(this);
	        //linear.setOrientation(LinearLayout.VERTICAL);
	        //linear.setPadding(0, 50, 0, 0);





            //String[][] themes = getThemes();


	        Integer count = themes.length;



            int i = 0;

	        final Button[] buttons = new Button[count];

	        for(final String[] key: themes) {
	  		    buttons[i] = new Button(this);
                Log.i("Theme/Active Theme", key[1] + ":" + activeTheme);
	  		    if (key[1].equals(activeTheme)) {

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
                        boolean clicked = true;
                        new switchTheme().execute(key[0]);

                    }
                });

	  		    linear.addView(buttons[i]);
	  		    ++i;

            }




        dialog.dismiss();

	}



    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

	private void InitializeSQLCipher() {

            SQLiteDatabase.loadLibs(this);
            File databaseFile = getDatabasePath(Globals.DATABASE_NAME);

            if(!databaseFile.exists()) {
            databaseFile.mkdirs();
            databaseFile.delete();
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, Globals.PASSWORD_SECRET, null);


            database.execSQL("create table accounts (id integer primary key autoincrement, url text, homeURL text, blogName text, username text, password text, blogID integer, selected integer);");
            database.execSQL("insert into accounts (url,homeURL,blogName,username,password,blogID,selected) values('http://blog.dailyinvention.com/','http://blog.dailyinvention.com/xmlrpc.php','Theme Changrr Site','bobafett','letmein59208xpq',1,1)");

            database.close();
            }
    }

}