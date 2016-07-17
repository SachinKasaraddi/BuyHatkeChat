package com.buyhatke.chat.xmpp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.buyhatke.chat.util.BHApplication;
import com.buyhatke.chat.util.BHConstants;
import com.buyhatke.chat.util.BHUtility;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class BHService extends Service {

    private static BHService instance;
    boolean isRegister;
    public static BHService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        isRegister= false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BHUtility.getLogger("BHService").debug("Starting service");
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                isRegister = bundle.getBoolean(BHConstants.INTENT_KEY_IS_REGISTER);
            }
        }
        BHApplication.getInstance().onServiceStarted(isRegister);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent createIntent(Context context){
        return new Intent(context,BHService.class);
    }

    public void stop(){
        BHUtility.getLogger("BHService").debug("Stopping BHService");
        BHApplication.getInstance().setServiceStopped();
        stopSelf();
    }
}

