package com.buyhatke.chat.xmpp.connection;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.buyhatke.chat.chat.ChatWindowActivity;
import com.buyhatke.chat.chat.ChatWindowPresenterImpl;
import com.buyhatke.chat.chat.OnChatStatusChangeListener;
import com.buyhatke.chat.db.MessageStore;
import com.buyhatke.chat.model.MessageObject;
import com.buyhatke.chat.util.BHApplication;
import com.buyhatke.chat.util.BHConstants;
import com.buyhatke.chat.util.BHUtility;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jxmpp.util.XmppStringUtils;

import java.util.Date;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ConnectionManager {

    private static ConnectionManager instance;

    private boolean isConnected;
    private boolean isAuthorized;

    private boolean isChatActive = false;

    private ConnectionThread connectionThread;

    private ConnectionManager() {
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public void setChatActive(boolean chatActive) {
        isChatActive = chatActive;
    }

    public boolean isChatActive() {
        return isChatActive;
    }

    public void connected() {
        isConnected = true;
    }

    public void disconnected() {
        isConnected = false;
    }

    public void authorized() {
        isAuthorized = true;
    }

    public void setConnectionThread(ConnectionThread connectionThread) {
        this.connectionThread = connectionThread;
    }

    public AbstractXMPPConnection getXmppConnection() {
        if (connectionThread != null) {
            return this.connectionThread.getXmppConnection();
        } else {
            return null;
        }
    }

    public void sendStanza(Stanza stanza) {
        if (getXmppConnection() != null) {
            try {
                getXmppConnection().sendStanza(stanza);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    public void processPacket(Stanza stanza) {
        if (stanza instanceof Message) {
            boolean isInvitation = false;
            boolean isGroupMsg = false;
            boolean isFile = false;
            String fileType = "";
            String sender = "";
            String fileUrl = "";
            Message message = (Message) stanza;

            /********** Returning if msg from me only for group chat ********/
            String nickname = XmppStringUtils.parseResource(message.getFrom());
            if (nickname.equalsIgnoreCase(BHUtility.getUsername()))
                return;
            /****************************************************************/

            if (message.getBody().startsWith("jchatfile")) {
                isFile = true;
                fileType = BHUtility.getMimeType(message.getBody());
                fileUrl = message.getBody().replace("jchatfile", "");
            }
            String msg = message.getBody();
            String from = XmppStringUtils.parseBareJid(message.getFrom());

            if (msg == null) {
                for (ExtensionElement element : message.getExtensions()) {
                    if (element instanceof ChatStateExtension) {
                        setStatus(element);
                    }
                }
                return;
            }

            if (Message.Type.groupchat.equals(message.getType())) {
                isGroupMsg = true;
                sender = XmppStringUtils.parseResource(message.getFrom());
            }

            for (ExtensionElement extensionElement : message.getExtensions()) {
                if (extensionElement instanceof GroupChatInvitation) {
                    /*GroupCreationPresenterImpl.getInstance().
                            joinGroup((GroupChatInvitation) extensionElement);*/
                    isInvitation = true;
                }
            }
            if (!isInvitation) {
                writeMessage(msg, from, isGroupMsg, message.getStanzaId(), sender, isFile, fileType, fileUrl, 0);
                createIntentAndNotify(from, msg, isGroupMsg);
            }
        }
    }

    private void setStatus(ExtensionElement element) {
        ChatStateExtension chatStateExtension = (ChatStateExtension) element;
        BHApplication.getInstance().runOnUiThread(() -> {
            OnChatStatusChangeListener onChatStatusChangeListener = ChatWindowPresenterImpl.getInstance();
            onChatStatusChangeListener.onStatusChanged(chatStateExtension.getChatState());
        });
    }

    private void createIntentAndNotify(String from, String msg, boolean isGroupMsg) {
        Intent goToDash = ChatWindowActivity.createIntent(BHApplication.getInstance());
        goToDash.putExtra(BHConstants.INTENT_KEY_USER_NAME, from.split("/")[0]);
        goToDash.putExtra(BHConstants.INTENT_KEY_MSG, msg);
        goToDash.putExtra(BHConstants.INTENT_KEY_IS_GROUP, isGroupMsg);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(BHApplication.getInstance(), 0, goToDash,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        if (!isChatActive())
            BHUtility.createAndSendNotification(12345, from.split("@")[0], msg, BHApplication.getInstance(),
                    resultPendingIntent);
    }

    private void writeMessage(final String msg, final String from, final boolean isGroup,
                              final String msgId, final String sender, final boolean isFile, final String fileType, final String fileUrl, final int fileUDStatus) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            MessageObject messageObject = new MessageObject(msg, true, from, "me", msgId, isGroup,
                    sender, new Date(), isFile, fileType, fileUrl, "", 0);
            MessageStore.getInstance(BHApplication.getInstance()).writeMessage(messageObject);
            MessageStore.getInstance(BHApplication.getInstance()).refresh();

        });
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void disconnect() {
        if (connectionThread != null) {
            connectionThread.disconnect();
        }
    }
}
