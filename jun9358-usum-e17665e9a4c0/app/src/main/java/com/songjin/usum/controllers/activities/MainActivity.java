package com.songjin.usum.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.songjin.usum.R;
import com.songjin.usum.controllers.fragments.BuyFragment;
import com.songjin.usum.controllers.fragments.CommunityFragment;
import com.songjin.usum.controllers.fragments.MyPageFragment;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.controllers.fragments.SupportFragment;
import com.songjin.usum.entities.AlarmEntity;
import com.songjin.usum.slidingtab.SlidingBaseFragment;
import com.songjin.usum.slidingtab.SlidingTabsBasicFragment;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {
    public static Context context;

    private MenuItem alarmMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_main);

        context = this;
        if (savedInstanceState == null) {
            ArrayList<SlidingBaseFragment> tabFragments = new ArrayList<>();
            tabFragments.add(new CommunityFragment());
            tabFragments.add(new BuyFragment());
            tabFragments.add(new MyPageFragment());
            tabFragments.add(new SupportFragment());
            tabFragments.add(new SettingFragment());

            ArrayList<String> tabTitles = new ArrayList<>();
            tabTitles.add("커뮤니티");
            tabTitles.add("마켓");
            tabTitles.add("마이페이지");
            tabTitles.add("서포트");
            tabTitles.add("설정");

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            fragment.setTabFragments(tabFragments);
            fragment.setTabTitles(tabTitles);
            transaction.replace(R.id.content_fragment, fragment);
            transaction.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        BillingProcessor billingProcessor = SupportFragment.billingProcessor;
        if (billingProcessor != null) {
            if (billingProcessor.handleActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        alarmMenuItem = menu.findItem(R.id.action_alram);
        updateAlarmStatus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_alram) {
            BaseActivity.startActivityUsingStack(AlarmActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateAlarmStatus();
    }

    private void updateAlarmStatus() {
        if (alarmMenuItem == null) {
            return;
        }

        alarmMenuItem.setIcon(hasNotSyncedAlarm() ? R.drawable.ic_alram : R.drawable.ic_no_alarm);
    }

    private boolean hasNotSyncedAlarm() {
        ArrayList<BaasioPaylolad> receivedPushMessages = SettingFragment.getReceivedPushMessages();
        if (receivedPushMessages.isEmpty()) {
            return false;
        }

        long lastSyncedTimestamp = SettingFragment.getLastAlarmSyncedTimestamp();
        AlarmEntity latestAlarmEntity = new AlarmEntity(receivedPushMessages.get(0));
        return (lastSyncedTimestamp < latestAlarmEntity.timestamp);
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);
    }
}
