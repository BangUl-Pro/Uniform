package com.songjin.usum.controllers.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.controllers.views.AlarmRecyclerView;
import com.songjin.usum.R;
import com.songjin.usum.entities.AlarmEntity;

public class AlarmActivity extends BaseActivity {

    private static final String TAG = "AlarmActivity";

    private class ViewHolder {
        public AlarmRecyclerView alarms;

        public ViewHolder(View view) {
            alarms = (AlarmRecyclerView) view.findViewById(R.id.alarms);
        }
    }

    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_alarm);
    }

    @Override
    public void onResume() {
        super.onResume();

        SettingFragment.updateLastAlarmSyncedTimestamp();
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);
        Log.d(TAG, "액티비티 시작");
        viewHolder = new ViewHolder(getWindow().getDecorView());
        for (AlarmEntity alarm :
                SettingFragment.getReceivedPushMessages()) {
            Log.d(TAG, "msg = " + alarm.message);
            Log.d(TAG, "timestamp = " + alarm.timestamp);
            Log.d(TAG, "type = " + alarm.type);
        }
        viewHolder.alarms.setAlarmEntities(
//                convertBaasioPayloadsToAlarmEntities(SettingFragment.getReceivedPushMessages())
                SettingFragment.getReceivedPushMessages()
        );
    }

//    private ArrayList<AlarmEntity> convertBaasioPayloadsToAlarmEntities(ArrayList<AlarmEntity> baasioPayloads) {
//        ArrayList<AlarmEntity> alarmEntities = new ArrayList<>();
//        for (BaasioPayload baasioPayload : baasioPayloads) {
//            alarmEntities.add(new AlarmEntity(baasioPayload));
//        }
//        return alarmEntities;
//    }
}
