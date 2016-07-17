package com.buyhatke.chat.messenger.contacts;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ContactItemModel {
    String contactImageUri;
    String contactName;
    String displayName;

    public ContactItemModel(String contactImageUri, String contactName, String displayName) {
        this.contactImageUri = contactImageUri;
        this.contactName = contactName;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getContactImageUri() {
        return contactImageUri;
    }

    public void setContactImageUri(String contactImageUri) {
        this.contactImageUri = contactImageUri;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
