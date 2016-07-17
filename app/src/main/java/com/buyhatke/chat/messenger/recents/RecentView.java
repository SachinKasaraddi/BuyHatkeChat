package com.buyhatke.chat.messenger.recents;

import com.buyhatke.chat.realm.RealmModelAdapter;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public interface RecentView {
    void setRealmAdapterAndUpdate(RealmModelAdapter realmModelAdapter);
    void dataSetChanged();
}
