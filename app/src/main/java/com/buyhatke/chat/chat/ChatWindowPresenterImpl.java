package com.buyhatke.chat.chat;

import android.content.Context;

import com.buyhatke.chat.db.MessageStore;
import com.buyhatke.chat.model.MessageObject;
import com.buyhatke.chat.util.BHApplication;
import com.buyhatke.chat.util.BHUtility;
import com.buyhatke.chat.xmpp.connection.ConnectionManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.slf4j.Logger;

import java.util.Date;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ChatWindowPresenterImpl implements ChatWindowPresenter, ChatMessageListener, RealmChangeListener, ChatStateListener, OnChatStatusChangeListener {

    private static ChatWindowPresenterImpl instance;
    private static ChatWindowView mChatWindowView;
    private Logger log;
    private boolean fromIncoming;
    private ChatManager chatManager;
    private Chat chat;
    private String username;
    private static Context mContext;
    private boolean isGroup;

    public static ChatWindowPresenterImpl getInstance(ChatWindowView chatWindowView) {
        if (instance == null)
            instance = new ChatWindowPresenterImpl();
        mChatWindowView = chatWindowView;
        mContext = (Context) mChatWindowView;
        return instance;
    }

    public static ChatWindowPresenterImpl getInstance() {
        if (instance == null)
            instance = new ChatWindowPresenterImpl();
        return instance;
    }

    private ChatWindowPresenterImpl() {
        log = BHUtility.getLogger(ChatWindowPresenterImpl.class.getSimpleName());
    }


    @Override
    public void initiateChatManager(final String username, boolean isIncoming, boolean isGroup) {
        this.username = username;
        this.isGroup = isGroup;
        if (ConnectionManager.getInstance().isConnected() &&
                ConnectionManager.getInstance().isAuthorized()) {
                chatManager = ChatManager.getInstanceFor(
                        ConnectionManager.getInstance().getXmppConnection());
                chat = chatManager.createChat(username, this);
        }
        RealmResults<MessageObject> realmResults = MessageStore.
                getInstance(BHApplication.getInstance()).getMessagesFrom(username);
        realmResults.addChangeListener(this);
        RealmChatMessageAdapter realmChatMessageAdapter = new RealmChatMessageAdapter(mContext, realmResults, true);
        mChatWindowView.setRealmAdapterAndUpdate(realmChatMessageAdapter);
    }

    @Override
    public void sendMessage(String messageBody) {
           Message message = new Message();
                MessageStore.getInstance(BHApplication.getInstance()).writeMessage(
                        new MessageObject(messageBody, false, "me", username, message.getStanzaId(), false, "", new Date(), false, "", "", "", 0));
                message.setBody(messageBody);
            try {
                chat.sendMessage(message);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }


    @Override
    public void sendStatusMessage(ChatState chatState) {
        Message message = new Message();
        message.setTo(username);
        message.addExtension(new ChatStateExtension(chatState));
        ConnectionManager.getInstance().sendStanza(message);
    }

    @Override
    public void processMessage(Chat chat, Message message) {

        final ExtensionElement extension = message
                .getExtension("http://jabber.org/protocol/chatstates");
        if (extension == null) {
            return;
        }

        ChatState state;
        try {
            state = ChatState.valueOf(extension.getElementName());
        } catch (final Exception ex) {
            return;
        }
                stateChanged(chat,state);
    }



    @Override
    public void onChange() {
        mChatWindowView.dataSetChanged();
    }



    @Override
    public void stateChanged(Chat chat, ChatState state) {
        onStatusChanged(state);

    }


    @Override
    public void onStatusChanged(ChatState chatState) {
        if (mChatWindowView != null) {
            if (ChatState.composing.equals(chatState)) {
                mChatWindowView.setStatus("typing...");
            } else if (ChatState.gone.equals(chatState)) {
                mChatWindowView.setStatus("");
            } else if (ChatState.active.equals(chatState)) {
                mChatWindowView.setStatus("Online");
            } else if (ChatState.paused.equals(chatState)) {
                mChatWindowView.setStatus("Online");
            } else {
                mChatWindowView.setStatus("");
            }
        }
    }

}


