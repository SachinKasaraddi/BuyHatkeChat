package com.buyhatke.chat.xmpp.connection;

import com.buyhatke.chat.registration.RegistrationPresenterImpl;
import com.buyhatke.chat.util.BHApplication;
import com.buyhatke.chat.util.BHConstants;
import com.buyhatke.chat.util.BHUtility;
import com.buyhatke.chat.xmpp.BHService;
import com.buyhatke.chat.xmpp.mtm.MemorizingTrustManager;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ConnectionThread implements
        ConnectionListener,
        StanzaListener, PingFailedListener {

    private AbstractXMPPConnection xmppConnection;

    private final ExecutorService executorService;

    private final String host;

    private final String login;

    private final String password;

    private final String resource;

    private final String name;

    private final AcceptAll ACCEPT_ALL = new AcceptAll();

    private Logger log;

    private String usernameToRegister;


    public ConnectionThread(ConnectionSettings connectionSettings) {
        log = BHUtility.getLogger(ConnectionThread.class.getSimpleName());
        executorService = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(
                    runnable,
                    "Connection thread");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setDaemon(true);
            return thread;
        });
        host = connectionSettings.getHost();
        password = connectionSettings.getPassword();
        name = connectionSettings.getName();
        usernameToRegister = connectionSettings.getUserName();
        login = connectionSettings.getUserName();
        resource = connectionSettings.getResource();
    }

    private void runOnConnectionThread(final Runnable runnable) {
        executorService.submit(() -> {
            try {
                runnable.run();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void start(boolean isRegister) {
        log.debug("Starting connection thread");
        runOnConnectionThread(() -> createConnection(isRegister));
    }


    private void createConnection(boolean isRegister) {
        log.debug("Creating connection");
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setSendPresence(false);
        builder.setHost(host);
        builder.setPort(5222);
        builder.setServiceName(host);
        builder.setKeystoreType(BHConstants.CA_STORE);
        builder.setResource(resource);
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible);
        builder.setCompressionEnabled(false);
        builder.setDebuggerEnabled(true);
        SASLAuthentication.blacklistSASLMechanism(BHConstants.SHA_1_MECHANISM);
        SASLAuthentication.blacklistSASLMechanism(BHConstants.DIGEST_MD5_MECHANISM);
        SASLAuthentication.unBlacklistSASLMechanism(BHConstants.PLAIN_MECHANISM);

        try {
            log.debug("IN TRY BLOCK");
            SSLContext sslContext = SSLContext.getInstance(BHConstants.TLS);
            MemorizingTrustManager mtm = new MemorizingTrustManager(BHApplication.getInstance());
            sslContext.init(null, new X509TrustManager[]{mtm}, new java.security.SecureRandom());
            builder.setCustomSSLContext(sslContext);
            builder.setHostnameVerifier(
                    mtm.wrapHostnameVerifier(new org.apache.http.conn.ssl.StrictHostnameVerifier()));
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            log.debug("MTM exception");
        }

        xmppConnection = new XMPPTCPConnection(builder.build());
        xmppConnection.addAsyncStanzaListener(this, ACCEPT_ALL);
        xmppConnection.addConnectionListener(this);

        final Roster roster = Roster.getInstanceFor(xmppConnection);
        roster.setRosterLoadedAtLogin(true);
        roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        org.jivesoftware.smackx.ping.PingManager.getInstanceFor(xmppConnection).registerPingFailedListener(this);
        runOnConnectionThread(() -> connect(isRegister));
    }

    private void connect(final boolean isRegister) {
        try {
            xmppConnection.connect();
            log.debug("Connecting to XMPP");
            runOnUiThread(() -> onConnected(isRegister));
        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
            log.debug("Connection Exception");
            BHService.getInstance().stop();
            OnConnectionListener onConnectionListener = RegistrationPresenterImpl.getInstance();
            onConnectionListener.onConnectionFailed();
        }
    }

    private void onConnected(final boolean isRegister) {
        log.debug("Connected isRegister :"+isRegister);
        if (isRegister){
            runOnConnectionThread(this::register);
        }else {
            runOnConnectionThread(this::authorization);
        }
    }

    public AbstractXMPPConnection getXmppConnection() {
        return xmppConnection;
    }

    private void runOnUiThread(final Runnable runnable) {
        BHApplication.getInstance().runOnUiThread(runnable::run);
    }

    @Override
    public void connected(XMPPConnection connection) {
        log.debug("Connected callback");
        ConnectionManager.getInstance().connected();
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        log.debug("Authenticated Callback");
        ConnectionManager.getInstance().authorized();
        ConnectionManager.getInstance().setConnectionThread(this);
        OnConnectionListener onConnectionListener = RegistrationPresenterImpl.getInstance();
        onConnectionListener.onConnected();
    }

    @Override
    public void connectionClosed() {
        log.debug("Connection closed callback");
        ConnectionManager.getInstance().disconnected();
        BHService.getInstance().stop();
        OnConnectionListener onConnectionListener = RegistrationPresenterImpl.getInstance();
        onConnectionListener.onConnectionFailed();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        log.debug("Connection closed on error callback "+e.getMessage());
        ConnectionManager.getInstance().disconnected();
        BHService.getInstance().stop();
        OnConnectionListener onConnectionListener = RegistrationPresenterImpl.getInstance();
        onConnectionListener.onConnectionFailed();
    }

    @Override
    public void reconnectionSuccessful() {
        log.debug("Reconnection Successful");
        ConnectionManager.getInstance().connected();
    }

    @Override
    public void reconnectingIn(int seconds) {
        log.debug("Reconnecting in " + seconds + "s");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        log.debug("Reconnection failed with error callback "+e.getMessage());
        ConnectionManager.getInstance().disconnected();
    }

    @Override
    public void pingFailed() {
        log.debug("Ping failed callback");
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        log.debug("Packet arrived : " + packet.toString());
        ConnectionManager.getInstance().processPacket(packet);
    }

    static class AcceptAll implements StanzaFilter {
        @Override
        public boolean accept(Stanza packet) {
            return true;
        }
    }

    private void register(){
        HashMap<String,String> attributes = new HashMap<>();
        attributes.put("name",name);
        attributes.put("email",name);
        OnConnectionListener onConnectionListener = RegistrationPresenterImpl.getInstance();
        log.debug("Registering on Server");
        try {
            AccountManager.getInstance(xmppConnection).createAccount(usernameToRegister,password,attributes);
            authorization();
        } catch (SmackException.NoResponseException e) {
            log.debug("Registering exception NoResponse");
            e.printStackTrace();
            BHService.getInstance().stop();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            if (e.getXMPPError().getCondition().equals(XMPPError.Condition.resource_constraint) ||
                    e.getXMPPError().getCondition().equals(XMPPError.Condition.conflict)){
                handleExistingUser(onConnectionListener);
            }else {
                log.debug("Registering exception XMPPErrorException");
                onConnectionListener.onConnectionFailed();
                BHService.getInstance().stop();
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            log.debug("Registering exception NotConnected");
            BHService.getInstance().stop();
        }
    }

    private void handleExistingUser(OnConnectionListener onConnectionListener){
        log.debug("User already exist XMPPErrorException");
        BHService.getInstance().stop();
        onConnectionListener.onUserExist();
    }

    private void authorization() {
        try {
            log.debug("Login to XMPP");
            xmppConnection.login(login, password);
            Presence presence = new Presence(Presence.Type.available);
            xmppConnection.sendStanza(presence);
        } catch (IOException | SmackException | XMPPException e) {
            e.printStackTrace();
            connectionClosedOnError(e);
            xmppConnection.disconnect();
            log.debug("Login failed");
            BHService.getInstance().stop();
            OnConnectionListener onConnectionListener = RegistrationPresenterImpl.getInstance();
            onConnectionListener.onConnectionFailed();
            return;
        }
    }

    public void disconnect(){
        xmppConnection.disconnect();
    }
}
