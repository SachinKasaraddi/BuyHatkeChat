package com.buyhatke.chat.messenger.contacts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buyhatke.chat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {

    private List<ContactItemModel> contactList = new ArrayList<>();

    public ContactListAdapter(List<ContactItemModel> contacts) {
        this.contactList = contacts;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout_contact_list, viewGroup, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        contactViewHolder.contactName.setText(contactList.get(i).getDisplayName());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView contactImage;
        TextView contactName;

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.txt_contact_name);
            contactImage = (ImageView) itemView.findViewById(R.id.iv_contact);
        }
    }
}

