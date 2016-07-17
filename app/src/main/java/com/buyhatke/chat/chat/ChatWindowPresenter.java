package com.buyhatke.chat.chat;

import org.jivesoftware.smackx.chatstates.ChatState;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public interface ChatWindowPresenter {
    void initiateChatManager(String username, boolean isIncoming,boolean isGroup);
    void sendMessage(String message);
    void sendStatusMessage(ChatState chatState);
}
