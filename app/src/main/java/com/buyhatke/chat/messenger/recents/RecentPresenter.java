package com.buyhatke.chat.messenger.recents;

import android.content.Context;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public interface RecentPresenter {
    void loadRecentChats();
    void goToChat(String username, Context context, boolean isGroupChat);
}
