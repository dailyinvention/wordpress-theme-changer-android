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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Client extends Activity {
	String url = (String)"http://test-wp.apache.local/xmlrpc.php";
	String username = (String)"admin";
	String password = (String)"password";
     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
         
      findViewById(R.id.text_view);
      LinearLayout linear = new LinearLayout(this);
	  linear.setOrientation(LinearLayout.VERTICAL);       
	  int count = getThemes(url, username, password).length;
	  int i = 0;
	  
	  final Button[] buttons = new Button[count];
	  
	  for(final String[] key: getThemes(url, username, password)) {
	  		buttons[i] = new Button(this);
	  		String active_theme = (String) getActiveTheme(url, username, password);
	  		
	  		if (key[1].equals(active_theme)) {
	  			
	  			checkButton(buttons[i],"active");
	  		}
	  		
	  		buttons[i].setText(key[1]);	
	  		((View) buttons[i]).setOnClickListener(new View.OnClickListener() {
	  		
			@Override
	  	       public void onClick(View clicked_button) {
				Drawable image = getBaseContext().getResources().getDrawable( R.drawable.check );
	    		image.setBounds( 0, 0, 30, 30 );
				for(int n=0; n < (buttons.length); ++n) {
					checkButton(buttons[n],"inactive");
				}
				
				checkButton(clicked_button,"active");
				switchTheme(key[0], url, username, password);
				
	  	       }
	  	     });
	  		
	  		linear.addView(buttons[i]);
	  		++i;
      }
    	    setContentView(linear);
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

	private String[][] getThemes(String url, String username, String password) {
        String[][] keys = null;
        
        try {
        	XMLRPCClient client = new XMLRPCClient(new URL(url));
            HashMap<String,Object> result = (HashMap<String,Object>) client.call("themes.getThemes",username,password);
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
            keys = new String[1][1];
            keys[0][1] = "XMLRPC error" + e;
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}       
        return keys;
    }
}