package com.buyhatke.chat.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ProtoRealm {
    private static ProtoRealm instance;
    private Realm realm;
    private static final String REALM_PROTO_ONE = "bh_chat_db.realm";

    public static ProtoRealm getInstance(Context context) {
        if (instance == null) {
            instance = new ProtoRealm(context);
        }
        return instance;
    }

    public ProtoRealm(Context context) {
        realm = Realm.getInstance(new RealmConfiguration.Builder(context).
                name(REALM_PROTO_ONE).build());
    }

    public Realm getRealm() {
        return realm;
    }

    public void close(){
        if (realm != null) {
            realm.close();
        }
    }
}
