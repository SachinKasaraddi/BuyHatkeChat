package com.buyhatke.chat.db;

import android.content.Context;

import com.buyhatke.chat.model.RosterObject;
import com.buyhatke.chat.util.BHUtility;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class RosterStore  { private Realm realm;

    public RosterStore(Context context) {
        realm = ProtoRealm.getInstance(context).getRealm();
    }

    public Realm getRealm() {
        return realm;
    }

    public static RosterStore getInstance(Context context) {
        return new RosterStore(context);
    }

    public void writeRoster(final RosterObject rosterObject) {
        BHUtility.getLogger("Realm").debug("Writing roster to db");
        realm.executeTransaction(realm1 -> realm1.copyToRealm(rosterObject));
    }

    public void refresh() {
        if (realm != null) {
            realm.refresh();
        }
    }

    public RealmResults<RosterObject> getRosters() {
        return realm.where(RosterObject.class).findAll();
    }

    public void close(){
        realm.close();
    }
}

