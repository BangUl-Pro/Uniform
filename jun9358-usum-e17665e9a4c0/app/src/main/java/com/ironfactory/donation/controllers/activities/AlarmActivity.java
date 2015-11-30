package com.ironfactory.donation.controllers.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.fragments.SettingFragment;
import com.ironfactory.donation.controllers.views.AlarmRecyclerView;

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
