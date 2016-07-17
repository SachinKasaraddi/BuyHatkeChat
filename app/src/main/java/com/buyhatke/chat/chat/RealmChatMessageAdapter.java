package com.buyhatke.chat.chat;

import android.content.Context;

import com.buyhatke.chat.model.MessageObject;
import com.buyhatke.chat.realm.RealmModelAdapter;

import io.realm.RealmResults;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class RealmChatMessageAdapter extends RealmModelAdapter<MessageObject> {

    public RealmChatMessageAdapter(Context context, RealmResults<MessageObject> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}

