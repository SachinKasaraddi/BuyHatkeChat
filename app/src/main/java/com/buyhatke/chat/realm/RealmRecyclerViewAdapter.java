package com.buyhatke.chat.realm;

import android.support.v7.widget.RecyclerView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter {

    private RealmBaseAdapter<T> realmBaseAdapter;

    public void setRealmAdapter(RealmBaseAdapter<T> realmBaseAdapter) {
        this.realmBaseAdapter = realmBaseAdapter;
    }

    public RealmBaseAdapter<T> getRealmAdapter() {
        return realmBaseAdapter;
    }

    public T getItem(int position) {
        return realmBaseAdapter.getItem(position);
    }
}

