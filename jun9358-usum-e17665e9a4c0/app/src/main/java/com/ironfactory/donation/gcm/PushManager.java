package com.ironfactory.donation.gcm;

import com.ironfactory.donation.controllers.activities.BaseActivity;
import com.ironfactory.donation.entities.AlarmEntity;

import java.util.ArrayList;

public class PushManager {
    public static void sendTransactionPush(ArrayList<String> userUuids, String msg) {
        BaasioPayload payload = new BaasioPayload();
        payload.setProperty(com.songjin.usum.gcm.GCMIntentService.PUSH_PROPERTY_TYPE, com.songjin.usum.gcm.GCMIntentService.PUSH_TYPE_TRANSACTION);
        payload.setAlert(msg);

        BaasioMessage message = new BaasioMessage();
        message.setPayload(payload);
        message.setTarget(BaasioMessage.TARGET_TYPE_USER);
        message.setTo(userUuids.toArray(new String[userUuids.size()]));
        message.setPlatform(BaasioMessage.PLATFORM_FLAG_TYPE_GCM);

        BaasioPush.sendPushInBackground(
                message,
                new BaasioCallback<BaasioMessage>() {
                    @Override
                    public void onResponse(BaasioMessage response) {
                    }

                    @Override
                    public void onException(BaasioException e) {
                    }
                });
    }

    public static void sendReservationPushToMe(String msg) {
//        BaasioPayload payload = new BaasioPayload();
//        payload.setProperty(com.songjin.usum.gcm.GCMIntentService.PUSH_PROPERTY_TYPE, com.songjin.usum.gcm.GCMIntentService.PUSH_TYPE_RESERVATION);
//        payload.setAlert(msg);
//        com.songjin.usum.gcm.GCMIntentService.generateNotification(BaasioApplication.context, payload);
        GCMIntentService.generateNotification(BaseActivity.context, new AlarmEntity(msg, AlarmEntity.PUSH_TYPE_RESERVATION));
    }

    public static void sendSchoolRankUpdatedPushToMe(String msg) {
//        BaasioPayload payload = new BaasioPayload();
//        payload.setProperty(com.songjin.usum.gcm.GCMIntentService.PUSH_PROPERTY_TYPE, com.songjin.usum.gcm.GCMIntentService.PUSH_TYPE_SCHOOL_RANK_UPDATED);
//        payload.setAlert(msg);
//        com.songjin.usum.gcm.GCMIntentService.generateNotification(BaasioApplication.context, payload);
        GCMIntentService.generateNotification(BaseActivity.context, new AlarmEntity(msg, AlarmEntity.PUSH_TYPE_SCHOOL_RANK_UPDATED));
    }
}
