package com.songjin.usum.reservation;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.kth.baasio.Baas;
import com.kth.baasio.callback.BaasioQueryCallback;
import com.kth.baasio.entity.BaasioBaseEntity;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.query.BaasioQuery;
import com.songjin.usum.baasio.BaasioApplication;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.dtos.SchoolRanking;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.gcm.PushManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.managers.SchoolManager;

import java.util.ArrayList;
import java.util.List;

public class SchoolRankingPushService extends IntentService {
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
        SchoolManager schoolManager = new SchoolManager(BaasioApplication.context);
        RequestManager.getSchoolRankingsInBackground(schoolManager,
                new BaasioQueryCallback() {
                    @Override
                    public void onResponse(List<BaasioBaseEntity> entities, List<Object> objects, BaasioQuery baasioQuery, long l) {
                        ArrayList<SchoolRanking> schoolRankings = new ArrayList<>();
                        for (BaasioBaseEntity entity : entities) {
                            schoolRankings.add(new SchoolRanking(entity));
                        }

                        int lastRank = SettingFragment.getLastSchoolRank();
                        int currentRank = getMyRank(schoolRankings);
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
                    public void onException(BaasioException e) {
                    }
                }
        );
    }

    private int getMyRank(ArrayList<SchoolRanking> schoolRankings) {
        UserEntity userEntity = new UserEntity(Baas.io().getSignedInUser());

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