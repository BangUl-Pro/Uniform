package com.ironfactory.donation.reservation;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ironfactory.donation.Global;
import com.ironfactory.donation.controllers.fragments.SettingFragment;
import com.ironfactory.donation.dtos.SchoolRanking;
import com.ironfactory.donation.entities.UserEntity;
import com.ironfactory.donation.managers.RequestManager;
import com.ironfactory.donation.managers.SchoolManager;

import java.util.ArrayList;

public class SchoolRankingPushService extends IntentService {
    private static final String TAG = "RankingPushService";
    private SchoolRankingCheckThread schoolRankingCheckThread;

    public SchoolRankingPushService() {
        super("SchoolRankingPushService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        schoolRankingCheckThread = new SchoolRankingCheckThread();
        schoolRankingCheckThread.run();
    }

    private class SchoolRankingCheckThread extends Thread {
        public void run() {
            while (true) {
                try {
                    checkSchoolRankUpdated();
                    sleep(60 * 60 * 1000); // 1시간마다
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkSchoolRankUpdated() {
        SchoolManager schoolManager = new SchoolManager(getApplicationContext());
        RequestManager.getSchoolRanking(new RequestManager.OnGetSchoolRanking() {
            @Override
            public void onSuccess(ArrayList<SchoolRanking> schoolRankings) {
                int lastRank = SettingFragment.getLastSchoolRank();
                int currentRank = getMyRank(schoolRankings);
                if (lastRank == -1 || currentRank == -1) {
                    SettingFragment.setLastSchoolRank(currentRank);
                } else if (lastRank != currentRank) {
//                PushManager.sendSchoolRankUpdatedPushToMe("학교 순위가 " + lastRank + "위에서 " + currentRank + "위로 변경되었습니다!");
                    Log.d(TAG, "푸시 알림 구현해야함");
                    SettingFragment.setLastSchoolRank(currentRank);
                }
                Log.d("USUM", "lastRank: " + lastRank);
                Log.d("USUM", "currentRank: " + currentRank);
            }

            @Override
            public void onException() {

            }
        });

//        RequestManager.getSchoolRankingsInBackground(schoolManager,
//                new BaasioQueryCallback() {
//                    @Override
//                    public void onResponse(List<BaasioBaseEntity> entities, List<Object> objects, BaasioQuery baasioQuery, long l) {
//                        ArrayList<SchoolRanking> schoolRankings = new ArrayList<>();
//                        for (BaasioBaseEntity entity : entities) {
//                            schoolRankings.add(new SchoolRanking(entity));
//                        }
//
//                        int lastRank = SettingFragment.getLastSchoolRank();
//                        int currentRank = getMyRank(schoolRankings);
//                        if (lastRank == -1 || currentRank == -1) {
//                            SettingFragment.setLastSchoolRank(currentRank);
//                        } else if (lastRank != currentRank) {
//                            PushManager.sendSchoolRankUpdatedPushToMe("학교 순위가 " + lastRank + "위에서 " + currentRank + "위로 변경되었습니다!");
//                            SettingFragment.setLastSchoolRank(currentRank);
//                        }
//                        Log.d("USUM", "lastRank: " + lastRank);
//                        Log.d("USUM", "currentRank: " + currentRank);
//                    }
//
//                    @Override
//                    public void onException(BaasioException e) {
//                    }
//                }
//        );
    }

    private int getMyRank(ArrayList<SchoolRanking> schoolRankings) {
//        UserEntity userEntity = new UserEntity(Baas.io().getSignedInUser());
        UserEntity userEntity = Global.userEntity;

        int myRanking = -1;
        for (int i = 0; i < schoolRankings.size(); i++) {
            SchoolRanking schoolRanking = schoolRankings.get(i);

            if (userEntity.schoolId == schoolRanking.school_id) {
                myRanking = i + 1;
                break;
            }
        }

        return myRanking;
    }
}