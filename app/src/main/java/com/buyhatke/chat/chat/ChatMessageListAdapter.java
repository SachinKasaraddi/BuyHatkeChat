package com.buyhatke.chat.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buyhatke.chat.R;
import com.buyhatke.chat.model.MessageObject;
import com.buyhatke.chat.realm.RealmRecyclerViewAdapter;
import com.buyhatke.chat.util.BHApplication;
import com.buyhatke.chat.util.BHUtility;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ChatMessageListAdapter extends RealmRecyclerViewAdapter<MessageObject> {

    //Incoming
    private static final int VIEW_TYPE_INCOMING_MESSAGE = 10;

    //outgoing
    private static final int VIEW_TYPE_OUTGOING_MESSAGE = 20;


    public ChatMessageListAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case VIEW_TYPE_INCOMING_MESSAGE:
                return new IncomingMessage(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_layout_incoming_message, parent, false));

            case VIEW_TYPE_OUTGOING_MESSAGE:
                return new OutgoingMessage(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_layout_outgoing_message, parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final int viewType = getItemViewType(position);
        switch (viewType) {

            case VIEW_TYPE_INCOMING_MESSAGE:
                IncomingMessage incomingMessage = (IncomingMessage) holder;
                incomingMessageViewHolder(incomingMessage, position);
                break;
            case VIEW_TYPE_OUTGOING_MESSAGE:
                OutgoingMessage outgoingMessage = (OutgoingMessage) holder;
                outgoingMessageViewHolder(outgoingMessage, position);
                break;
        }
    }

    public void incomingMessageViewHolder(IncomingMessage incomingMessage, int position) {

        incomingMessage.msgContent.setText(getItem(position).getContent());
        incomingMessage.txtTime.setText(BHUtility.getSmartTimeText(BHApplication.getInstance(),
                getItem(position).getTimestamp()));

    }

    public void outgoingMessageViewHolder(OutgoingMessage outgoingMessage, int position) {
        outgoingMessage.msgContent.setText(getItem(position).getContent());
        outgoingMessage.txtTime.setText(BHUtility.getSmartTimeText(BHApplication.getInstance(),
                getItem(position).getTimestamp()));
    }


    @Override
    public int getItemCount() {
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        MessageObject messageObject = getItem(position);
        if (messageObject.isIncoming()) {
                 return VIEW_TYPE_INCOMING_MESSAGE;
        }
        else {
                    return VIEW_TYPE_OUTGOING_MESSAGE;
        }
    }

    public static class IncomingMessage extends RecyclerView.ViewHolder {

        TextView msgContent;
        TextView txtTime;

        public IncomingMessage(View itemView) {
            super(itemView);
            msgContent = (TextView) itemView.findViewById(R.id.txt_message_content);
            txtTime = (TextView) itemView.findViewById(R.id.txt_msg_time);

        }
    }

    public static class OutgoingMessage extends RecyclerView.ViewHolder {

        TextView msgContent;
        TextView txtTime;

        public OutgoingMessage(View itemView) {
            super(itemView);
            msgContent = (TextView) itemView.findViewById(R.id.txt_message_content);
            txtTime = (TextView) itemView.findViewById(R.id.txt_msg_time);
        }
    }
}

