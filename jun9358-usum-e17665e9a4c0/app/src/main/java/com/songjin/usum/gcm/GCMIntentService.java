/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.songjin.usum.gcm;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.kth.baasio.entity.push.BaasioPayload;
import com.kth.baasio.entity.push.BaasioPush;
import com.kth.baasio.exception.BaasioException;
import com.kth.baasio.utils.JsonUtils;
import com.kth.baasio.utils.ObjectUtils;
import com.kth.common.utils.LogUtils;
import com.songjin.usum.R;
import com.songjin.usum.baasio.BaasioConfig;
import com.songjin.usum.controllers.activities.MainActivity;
import com.songjin.usum.controllers.fragments.SettingFragment;

/**
 * {@link android.app.IntentService} responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = "GCMIntentService";

    public static final String PUSH_PROPERTY_TYPE = "push_type";
    public static final int PUSH_TYPE_TRANSACTION = 1;
    public static final int PUSH_TYPE_TIMELINE = 2;
    public static final int PUSH_TYPE_RESERVATION = 3;
    public static final int PUSH_TYPE_SCHOOL_RANK_UPDATED = 4;

    public GCMIntentService() {
        super(BaasioConfig.GCM_SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        LogUtils.LOGI(TAG, "Device registered: regId=" + regId);

        try {
            BaasioPush.register(context, regId);
        } catch (BaasioException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        LogUtils.LOGI(TAG, "Device unregistered");

        try {
            BaasioPush.unregister(context);
        } catch (BaasioException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String announcement = intent.getStringExtra("message");
        if (announcement != null) {
            filterNotification(context, announcement);
        }
    }

    private static void filterNotification(Context context, String message) {
        BaasioPayload msg = JsonUtils.parse(message, BaasioPayload.class);
        if (ObjectUtils.isEmpty(msg)) {
            return;
        }

        if (msg.getProperty(PUSH_PROPERTY_TYPE) != null) {
            switch (msg.getProperty(PUSH_PROPERTY_TYPE).asInt()) {
                case PUSH_TYPE_TRANSACTION:
                    if (!SettingFragment.useTransactionPush(context)) {
                        return;
                    }
                    break;
                case PUSH_TYPE_TIMELINE:
                    if (!SettingFragment.useTimelinePush(context)) {
                        return;
                    }
                    break;
            }
        }

        generateNotification(context, msg);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    public static void generateNotification(Context context, BaasioPayload msg) {
        String alert = "";
        if (!ObjectUtils.isEmpty(msg.getAlert())) {
            alert = msg.getAlert().replace("\\r\\n", "\n");
        }

        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(context).setWhen(when)
                .setSmallIcon(icon).setContentTitle(context.getString(R.string.app_name))
                .setContentText(alert).setContentIntent(intent).setTicker(alert)
                .setAutoCancel(true).getNotification();

        notificationManager.notify(0, notification);

        msg.setProperty("timestamp", System.currentTimeMillis());
        SettingFragment.addReceivedPushMessage(msg);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.e(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.w(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }
}