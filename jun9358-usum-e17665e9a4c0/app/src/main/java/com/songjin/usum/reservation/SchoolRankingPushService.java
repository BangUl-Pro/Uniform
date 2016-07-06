package com.songjin.usum.reservation;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.songjin.usum.Global;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.dtos.SchoolRanking;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.gcm.PushManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.managers.SchoolManager;

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
        if (Global.userEntity == null || Global.userEntity.schoolId == 0)
            return;
        SchoolManager schoolManager = new SchoolManager(getApplicationContext());
        RequestManager.getMySchoolRanking(new RequestManager.OnGetMySchoolRanking() {
            @Override
            public void onSuccess(int rank) {
                int lastRank = SettingFragment.getLastSchoolRank();
                int currentRank = rank;
                if (lastRank == -1 || currentRank == -1) {
                    SettingFragment.setLastSchoolRank(currentRank);
                } else if (lastRank != currentRank) {
                    PushManager.sendSchoolRankUpdatedPushToMe("학교 순위가 " + lastRank + "위에서 " + currentRank + "위로 변경되었습니다!");
                    SettingFragment.setLastSchoolRank(currentRank);
                }
                Log.d("USUM", "lastRank: " + lastRank);
                Log.d("USUM", "currentRank: " + currentRank);
            }

            @Override
            public void onException() {
                Log.d(TAG, "내 학교 순위 얻기 실패");
            }
        }, Global.userEntity.schoolId);

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