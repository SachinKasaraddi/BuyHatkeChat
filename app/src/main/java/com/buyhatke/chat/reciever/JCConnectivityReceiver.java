package com.buyhatke.chat.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.buyhatke.chat.util.BHApplication;
import com.buyhatke.chat.util.BHUtility;
import com.buyhatke.chat.xmpp.BHService;
import com.buyhatke.chat.xmpp.connection.ConnectionManager;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class JCConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (!BHApplication.getInstance().isServiceStarted() && BHUtility.isRegistered(context)){
                context.startService(BHService.createIntent(context));
            }
            BHApplication.setIsOnline(true);
        } else {
            try {
                ConnectionManager.getInstance().disconnect();
                BHService.getInstance().stop();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            BHApplication.setIsOnline(false);
        }
    }
}

