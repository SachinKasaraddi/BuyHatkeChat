package com.buyhatke.chat.model;

import io.realm.RealmObject;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class RosterObject extends RealmObject {

    private String username;
    private String displayName;

    public RosterObject(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }

    public RosterObject() {
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

