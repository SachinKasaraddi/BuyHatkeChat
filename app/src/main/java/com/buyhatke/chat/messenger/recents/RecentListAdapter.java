package com.buyhatke.chat.messenger.recents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buyhatke.chat.R;
import com.buyhatke.chat.model.RecentChatObject;
import com.buyhatke.chat.realm.RealmRecyclerViewAdapter;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class RecentListAdapter extends RealmRecyclerViewAdapter<RecentChatObject> {

    public RecentListAdapter() {
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_chat_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecentChatObject recentChatObject = getItem(position);
        ((ChatViewHolder) holder).contactName.setText(recentChatObject.getChatDisplayName());
        ((ChatViewHolder) holder).chatLastMsg.setText(recentChatObject.getLastMsg());
        if (recentChatObject.isGroupChat()){
            ((ChatViewHolder) holder).contactImage.setImageResource(R.drawable.ic_group);
        }
    }


    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView contactImage;
        TextView contactName;
        TextView chatLastMsg;

        public ChatViewHolder(View itemView) {
            super(itemView);
            contactImage = (ImageView) itemView.findViewById(R.id.iv_contact);
            contactName = (TextView) itemView.findViewById(R.id.txt_contact_name);
            chatLastMsg = (TextView) itemView.findViewById(R.id.txt_last_msg);
        }
    }
}

