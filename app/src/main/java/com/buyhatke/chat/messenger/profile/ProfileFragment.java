package com.buyhatke.chat.messenger.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buyhatke.chat.R;
import com.buyhatke.chat.util.BHUtility;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class ProfileFragment extends Fragment implements ProfileView {


    @Bind(R.id.rv_joined_groups)
    RecyclerView mJoinedGroupsRecyclerView;

    @Bind(R.id.txt_user_name)
    TextView txtUserName;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);
        txtUserName.setText(BHUtility.getUsername());
        return rootView;
    }
}

