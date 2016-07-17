package com.buyhatke.chat.messenger.contacts;

import android.content.Context;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public interface ContactsPresenter {
    void loadContacts();
    void goToChat(String username, Context context);
}
