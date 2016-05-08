package com.songjin.usum.controllers.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.fragments.BuyFragment;
import com.songjin.usum.controllers.fragments.CommunityFragment;
import com.songjin.usum.controllers.fragments.MyPageFragment;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.controllers.fragments.SupportFragment;
import com.songjin.usum.entities.AlarmEntity;
import com.songjin.usum.gcm.gcm.GCMManager;
import com.songjin.usum.gcm.gcm.RegistrationIntentService;
import com.songjin.usum.slidingtab.SlidingBaseFragment;
import com.songjin.usum.slidingtab.SlidingTabsBasicFragment;

import java.util.ArrayList;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    public static Context context;

    private MenuItem alarmMenuItem;

    private CommunityFragment communityFragment;
    private BuyFragment buyFragment;
    private MyPageFragment myPageFragment;
    private SupportFragment supportFragment;
    private SettingFragment settingFragment;


    private Button mRegistrationButton;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public void getInstanceIdToken() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */
    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                if(action.equals(GCMManager.REGISTRATION_COMPLETE)){
                    // 액션이 COMPLETE일 경우
                    String token = intent.getStringExtra("token");
                    Log.d(TAG, "token = " + token);
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_main);
        Log.d(TAG, "액티비티 시작");
        registBroadcastReceiver();
        getInstanceIdToken();

        context = this;
        if (savedInstanceState == null) {
            if (getIntent().getParcelableExtra(Global.USER) == null) {
                Toast.makeText(getApplicationContext(), "로그인 중 에러가 발생했습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                finish();
            }

            Global.userEntity = getIntent().getParcelableExtra(Global.USER);
            Log.d(TAG, "id = " + Global.userEntity.id);
            Log.d(TAG, "schoolId = " + Global.userEntity.schoolId);
            ArrayList<SlidingBaseFragment> tabFragments = new ArrayList<>();

            communityFragment = new CommunityFragment();
            buyFragment = new BuyFragment();
            myPageFragment = new MyPageFragment();
            supportFragment = new SupportFragment();
            settingFragment = new SettingFragment();

            tabFragments.add(communityFragment);
            tabFragments.add(buyFragment);
            tabFragments.add(myPageFragment);
            tabFragments.add(supportFragment);
            tabFragments.add(settingFragment);
            SettingFragment.context = getApplicationContext();

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
            startActivityUsingStack(AlarmActivity.class);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateAlarmStatus();

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMManager.REGISTRATION_REDAY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMManager.REGISTRATION_GENERATION));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMManager.REGISTRATION_COMPLETE));

    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    private void updateAlarmStatus() {
        if (alarmMenuItem == null) {
            return;
        }

        alarmMenuItem.setIcon(hasNotSyncedAlarm() ? R.drawable.ic_alram : R.drawable.ic_no_alarm);
    }

    private boolean hasNotSyncedAlarm() {
//        ArrayList<BaasioPaylolad> receivedPushMessages = SettingFragment.getReceivedPushMessages();
        ArrayList<AlarmEntity> receivedPushMessages = SettingFragment.getReceivedPushMessages();
        if (receivedPushMessages.isEmpty()) {
            return false;
        }

        long lastSyncedTimestamp = SettingFragment.getLastAlarmSyncedTimestamp();
//        AlarmEntity latestAlarmEntity = new AlarmEntity(receivedPushMessages.get(0));
        AlarmEntity latestAlarmEntity = receivedPushMessages.get(0);
        return (lastSyncedTimestamp < latestAlarmEntity.timestamp);
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);
    }
}
