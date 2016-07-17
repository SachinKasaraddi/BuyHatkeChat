package com.buyhatke.chat.messenger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import com.buyhatke.chat.chat.ChatWindowActivity;
import com.buyhatke.chat.db.RecentChatStore;
import com.buyhatke.chat.db.RosterStore;
import com.buyhatke.chat.messenger.contacts.ContactItemModel;
import com.buyhatke.chat.messenger.contacts.ContactsPresenter;
import com.buyhatke.chat.messenger.contacts.ContactsView;
import com.buyhatke.chat.messenger.profile.ProfileView;
import com.buyhatke.chat.messenger.recents.RealmRecentChatAdapter;
import com.buyhatke.chat.messenger.recents.RecentPresenter;
import com.buyhatke.chat.messenger.recents.RecentView;
import com.buyhatke.chat.model.RecentChatObject;
import com.buyhatke.chat.model.RosterObject;
import com.buyhatke.chat.util.BHConstants;
import com.buyhatke.chat.util.BHUtility;
import com.buyhatke.chat.xmpp.connection.ConnectionManager;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

import io.realm.RealmResults;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class MessengerPresenterImpl implements ContactsPresenter, RecentPresenter {

    private static MessengerPresenterImpl instance;

    private static ContactsView mContactsView;
    private static RecentView mRecentView;
    private static ProfileView mProfileView;

    private Logger log;

    private Handler handler;

    private static Context mContext;

    private MessengerPresenterImpl() {
        log = BHUtility.getLogger(MessengerPresenterImpl.class.getSimpleName());
        handler = new Handler();
    }

    public static MessengerPresenterImpl getInstance(ContactsView contactsView, Context context) {
        if (instance == null)
            instance = new MessengerPresenterImpl();
        mContactsView = contactsView;
        mContext = context;
        return instance;
    }

    public static MessengerPresenterImpl getInstance(RecentView recentView, Context context){
        if (instance == null)
            instance = new MessengerPresenterImpl();
        mRecentView = recentView;
        mContext = context;
        return instance;
    }

    public static MessengerPresenterImpl getInstance(ProfileView profileView, Context context){
        if (instance == null)
            instance = new MessengerPresenterImpl();
        mProfileView = profileView;
        mContext = context;
        return instance;
    }

    @Override
    public void loadContacts() {
        log.debug("Loading roster");
        if (BHUtility.getBooleanFromPreferences(mContext,
                BHConstants.SHARED_PREFERENCE_KEY_ROSTER_ADDED)) {
            loadRosterOffline();
        } else {
            loadRosterOnline();
        }
    }

    private void loadRosterOffline() {
        RealmResults<RosterObject> rosterObjectRealmResults = RosterStore.getInstance(mContext).getRosters();
        ArrayList<ContactItemModel> contactList = new ArrayList<>();
        for (RosterObject rosterObject : rosterObjectRealmResults) {
            contactList.add(new ContactItemModel("none", rosterObject.getUsername(), rosterObject.getDisplayName()));
        }
        mContactsView.setContactList(contactList);
    }

    private void loadRosterOnline() {
        if (ConnectionManager.getInstance().isConnected() && ConnectionManager.getInstance().isAuthorized()) {
            log.debug("Connected and logged in while Roster");
            new AsyncTask<Void, Void, Collection<RosterEntry>>() {
                ArrayList<ContactItemModel> contactList = new ArrayList<>();

                @Override
                protected Collection<RosterEntry> doInBackground(Void... params) {
                    Roster roster = Roster.getInstanceFor(ConnectionManager.getInstance().getXmppConnection());
                    if (!roster.isLoaded()) {
                        log.debug("Roster is not loaded loading it again");
                        try {
                            roster.reloadAndWait();
                            return roster.getEntries();
                        } catch (SmackException.NotLoggedInException |
                                InterruptedException |
                                SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                    log.debug("Already loaded returning directly");
                    return roster.getEntries();
                }

                @Override
                protected void onPostExecute(Collection<RosterEntry> rosterEntries) {
                    super.onPostExecute(rosterEntries);

                    for (RosterEntry entry : rosterEntries) {
                        String displayName = entry.getUser().split("@")[0];
                        log.debug("Display NAME",displayName);
                        contactList.add(new ContactItemModel("none", entry.getUser(), displayName));
                        requestSubscription(entry.getUser());
                        RosterStore.getInstance(mContext).writeRoster(new RosterObject(entry.getUser(), displayName));
                    }
                    BHUtility.writeBooleanToPreferences(mContext, BHConstants.SHARED_PREFERENCE_KEY_ROSTER_ADDED, true);
                    log.debug("Got roster :" + contactList);
                    mContactsView.setContactList(contactList);
                }
            }.execute();

        }
    }

    public void requestSubscription(String user) {
        Presence packet = new Presence(Presence.Type.subscribe);
        packet.setTo(user);
        ConnectionManager.getInstance().sendStanza(packet);
    }

    @Override
    public void goToChat(String username, Context context) {
        Intent intent = ChatWindowActivity.createIntent(context);
        intent.putExtra(BHConstants.INTENT_KEY_USER_NAME, username);
        context.startActivity(intent);
    }

    @Override
    public void loadRecentChats() {
        RealmResults<RecentChatObject> recentChatObjects = RecentChatStore.getInstance(mContext).getRecentChats();
        recentChatObjects.addChangeListener(mRecentView::dataSetChanged);
        RealmRecentChatAdapter realmRecentChatAdapter = new RealmRecentChatAdapter(mContext, recentChatObjects, true);
        mRecentView.setRealmAdapterAndUpdate(realmRecentChatAdapter);
    }

    @Override
    public void goToChat(String username, Context context, boolean isGroupChat) {
        Intent intent = ChatWindowActivity.createIntent(context);
        intent.putExtra(BHConstants.INTENT_KEY_USER_NAME, username);
        intent.putExtra(BHConstants.INTENT_KEY_IS_GROUP, isGroupChat);
        context.startActivity(intent);
    }
}

