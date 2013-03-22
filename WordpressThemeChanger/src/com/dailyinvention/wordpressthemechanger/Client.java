package com.dailyinvention.wordpressthemechanger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;



public class Client extends Activity {
    
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_client);
         
        //uri = URI.create("http://test-wp.apache.local/xmlrpc.php");
         
      findViewById(R.id.text_view);
      LinearLayout linear = new LinearLayout(this);
	  linear.setOrientation(LinearLayout.VERTICAL);       
	  int count = getDataMethod().length;
	  int i = 0;
	  
	  Button[] buttons = new Button[count];
	  
	  for(final String key: getDataMethod()) {
	  		buttons[i] = new Button(this);
	  		buttons[i].setText(key);
	  		buttons[i].setOnClickListener(new View.OnClickListener() {

	  	       @Override
	  	       public void onClick(View linear) {
	  	    	 switchTheme(key); 
	  	       }
	  	     });
	  		linear.addView(buttons[i]);
	  		++i;
      }
    	    setContentView(linear);
      }
      //TextViewtextView.setText(getDataMethod());
    
    private void switchTheme(String themeName) {
    	try {
			XMLRPCClient client = new XMLRPCClient(new URL("http://test-wp.apache.local/xmlrpc.php"));
			String[] params = {"admin","password",themeName};
			String result = (String) client.call("themes.switchThemes",params);
    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLRPCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }

	private String[] getDataMethod() {
        String[] keys = null;
        
        try {
        	XMLRPCClient client = new XMLRPCClient(new URL("http://test-wp.apache.local/xmlrpc.php"));
            HashMap<String,Object> result = (HashMap<String,Object>) client.call("themes.getThemes","admin","password");
            //keys = new String[result.keySet().toArray().length];
            Log.i("Count",Integer.toString(result.keySet().toArray().length));
            keys = result.keySet().toArray(new String[result.keySet().toArray().length]);
            
        } catch (XMLRPCException e) {
            Log.w("XMLRPC Test", "Error", e);
            keys = new String[1];
            keys[0] = "XMLRPC error" + e;
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
        //return text;
        return keys;
    }
}