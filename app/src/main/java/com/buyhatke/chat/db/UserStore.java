package com.buyhatke.chat.db;

import android.content.Context;

import com.buyhatke.chat.model.UserObject;
import com.buyhatke.chat.util.BHUtility;

import io.realm.Realm;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class UserStore {
    private Realm realm;

    public UserStore(Context context) {
        realm = ProtoRealm.getInstance(context).getRealm();
    }

    public Realm getRealm() {
        return realm;
    }

    public static UserStore getInstance(Context context) {
        return new UserStore(context);
    }

    public void writeUser(final UserObject userObject) {
        BHUtility.getLogger("Realm").debug("Writing user to db");
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(userObject);
            }
        });
    }

    public void refresh() {
        if (realm != null) {
            realm.refresh();
        }
    }

    public UserObject getUser() {
        return realm.where(UserObject.class).findAllAsync().get(0);
    }

    public void close(){
        realm.close();
    }
}

