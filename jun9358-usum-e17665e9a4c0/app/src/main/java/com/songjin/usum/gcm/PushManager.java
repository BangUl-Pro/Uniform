package com.songjin.usum.gcm;

import com.kth.baasio.callback.BaasioCallback;
import com.kth.baasio.entity.push.BaasioMessage;
import com.kth.baasio.entity.push.BaasioPayload;
import com.kth.baasio.entity.push.BaasioPush;
import com.kth.baasio.exception.BaasioException;
import com.songjin.usum.baasio.BaasioApplication;

import java.util.ArrayList;

public class PushManager {
    public static void sendTransactionPush(ArrayList<String> userUuids, String msg) {
        BaasioPayload payload = new BaasioPayload();
        payload.setProperty(GCMIntentService.PUSH_PROPERTY_TYPE, GCMIntentService.PUSH_TYPE_TRANSACTION);
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
        BaasioPayload payload = new BaasioPayload();
        payload.setProperty(GCMIntentService.PUSH_PROPERTY_TYPE, GCMIntentService.PUSH_TYPE_RESERVATION);
        payload.setAlert(msg);

        GCMIntentService.generateNotification(BaasioApplication.context, payload);
    }

    public static void sendSchoolRankUpdatedPushToMe(String msg) {
        BaasioPayload payload = new BaasioPayload();
        payload.setProperty(GCMIntentService.PUSH_PROPERTY_TYPE, GCMIntentService.PUSH_TYPE_SCHOOL_RANK_UPDATED);
        payload.setAlert(msg);

        GCMIntentService.generateNotification(BaasioApplication.context, payload);
    }
}
