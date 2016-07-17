package com.buyhatke.chat.messenger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.buyhatke.chat.R;

/**
 * Created by sachin.kasaraddi on 16/07/16.
 */
public class MessengerActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private MessengerTabsAdapter mMessengerTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mViewPager = (ViewPager) findViewById(R.id.messengerTabsPager);
        mTabLayout = (TabLayout) findViewById(R.id.messengerTabLayout);
        mMessengerTabsAdapter = new MessengerTabsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMessengerTabsAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MessengerActivity.class);
    }
}
