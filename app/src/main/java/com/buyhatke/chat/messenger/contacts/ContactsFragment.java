package com.buyhatke.chat.messenger.contacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buyhatke.chat.R;
import com.buyhatke.chat.messenger.MessengerPresenterImpl;
import com.buyhatke.chat.util.BHUtility;
import com.buyhatke.chat.util.DividerItemDecoration;
import com.buyhatke.chat.util.RecyclerItemCLickListener;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ContactsFragment extends Fragment implements ContactsView{

    private RecyclerView mContactsRecyclerView;
    private List<ContactItemModel> mContactList;
    private ContactListAdapter mContactListAdapter;
    private ContactsPresenter contactsPresenter;
    private View rootView;
    private Logger log;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contacts, container, false);
        mContactList = new ArrayList<>();
        contactsPresenter = MessengerPresenterImpl.getInstance(this, getActivity());
        log = BHUtility.getLogger(ContactsFragment.class.getSimpleName());
        initRecyclerView();
        mContactsRecyclerView.addOnItemTouchListener(new RecyclerItemCLickListener(getActivity(),
                mContactsRecyclerView, new RecyclerItemCLickListener.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                contactsPresenter.goToChat(mContactList.get(position).getContactName(),getActivity());
            }

            @Override
            public void OnItemLongClick(View view, int position) {

            }
        }));
        contactsPresenter.loadContacts();
        return rootView;
    }

    @Override
    public void setContactList(ArrayList<ContactItemModel> contactList) {
        log.debug("Contact list setting "+ contactList);
        this.mContactList.addAll(contactList);
        mContactListAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView(){
        mContactsRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_contacts);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContactsRecyclerView.setHasFixedSize(true);
        mContactListAdapter = new ContactListAdapter(mContactList);
        mContactsRecyclerView.setAdapter(mContactListAdapter);
        mContactsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mContactsRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
    }
}

