package com.buyhatke.chat.xmpp.connection;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ConnectionSettings {
    private String host = "192.168.0.5";
    private String userName;
    private String password;
    private String resource = "BuyHatke";
    private String name;

    public ConnectionSettings(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
