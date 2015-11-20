package com.songjin.usum.controllers.activities;

import android.os.Bundle;
import android.view.View;

import com.songjin.usum.R;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.controllers.views.AlarmRecyclerView;

public class AlarmActivity extends BaseActivity {
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
        viewHolder = new ViewHolder(getWindow().getDecorView());
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
