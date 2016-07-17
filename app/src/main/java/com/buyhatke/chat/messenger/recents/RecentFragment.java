package com.buyhatke.chat.messenger.recents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buyhatke.chat.R;
import com.buyhatke.chat.messenger.MessengerPresenterImpl;
import com.buyhatke.chat.realm.RealmModelAdapter;
import com.buyhatke.chat.util.DividerItemDecoration;
import com.buyhatke.chat.util.RecyclerItemCLickListener;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class RecentFragment extends Fragment implements RecentView {

    private RecyclerView mChatsRecyclerView;
    private RecentListAdapter mRecentListAdapter;
    private RecentPresenter recentPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        initRecyclerView(rootView);
        recentPresenter = MessengerPresenterImpl.getInstance(this, getActivity());
        recentPresenter.loadRecentChats();
        return rootView;
    }

    private void initRecyclerView(View rootView) {
        mChatsRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_chats);
        mChatsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mChatsRecyclerView.setHasFixedSize(true);
        mRecentListAdapter = new RecentListAdapter();
        mChatsRecyclerView.setAdapter(mRecentListAdapter);
        mChatsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mChatsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        mChatsRecyclerView.addOnItemTouchListener(new RecyclerItemCLickListener(getActivity(),
                mChatsRecyclerView, new RecyclerItemCLickListener.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                recentPresenter.goToChat(mRecentListAdapter.getItem(position).getJid(), getActivity(),
                        mRecentListAdapter.getItem(position).isGroupChat());
            }

            @Override
            public void OnItemLongClick(View view, int position) {
                //TODO : Handle item long click
            }
        }));
    }

    @Override
    public void setRealmAdapterAndUpdate(RealmModelAdapter realmModelAdapter) {
        mRecentListAdapter.setRealmAdapter(realmModelAdapter);
        mRecentListAdapter.notifyDataSetChanged();
    }

    @Override
    public void dataSetChanged() {
        mRecentListAdapter.notifyDataSetChanged();
    }
}

