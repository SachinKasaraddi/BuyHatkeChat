package com.buyhatke.chat.db;

import android.content.Context;

import com.buyhatke.chat.model.RecentChatObject;
import com.buyhatke.chat.util.BHUtility;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class RecentChatStore {
    private Realm realm;

    public RecentChatStore(Context context) {
        realm = ProtoRealm.getInstance(context).getRealm();
    }

    public Realm getRealm() {
        return realm;
    }

    public static RecentChatStore getInstance(Context context) {
        return new RecentChatStore(context);
    }

    public void writeRecentChat(final RecentChatObject recentChatObject) {
        BHUtility.getLogger("Realm").debug("Writing recent chat to db");
        realm.executeTransaction(realm1 -> realm1.copyToRealm(recentChatObject));
    }

    public void refresh() {
        if (realm != null) {
            realm.refresh();
        }
    }

    public RealmResults<RecentChatObject> getRecentChats() {
        return realm.where(RecentChatObject.class).findAll();
    }

    public boolean isExist(RecentChatObject recentChatObject) {
        RealmQuery<RecentChatObject> query = realm.where(RecentChatObject.class)
                .equalTo("jid", recentChatObject.getJid());
        query.count();
        return query.count() > 0;
    }

    public void updateLastMessage(RecentChatObject recentChatObject){
        RecentChatObject recentChatObjectOld = realm.where(RecentChatObject.class).
                equalTo("jid", recentChatObject.getJid()).findFirst();
        realm.beginTransaction();
        recentChatObjectOld.setLastMsg(recentChatObject.getLastMsg());
        realm.commitTransaction();
    }

    public void close(){
        realm.close();
    }
}
