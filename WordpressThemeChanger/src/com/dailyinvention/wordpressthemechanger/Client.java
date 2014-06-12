package com.dailyinvention.wordpressthemechanger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class Client extends Activity {
	String url;
	String username;
	String password;
    String[][] themes = null;
    String activeTheme = null;
    String blogName = null;
    ProgressDialog dialog;
    SQLiteDatabase dbase;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        // Create database if not created

        InitializeSQLCipher();

        // Define buttons and selectors
        ImageButton info = (ImageButton) findViewById(R.id.info);
        Button add_blog = (Button) findViewById(R.id.add_blog);
        Button remove_blog = (Button) findViewById(R.id.remove_blog);
        Spinner theme_selector = (Spinner) findViewById(R.id.blog_selector);

        info.setBackgroundColor(Color.TRANSPARENT);

        // Info button click dialogue

        info.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View infoButton) {

                final TextView message = new TextView(Client.this);
                message.setTextColor(Color.WHITE);
                message.setPadding(10,10,10,10);


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

        // Set listener on add blog button
        add_blog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View add_button) {
                // Calls the AddBlogs view
                startActivity(new Intent(Client.this, AddBlogs.class));

            }
        });

        // Set listener on remove button

        remove_blog.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View add_button) {
                String[] selectedBlog = getSelectedBlog();
                int selectedId = Integer.parseInt(selectedBlog[0]);

                // Prevents removing default blog

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

                // Removes entered blog

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



    }

    protected void onStop(){
        super.onStop();
        if (this.isFinishing()){
            finish();
        }
    }

    public void onPause() {
        super.onPause();
        dbase.close();
    }

    public void onResume() {

        super.onResume();

        SQLiteDatabase.loadLibs(this);

        final EventDataSQLHelper eventsData = new EventDataSQLHelper(Client.this);
        dbase = eventsData.getWritableDatabase(Globals.PASSWORD_SECRET);


        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean isScreenOn = powerManager.isScreenOn();

        // If screen is on do an online check.  If online then get blogs.

        if (isScreenOn) {

        if(CheckConnectivity.isOnline(Client.this)) {


         getBlogs();








        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Data Connection")
                    .setMessage("This application requires at least a 3g or wifi data connection to use.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();

                        }
                    });

            final AlertDialog alert = builder.create();
            alert.show();

        }

        }
	}

    // Gets the active theme

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

        //if (theme_name != null) {
            activeTheme = theme_name;
        //}
        /* else {
            activeTheme = null;
        }
        */
		return theme_name;
        }

        protected void onPostExecute(String result) {
            addButtons();
        }

    }

    // Puts a check box next to the active button

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

    // Switches the theme

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

    // Gets the list of themes

    public class getThemes extends AsyncTask<String,Void,String> {
        boolean check = true;


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



            //if ((!returnedThemes.isEmpty())) {
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

            /*
            }

            else {

                AlertDialog.Builder builder = new AlertDialog.Builder(Client.this);
                builder.setTitle("Broken Link")
                        .setMessage("The blog you want to connect to can not be found.  Maybe the blog was removed, the plugin was disabled, or the username or password were changed.  What would you like to do?")
                        .setCancelable(false)
                        .setPositiveButton("Remove Blog", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();

                            }
                        })
                        .setNegativeButton("Fix Blog", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();

                            }
                        });

                final AlertDialog alert = builder.create();
                alert.show();
                check = false;
            }

            */

        } catch (XMLRPCException e) {
            Log.i("Error", "XMLRPC error" + e);
            check = false;

        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

            String result = "Task Completed";
            return result;
        }

        protected void onPostExecute(String result) {
             // Checks for blog existance
             if (check == true) {
                new getActiveTheme().execute();
             }
            else {
                 dialog.dismiss();
                 AlertDialog.Builder builder = new AlertDialog.Builder(Client.this);
                 builder.setTitle("Broken Link")
                         .setMessage("The blog you want to connect to can not be found.  Maybe the blog was removed, the plugin was disabled, or the username or password were changed.  What would you like to do?")
                         .setCancelable(false)
                         .setPositiveButton("Remove Blog", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {
                                 removeBlog();
                                 getBlogs();
                                 dialog.dismiss();

                             }
                         })
                         .setNegativeButton("Fix Blog", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.dismiss();
                                 Intent intent = new Intent(Client.this, AddBlogs.class);
                                 intent.putExtra("url", url.replace("/xmlrpc.php",""));
                                 intent.putExtra("username", username);
                                 intent.putExtra("blogName", blogName);
                                 startActivity(intent);




                             }
                         });

                 final AlertDialog alert = builder.create();
                 alert.show();

             }
        }




    }

    // Gets selected blog information

    private String[] getSelectedBlog(){
        String selectedQuery = "SELECT  * FROM " + Globals.SETTINGS_TABLE + " WHERE selected='1'";

        int id = 0;
        String url = null;
        String homeURL = null;
        String blogName = null;
        String username = null;
        String password = null;




        Cursor selectedRow = dbase.rawQuery(selectedQuery, null);

        try {
        if (selectedRow.moveToFirst()) {
            id = selectedRow.getInt(0);
            url = selectedRow.getString(1);
            homeURL = selectedRow.getString(2);
            blogName = selectedRow.getString(3);
            username = selectedRow.getString(4);
            password = selectedRow.getString(5);

        }
        }
        finally {
            selectedRow.close();
        }



       String[] result = {String.valueOf(id),url,homeURL,blogName,username,password};

       return result;



    }

    // Gets the blog list and populates spinner

    private void getBlogs(){
        List<String> blogs = new ArrayList<String>();

        String selectQuery = "SELECT  * FROM " + Globals.SETTINGS_TABLE + " WHERE selected='0'";

        Cursor cursor = dbase.rawQuery(selectQuery, null);

        try {

            // Gets the selected blog information

        String[] first = getSelectedBlog();



        blogs.add(first[3]);
        username = first[4];
        password = first[5];
        url = first[2];
        blogName = first[3];



        if (cursor.moveToFirst()) {
            do {
                blogs.add(cursor.getString(3));
            } while (cursor.moveToNext());
        }
        }
        finally {
            cursor.close();
        }


        Spinner spinner = (Spinner) findViewById(R.id.blog_selector);

        // Build the spinner

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, blogs);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(null);
        spinner.setAdapter(dataAdapter);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getItemAtPosition(position).toString();
                    //new getActiveTheme().execute();
                    // if(activeTheme != null){
                        new getThemes().execute(selected);
                    /* }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Client.this);
                        builder.setTitle("Broken Link")
                                .setMessage("The blog you want to connect to can not be found.  Maybe the blog was removed, the plugin was disabled, or the username or password were changed.  What would you like to do?")
                                .setCancelable(false)
                                .setPositiveButton("Remove Blog", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();

                                    }
                                })
                                .setNegativeButton("Fix Blog", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();

                                    }
                                });

                        final AlertDialog alert = builder.create();
                        alert.show();

                    } */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {


            }

        });




	}


    // Sets the selected blog in database

    private void setBlog(String blogName) {
        String clearSelectedQuery = "UPDATE " + Globals.SETTINGS_TABLE + " SET selected='0' WHERE selected='1'";
        String setQuery = "UPDATE " + Globals.SETTINGS_TABLE + " SET selected='1' WHERE blogName='" + blogName + "'";
        String getQuery = "SELECT * FROM " + Globals.SETTINGS_TABLE + " WHERE blogName='" + blogName + "'";


        Log.i("Blogname",blogName);

        dbase.execSQL(clearSelectedQuery);
        dbase.execSQL(setQuery);

        Cursor getURLFetch = dbase.rawQuery(getQuery, null);

        try {
        if (getURLFetch.moveToFirst()) {
            url = getURLFetch.getString(2);
            username = getURLFetch.getString(4);
            password = getURLFetch.getString(5);

        }
        }
        finally {
            getURLFetch.close();
        }



    }

    // Removes the blog

    private void removeBlog(){

        String removeQuery = "DELETE FROM " + Globals.SETTINGS_TABLE + " WHERE selected='1'";
        String lastIDQuery = "SELECT * FROM " + Globals.SETTINGS_TABLE + " ORDER by id DESC LIMIT 1";



        dbase.execSQL(removeQuery);
        Cursor lastIdFetch = dbase.rawQuery(lastIDQuery, null);

        try {
        Integer lastID = null;

        if (lastIdFetch.moveToFirst()) {
            lastID = lastIdFetch.getInt(0);
            url = lastIdFetch.getString(2);
            username = lastIdFetch.getString(4);
            password = lastIdFetch.getString(5);
        }

        dbase.execSQL("UPDATE " + Globals.SETTINGS_TABLE + " SET selected='1' WHERE id='" + lastID + "'");
        }
        finally {
            lastIdFetch.close();
        }




    }


    // Adds buttons to screen

    private void addButtons() {



            ViewGroup linear = (ViewGroup) findViewById(R.id.button_layout);
            linear.removeAllViews();

	        Integer count = themes.length;

            int i = 0;

	        final Button[] buttons = new Button[count];

	        for(final String[] key: themes) {
	  		    buttons[i] = new Button(this);
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

    // Sets up database and uses SQLCipher

	private void InitializeSQLCipher() {

            SQLiteDatabase.loadLibs(this);
            File databaseFile = getDatabasePath(Globals.DATABASE_NAME);

            if(!databaseFile.exists()) {
            databaseFile.mkdirs();
            databaseFile.delete();
            SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, Globals.PASSWORD_SECRET, null);


            database.execSQL("create table accounts (id integer primary key autoincrement, url text, homeURL text, blogName text, username text, password text, blogID integer, selected integer);");
            database.execSQL("insert into accounts (url,homeURL,blogName,username,password,blogID,selected) values('https://themechangrr.dailyinvention.com/','https://themechangrr.dailyinvention.com/xmlrpc.php','Theme Changrr Site','bobafett','letmein59208xpq',1,1)");

            database.close();
            }
    }

}