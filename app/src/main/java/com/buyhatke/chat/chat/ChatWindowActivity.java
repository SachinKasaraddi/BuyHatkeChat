package com.buyhatke.chat.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buyhatke.chat.R;
import com.buyhatke.chat.db.RecentChatStore;
import com.buyhatke.chat.model.RecentChatObject;
import com.buyhatke.chat.realm.RealmModelAdapter;
import com.buyhatke.chat.util.BHApplication;
import com.buyhatke.chat.util.BHConstants;
import com.buyhatke.chat.util.BHUtility;
import com.buyhatke.chat.xmpp.connection.ConnectionManager;

import org.jivesoftware.smackx.chatstates.ChatState;
import org.jxmpp.util.XmppStringUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ChatWindowActivity extends AppCompatActivity implements ChatWindowView {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.rv_chat_messages)
    RecyclerView rvMessages;

    @Bind(R.id.edt_message)
    EditText edtMessage;

    @Bind(R.id.txt_chat_name)
    TextView txtChatName;

    @Bind(R.id.iv_btn_send)
    ImageView btnSend;

    @Bind(R.id.txt_user_status)
    TextView txtUserStatus;

    @Bind(R.id.iv_contact_image)
    ImageView ivContactImage;


    private LinearLayoutManager linearLayoutManager;
    private String username;
    private String firstMsg;
    private boolean isGroupChat = false;
    private boolean fromIncoming;
    private ChatWindowPresenter chatWindowPresenter;
    private ChatMessageListAdapter chatMessageListAdapter;
    private Timer stopTypingTimer = new Timer();
    private final long STOP_TYPING_DELAY = 2000; // in ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        ButterKnife.bind(this);
        ConnectionManager.getInstance().setChatActive(true);
        setUpToolBar();

        chatWindowPresenter = ChatWindowPresenterImpl.getInstance(this);

        initMessageList();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString(BHConstants.INTENT_KEY_USER_NAME);
            firstMsg = bundle.getString(BHConstants.INTENT_KEY_MSG);
            isGroupChat = bundle.getBoolean(BHConstants.INTENT_KEY_IS_GROUP);
            txtChatName.setText(username.split("@")[0]);
            if (!TextUtils.isEmpty(firstMsg)) {
                chatMessageListAdapter.notifyDataSetChanged();
                fromIncoming = true;
            }
            if (isGroupChat) {
                txtUserStatus.setText("Group Chat");
                ivContactImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_group));
            }
            chatWindowPresenter.initiateChatManager(username, fromIncoming, isGroupChat);
        }

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int before) {
                if (stopTypingTimer != null) {
                    stopTypingTimer.cancel();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int before) {


                chatWindowPresenter.sendStatusMessage(ChatState.composing);

                stopTypingTimer = new Timer();
                stopTypingTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        BHApplication.getInstance().runOnUiThread(() ->
                                chatWindowPresenter.sendStatusMessage(ChatState.paused));
                    }

                }, STOP_TYPING_DELAY);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initMessageList() {
        linearLayoutManager = new LinearLayoutManager(this);
        chatMessageListAdapter = new ChatMessageListAdapter();
        rvMessages.setLayoutManager(linearLayoutManager);
        rvMessages.setAdapter(chatMessageListAdapter);
    }

    private void setUpToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @OnClick(R.id.iv_btn_send)
    void sendMessage() {
        if (!BHApplication.isOnline()) {
            BHUtility.showToast(this, "You are offline, can't send message", Toast.LENGTH_SHORT);
            return;
        }
        if (edtMessage.getText().toString().length() > 0) {
            chatMessageListAdapter.notifyDataSetChanged();
            chatWindowPresenter.sendMessage(edtMessage.getText().toString());
            edtMessage.setText("");
            updateChat();
        }
    }

    private void scrollChat(int itemCountBeforeUpdate) {
        int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
        if (lastVisibleItemPosition == -1 || lastVisibleItemPosition <= (itemCountBeforeUpdate - 1)) {
            scrollDown();
        }
    }


    private void scrollDown() {
        rvMessages.scrollToPosition(chatMessageListAdapter.getItemCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatMessageListAdapter.getItemCount() > 0) {
            String lastMsg = chatMessageListAdapter.getItem(chatMessageListAdapter.
                    getItemCount() - 1).getContent();
            if (lastMsg == null) {
                lastMsg = "";
            }
            RecentChatObject recentChatObject = new RecentChatObject(username, lastMsg, isGroupChat,
                    XmppStringUtils.parseLocalpart(username), "none");
            RecentChatStore recentChatStore = RecentChatStore.getInstance(this);
            if (recentChatStore.isExist(recentChatObject)) {
                recentChatStore.updateLastMessage(recentChatObject);
            } else {
                recentChatStore.writeRecentChat(recentChatObject);
            }
        }
        ConnectionManager.getInstance().setChatActive(false);
    }

    public void updateChat() {
        int itemCountBeforeUpdate = chatMessageListAdapter.getItemCount() - 1;
        scrollChat(itemCountBeforeUpdate);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, ChatWindowActivity.class);
    }


    @Override
    public void setRealmAdapterAndUpdate(RealmModelAdapter realmModelAdapter) {
        chatMessageListAdapter.setRealmAdapter(realmModelAdapter);
        chatMessageListAdapter.notifyDataSetChanged();
        updateChat();
    }

    @Override
    public void dataSetChanged() {
        chatMessageListAdapter.notifyDataSetChanged();
        updateChat();
    }

    @Override
    public void setStatus(String status) {
        txtUserStatus.setText(status);
    }
}

