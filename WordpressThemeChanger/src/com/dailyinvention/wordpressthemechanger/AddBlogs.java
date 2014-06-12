package com.dailyinvention.wordpressthemechanger;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import net.sqlcipher.database.SQLiteDatabase;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

//Adds blogs

public class AddBlogs extends Activity {
    SQLiteDatabase dbase;
    String updateBlogName = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
            //Sets layout
            setContentView(R.layout.addblogs);

        //Declare buttons

        ImageButton info = (ImageButton) findViewById(R.id.info);
        TextView addTitleLabel = (TextView) findViewById(R.id.add_title_label);
        Button cancel_add = (Button) findViewById(R.id.cancel);
        Button add = (Button) findViewById(R.id.add_blog_button);

        //Declare form fields
        final EditText blogName = (EditText)findViewById(R.id.blog_name);
        final Spinner httpSelect = (Spinner)findViewById(R.id.http_selector);
        final EditText blogUrl = (EditText)findViewById(R.id.blog_url);
        final EditText username = (EditText)findViewById(R.id.username);
        final EditText password = (EditText)findViewById(R.id.password);

        Bundle bundle = getIntent().getExtras();

        //If a blog is broken, change button names

        if (bundle != null) {
            cancel_add.setText("Delete");
            add.setText("Update");
            addTitleLabel.setText("Fix a Wordpress Blog");
            blogName.setKeyListener(null);
            updateBlogName = bundle.getString("blogName");
            int slash = bundle.getString("url").indexOf('/');
            String updateUrl = bundle.getString("url").substring(slash + 2);
            String updateUsername = bundle.getString("username");
            blogName.setText(updateBlogName);
            blogUrl.setText(updateUrl);
            username.setText(updateUsername);
        }

        // Infobutton click

        info.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View infoButton) {

                final TextView message = new TextView(AddBlogs.this);
                message.setTextColor(Color.WHITE);
                message.setPadding(10,10,10,10);


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
                // If blog is broken display this alert dialogue

                if (updateBlogName != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddBlogs.this);
                    builder.setTitle("Delete Blog")
                            .setMessage("Are you sure you want to delete this blog?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    removeBlog();
                                    finish();
                                    dialog.dismiss();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    final AlertDialog alert = builder.create();
                    alert.show();

                }
                else {
                    finish();
                    Intent intent= new Intent(AddBlogs.this, Client.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    startActivity(intent);
                }


            }
        });

        // If add button is pressed

        add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View add_view) {
                // Checks for duplicate name
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
                    // checks to see if blog connection works
                    try {
                        success = new checkBlog()
                                .execute(httpSelect.getSelectedItem().toString(), blogUrl.getText().toString(), username.getText().toString(), password.getText().toString())
                                .get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    // if true, add blog
                    if(success == "true") {

                        new addAccount().execute(httpSelect.getSelectedItem().toString() + blogUrl.getText().toString(), httpSelect.getSelectedItem().toString() + blogUrl.getText().toString() + "/xmlrpc.php", blogName.getText().toString(), username.getText().toString(), password.getText().toString(), "1");


                    }

                    // if false, throw dialogue

                    else if (success == "false"){
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddBlogs.this);
                        builder.setTitle("Cannot Connect to Blog")
                                .setMessage("Unable to connect to Theme Changrr plugin using the blog url of " + httpSelect.getSelectedItem() + blogUrl.getText() + ".  Make sure the Theme Changrr plugin is set up for this blog and the username and password are correct.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {




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

                }

                // If fixing blog

                if(checkSuccess == "true") {

                    // if blog name to fix is set
                    if (updateBlogName != null) {

                        try {
                            success = new checkBlog()
                                    .execute(httpSelect.getSelectedItem().toString(), blogUrl.getText().toString(), username.getText().toString(), password.getText().toString())
                                    .get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        //check to see if blog exists

                        if (success == "true") {
                            //  Update account

                            new addAccount().execute(httpSelect.getSelectedItem().toString() + blogUrl.getText().toString() + "/xmlrpc.php", username.getText().toString(), password.getText().toString());

                        }
                        else {

                            // Show can not connect dialogue

                            AlertDialog.Builder builder = new AlertDialog.Builder(AddBlogs.this);
                            builder.setTitle("Cannot Connect to Blog")
                                    .setMessage("Unable to connect to Theme Changrr plugin using the blog url of " + httpSelect.getSelectedItem() + blogUrl.getText() + ".  Make sure the Theme Changrr plugin is set up for this blog and the username and password are correct.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {




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

                    }
                    else {

                        // Blog has a duplicate name

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
            }

        });
        
	}

    public void onPause(){
        super.onPause();
        dbase.close();
    }


    public void onResume() {

        super.onResume();


    	
    	SQLiteDatabase.loadLibs(this);

        final EventDataSQLHelper eventsData = new EventDataSQLHelper(AddBlogs.this);
        dbase = eventsData.getWritableDatabase(Globals.PASSWORD_SECRET);

        
        


	}	

	// Method that adds account
	private class addAccount extends AsyncTask<String,Void,String> {
        ProgressDialog dialog;
        protected void onPreExecute() {


                dialog = new ProgressDialog(AddBlogs.this);
                if(updateBlogName != null) {
                    dialog.setTitle("Updating blog...");
                }
                else {
                    dialog.setTitle("Adding blog...");
                }
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(true);
                dialog.show();

        }
        protected String doInBackground(String... blogValue) {
        //Log.i("SQL Values", blogValue[0] + ":" + blogValue[1] + blogValue[2] + ":" + blogValue[3] + blogValue[4] + ":" + blogValue[5]);
        ContentValues values = new ContentValues();

        if(updateBlogName != null) {
            String url = blogValue[0].replace("/xmlrpc.php","");

            dbase.execSQL("UPDATE " + Globals.SETTINGS_TABLE + " SET url='" + url + "',homeURL='" + blogValue[0] + "',username='" + blogValue[1] + "',password='" + blogValue[2] + "' WHERE blogName='" + updateBlogName + "';");
        }
        else {
            values.put("url", blogValue[0]);
            values.put("homeURL", blogValue[1]);
            values.put("blogName", blogValue[2]);
            values.put("username", blogValue[3]);
            values.put("password", blogValue[4]);
            values.put("blogId", Integer.parseInt(blogValue[5]));
            values.put("selected", 1);

            dbase.execSQL("UPDATE " + Globals.SETTINGS_TABLE + " SET selected='0' WHERE selected='1'");
            dbase.insert(Globals.SETTINGS_TABLE, null, values);
        }
        //values.put("wpVersion", wpVersion);



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
        
    }

    // Checks for duplicate name

    private class checkDuplicateName extends AsyncTask<String,Void,String> {
        protected String doInBackground(String... blogName) {
            String success = "false";
            String blogNameReturn = null;

            String selectedQuery = "SELECT blogName FROM " + Globals.SETTINGS_TABLE + " WHERE blogName='" + blogName[0] + "'";



            Cursor selectedblogName = dbase.rawQuery(selectedQuery, null);
            if (selectedblogName.moveToFirst()) {
                blogNameReturn = selectedblogName.getString(0);
                Log.i("Blog Name", blogNameReturn);
            }

            if (blogNameReturn != null) {
                success = "true";
            }

            selectedblogName.close();

            String result = success;
            return result;



        }

        protected void onPostExecute(String result) {

        }
    }

    // Checks to see if blog exists

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
            }

            dbase.execSQL("UPDATE " + Globals.SETTINGS_TABLE + " SET selected='1' WHERE id='" + lastID + "'");
        }
        finally {
            lastIdFetch.close();
        }




    }
	
	



	
}
