package com.buyhatke.chat.messenger.recents;

import android.content.Context;

import com.buyhatke.chat.model.RecentChatObject;
import com.buyhatke.chat.realm.RealmModelAdapter;

import io.realm.RealmResults;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class RealmRecentChatAdapter extends RealmModelAdapter<RecentChatObject> {

    public RealmRecentChatAdapter(Context context, RealmResults<RecentChatObject> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}

