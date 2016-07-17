package com.buyhatke.chat.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class RecentChatObject extends RealmObject {

    @PrimaryKey
    private String jid;
    private String lastMsg;
    private boolean isGroupChat;
    private String chatDisplayName;
    private String chatImageUri;

    public RecentChatObject(String jid, String lastMsg, boolean isGroupChat, String chatDisplayName, String chatImageUri) {
        this.jid = jid;
        this.lastMsg = lastMsg;
        this.isGroupChat = isGroupChat;
        this.chatDisplayName = chatDisplayName;
        this.chatImageUri = chatImageUri;
    }

    public RecentChatObject() {
    }

    public String getChatDisplayName() {
        return chatDisplayName;
    }

    public void setChatDisplayName(String chatDisplayName) {
        this.chatDisplayName = chatDisplayName;
    }

    public String getChatImageUri() {
        return chatImageUri;
    }

    public void setChatImageUri(String chatImageUri) {
        this.chatImageUri = chatImageUri;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public void setIsGroupChat(boolean isGroupChat) {
        this.isGroupChat = isGroupChat;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
}

