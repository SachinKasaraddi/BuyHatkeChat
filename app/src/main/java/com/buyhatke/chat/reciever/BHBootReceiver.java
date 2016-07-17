package com.buyhatke.chat.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.buyhatke.chat.xmpp.BHService;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class BHBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(BHService.createIntent(context));
    }
}

