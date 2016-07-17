package com.buyhatke.chat.registration;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.buyhatke.chat.messenger.MessengerActivity;
import com.buyhatke.chat.util.BHConstants;
import com.buyhatke.chat.util.BHUtility;
import com.buyhatke.chat.xmpp.BHService;
import com.buyhatke.chat.xmpp.connection.ConnectionManager;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class LaunchControlActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BHUtility.getBooleanFromPreferences(this, BHConstants.SHARED_PREFERENCE_KEY_REG)){
            if (ConnectionManager.getInstance().isConnected() && ConnectionManager.getInstance().isAuthorized()){
                startActivity(MessengerActivity.createIntent(this));
                finish();
            }else {
                startService(BHService.createIntent(this));
                startActivity(MessengerActivity.createIntent(this));
                finish();
            }
        }else {
            startActivity(LoginActivity.createIntent(this));
            finish();
        }
    }
}

