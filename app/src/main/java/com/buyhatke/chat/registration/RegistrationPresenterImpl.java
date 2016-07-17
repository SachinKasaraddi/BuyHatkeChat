package com.buyhatke.chat.registration;

import android.os.Handler;

import com.buyhatke.chat.db.UserStore;
import com.buyhatke.chat.model.UserObject;
import com.buyhatke.chat.util.BHApplication;
import com.buyhatke.chat.util.BHUtility;
import com.buyhatke.chat.xmpp.connection.OnConnectionListener;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class RegistrationPresenterImpl implements LoginPresenter, OnConnectionListener, RegistrationPresenter {

    private static RegistrationPresenterImpl instance;
    private static LoginView mLoginView;
    private static RegistrationView mRegistrationView;

    private RegistrationPresenterImpl() {

    }

    public static RegistrationPresenterImpl getInstance(LoginView loginView) {
        if (instance == null)
            instance = new RegistrationPresenterImpl();
        mLoginView = loginView;
        return instance;
    }

    public static RegistrationPresenterImpl getInstance(RegistrationView registrationView) {
        if (instance == null)
            instance = new RegistrationPresenterImpl();
        mRegistrationView = registrationView;
        return instance;
    }

    public static RegistrationPresenterImpl getInstance() {
        if (instance == null)
            instance = new RegistrationPresenterImpl();
        return instance;
    }


    @Override
    public void startConnection(String username, String password) {
        BHUtility.storeUser(BHApplication.getInstance(),username);
        UserStore.getInstance(BHApplication.getInstance()).
                writeUser(new UserObject(username, password));
        Handler handler = new Handler();
        handler.postDelayed(mLoginView::startBHService, 1000);
    }

    @Override
    public void onConnected() {
        if (mLoginView != null) {
            mLoginView.goToDash();
        }
        if (mRegistrationView != null){
            mRegistrationView.goToDash();
        }
    }

    @Override
    public void onConnectionFailed() {
        if (mLoginView != null) {
            mLoginView.showError();
        }
        if (mRegistrationView != null) {
            mRegistrationView.showError();
        }
    }

    @Override
    public void onUserExist() {
        if (mRegistrationView != null) {
            mRegistrationView.showUserAlreadyExistError();
        }
    }

    @Override
    public void connectAndRegister(String username, String password, String name) {
        UserStore.getInstance(BHApplication.getInstance()).
                writeUser(new UserObject(username, password, name));
        Handler handler = new Handler();
        handler.postDelayed(() -> mRegistrationView.startBHService(true),1000);
    }
}

