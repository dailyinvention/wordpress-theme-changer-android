package com.dailyinvention.wordpressthemechanger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by dailyinvention on 5/26/13.
 */
public class CheckConnectivity {
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if( cm == null )
            return false;
        NetworkInfo info = cm.getActiveNetworkInfo();
        if( info == null )
            return false;
        return info.isConnectedOrConnecting();
    }
}