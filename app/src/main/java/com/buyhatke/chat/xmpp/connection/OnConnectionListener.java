package com.buyhatke.chat.xmpp.connection;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public interface OnConnectionListener {
    void onConnected();
    void onConnectionFailed();
    void onUserExist();

}
