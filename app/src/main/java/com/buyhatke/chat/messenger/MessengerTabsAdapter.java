package com.buyhatke.chat.messenger;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.buyhatke.chat.messenger.contacts.ContactsFragment;
import com.buyhatke.chat.messenger.profile.ProfileFragment;
import com.buyhatke.chat.messenger.recents.RecentFragment;
import com.buyhatke.chat.util.BHConstants;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class MessengerTabsAdapter extends FragmentPagerAdapter {

    private RecentFragment recentFragment;
    private ContactsFragment contactsFragment;
    private ProfileFragment profileFragment;

    public MessengerTabsAdapter(FragmentManager fm) {
        super(fm);
        recentFragment = new RecentFragment();
        contactsFragment = new ContactsFragment();
        profileFragment = new ProfileFragment();
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
            case 0:
                fragment = recentFragment;
                break;
            case 1:
                fragment = contactsFragment;
                break;
            case 2:
                fragment = profileFragment;
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = BHConstants.TAB_TITLE_CHATS;
                break;
            case 1:
                title = BHConstants.TAB_TITLE_CONTACTS;
                break;
            case 2:
                title = BHConstants.TAB_TITLE_PROFILE;
                break;
        }
        return title;
    }
}

