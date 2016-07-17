package com.buyhatke.chat.db;

import android.content.Context;

import com.buyhatke.chat.model.MessageObject;
import com.buyhatke.chat.util.BHUtility;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class MessageStore {
    private Realm realm;

    public MessageStore(Context context) {
        realm = ProtoRealm.getInstance(context).getRealm();
    }

    public static MessageStore getInstance(Context context) {
        return new MessageStore(context);
    }

    public void writeMessage(final MessageObject messageObject) {
        BHUtility.getLogger("Realm").debug("Writing message to db");
        realm.executeTransaction(realm1 -> realm1.copyToRealm(messageObject));
    }

    public void refresh() {
        if (realm != null) {
            realm.refresh();
        }
    }

    public RealmResults<MessageObject> getAllMessages() {
        return realm.where(MessageObject.class).findAllAsync();

    }

    public RealmResults<MessageObject> getMessagesFrom(String from) {
        return realm.where(MessageObject.class).equalTo("from", from).or().equalTo("to", from).findAllAsync();
    }

    public boolean isMsgExist(String msgId) {
        RealmQuery<MessageObject> query = realm.where(MessageObject.class)
                .equalTo("msgId", msgId);
        return query.count() > 0;
    }

    public void close() {
        realm.close();
    }

    public void updateFileUDStatus(String msgId,int statusCode) {
        MessageObject messageObjectOld = realm.where(MessageObject.class).equalTo("msgId", msgId).findFirst();
        realm.beginTransaction();
        messageObjectOld.setFileUDStatus(statusCode);
        realm.commitTransaction();
    }
}
