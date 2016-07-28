package com.songjin.usum.gcm.gcm;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.songjin.usum.R;

import java.io.IOException;

/**
 * Created by IronFactory on 2016. 5. 8..
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntentService";

    private String token = null;

    private InstanceID instanceID;

    public RegistrationIntentService() {
        super(TAG);
    }

    /**
     * GCM을 위한 Instance ID의 토큰을 생성하여 가져온다.
     * @param intent
     */
    @SuppressLint("LongLogTag")
    @Override
    protected void onHandleIntent(Intent intent) {

        // GCM Instance ID의 토큰을 가져오는 작업이 시작되면 LocalBoardcast로 GENERATING 액션을 알려 ProgressBar가 동작하도록 한다.
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(GCMManager.REGISTRATION_GENERATION));

        // GCM을 위한 Instance ID를 가져온다.
        instanceID = InstanceID.getInstance(this);

        synchronized (TAG) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long backoff = 3000;
                    for (int i = 1; i <= 5; i++) {
                        try {
                            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                            if(null != token && !token.isEmpty()) {
//                                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//                                token = gcm.register(getString(R.string.gcm_defaultSenderId));
//                                Log.d(TAG, "GCM Registration : " + token);
                                break;
                            }
                        } catch (IOException e) {
                            //Log exception
                            e.printStackTrace();
                        }
                        if (i == 5) {
                            break;
                        }
                        try {
                            Thread.sleep(backoff);
                        } catch (InterruptedException e1) {
                            break;
                        }
                        // increase backoff exponentially
                        backoff <<= 1;
                    }

                    // GCM Instance ID에 해당하는 토큰을 획득하면 LocalBoardcast에 COMPLETE 액션을 알린다.
                    // 이때 토큰을 함께 넘겨주어서 UI에 토큰 정보를 활용할 수 있도록 했다.
                    Intent registrationComplete = new Intent(GCMManager.REGISTRATION_COMPLETE);
                    registrationComplete.putExtra("token", token);
                    LocalBroadcastManager.getInstance(RegistrationIntentService.this).sendBroadcast(registrationComplete);
                }
            }).start();
        }
    }
}