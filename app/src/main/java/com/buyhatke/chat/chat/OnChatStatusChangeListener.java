package com.buyhatke.chat.chat;

import org.jivesoftware.smackx.chatstates.ChatState;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public interface OnChatStatusChangeListener {
    void onStatusChanged(ChatState chatState);
}
