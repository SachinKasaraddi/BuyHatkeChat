package com.buyhatke.chat.model;

import io.realm.RealmObject;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class UserObject extends RealmObject {

    private String username;
    private String password;
    private String name;

    public UserObject() {
    }

    public UserObject(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public UserObject(String username,String password){
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

