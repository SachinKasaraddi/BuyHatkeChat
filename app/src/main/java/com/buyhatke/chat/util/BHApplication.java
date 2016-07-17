package com.buyhatke.chat.util;

import android.app.Application;
import android.os.Handler;

import com.buyhatke.chat.db.UserStore;
import com.buyhatke.chat.model.UserObject;
import com.buyhatke.chat.xmpp.connection.ConnectionSettings;
import com.buyhatke.chat.xmpp.connection.ConnectionThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class BHApplication extends Application {

    private static BHApplication instance;
    private ExecutorService backgroundExecutor;
    private Handler handler;
    private boolean serviceStarted;
    private static boolean isOnline;

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        ConnectionDetectorUtility connectionDetectorUtility = new ConnectionDetectorUtility(this);
        setIsOnline(connectionDetectorUtility.isConnectingToInternet());
    }

    public static boolean isOnline() {
        return isOnline;
    }

    public static void setIsOnline(boolean isOnline) {
        BHApplication.isOnline = isOnline;
    }

    public BHApplication() {
        instance = this;
        handler = new Handler();
        serviceStarted = false;
        backgroundExecutor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "Background executor service");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setDaemon(true);
            return thread;
        });
    }

    public void onServiceStarted(boolean isRegister) {
        BHUtility.getLogger("BHApplication").debug("In service started");
        if (serviceStarted) {
            BHUtility.getLogger("BHApplication").debug("Service already started");
            return;
        }
        serviceStarted = true;
        UserObject userObject = UserStore.getInstance(this).getUser();
        ConnectionSettings connectionSettings = new ConnectionSettings(userObject.getUsername(), userObject.getPassword());
        connectionSettings.setName(userObject.getName());
        ConnectionThread connectionThread = new ConnectionThread(connectionSettings);
        connectionThread.start(isRegister);
    }

    public static BHApplication getInstance() {
        if (instance == null)
            throw new IllegalStateException();
        return instance;
    }

    public boolean isServiceStarted() {
        return serviceStarted;
    }

    public void runInBackground(final Runnable runnable) {
        backgroundExecutor.submit(runnable::run);
    }

    public void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void setServiceStopped() {
        serviceStarted = false;
    }

}

