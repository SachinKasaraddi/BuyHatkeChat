package com.buyhatke.chat.chat;

import com.buyhatke.chat.realm.RealmModelAdapter;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public interface ChatWindowView {

    void setRealmAdapterAndUpdate(RealmModelAdapter realmModelAdapter);
    void dataSetChanged();
    void setStatus(String status);
}
