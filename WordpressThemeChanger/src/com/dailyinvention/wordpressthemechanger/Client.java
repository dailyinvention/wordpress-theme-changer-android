package com.dailyinvention.wordpressthemechanger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class Client extends Activity {
	String url = (String)"http://test-wp.apache.local/xmlrpc.php";
	String username = (String)"admin";
	String password = (String)"password";
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_client);
         
        //uri = URI.create("http://test-wp.apache.local/xmlrpc.php");
         
      findViewById(R.id.text_view);
      LinearLayout linear = new LinearLayout(this);
	  linear.setOrientation(LinearLayout.VERTICAL);       
	  int count = getDataMethod(url, username, password).length;
	  int i = 0;
	  
	  final Button[] buttons = new Button[count];
	  
	  for(final String[] key: getDataMethod(url, username, password)) {
	  		buttons[i] = new Button(this);
	  		String active_theme = (String) getActiveTheme(url, username, password);
	  		Log.i("Active Theme", key + ":" + active_theme);
	  		if (key[1].equals(active_theme)) {
	  			
	  			buttons[i].getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xDAA52000));
	  		}
	  		
	  		buttons[i].setText(key[1]);
	  		
	  		final Object single_button = (Object) buttons[i];	
	  		((View) single_button).setOnClickListener(new View.OnClickListener() {
	  		
	  	       @Override
	  	       public void onClick(View linear) {  
	  	    	 ((View) single_button).getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xDAA52000));  
	  	    	 switchTheme(key[0], url, username, password); 
	  	       }
	  	     });
	  		linear.addView(buttons[i]);
	  		++i;
      }
    	    setContentView(linear);
      }
      //TextViewtextView.setText(getDataMethod());
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

	private String[][] getDataMethod(String url, String username, String password) {
        String[][] keys = null;
        
        try {
        	XMLRPCClient client = new XMLRPCClient(new URL(url));
            HashMap<String,Object> result = (HashMap<String,Object>) client.call("themes.getThemes",username,password);
            //keys = new String[result.keySet().toArray().length];
            //Log.i("Count",Integer.toString(result.values().toArray().length));
            //keys = result.values().toArray(new String[result.values().toArray().length]);
            String[][] arr = new String[result.size()][2];
            Set entries = result.entrySet();
            Iterator entriesIterator = entries.iterator();
            
            int i = 0;
            while(entriesIterator.hasNext()){

                Map.Entry mapping = (Map.Entry) entriesIterator.next();

                arr[i][0] = mapping.getKey().toString();
                arr[i][1] = mapping.getValue().toString();

                i++;
            }
            keys = arr;
        } catch (XMLRPCException e) {
            Log.w("XMLRPC Test", "Error", e);
            keys = new String[1][1];
            keys[0][1] = "XMLRPC error" + e;
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
        //return text;
        return keys;
    }
}