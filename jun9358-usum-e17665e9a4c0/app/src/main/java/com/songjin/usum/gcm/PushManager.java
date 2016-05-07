package com.songjin.usum.gcm;

import android.util.Log;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.push.PushService;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.entities.AlarmEntity;

import org.json.JSONArray;

import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class PushManager {
    private static final String TAG = "PushManager";

    public static void sendTransactionPush(ArrayList<String> userIds, String msg) {
        Log.d(TAG, "msg = " + msg);

//        final JSONArray array = new JSONArray();
//        for (String id: userIds) {
//            array.put(id);
//        }

        final String json = "{" +
                "\"for_apns\":{" +
                "\"badge\": 3," +
                "\"sound\": \"sound_file\"," +
                "\"push_alert\": true," +
                "\"message\": \"홍길동님 외 2명이 댓글을 달았습니다.\"," +
                "\"custom_field\": {" +
                "\"article_id\": \"111\"," +
                "\"comment_id\": \"222\"" +
                "}" +
                "}," +
                "\"for_gcm\":{" +
                "\"collapse\": \"articleId123\"," +
                "\"delay_while_idle\":false," +
                "\"custom_field\": {" +
                "\"msg\": \"" + msg + "\"" +
                "}" +
                "}" +
                "}";

//        sendByHttp(userIds, msg);

        for (int i = 0; i < userIds.size(); i++) {
            Log.d(TAG, "GCM Device Id = " + userIds.get(i));
            PushService.sendPushMessage(new ApiResponseCallback<Boolean>() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.d(TAG, "GCM PUSH 에러 메세지 = " + errorResult.getErrorMessage());
                    Log.d(TAG, "GCM PUSH 에러 URL = " + errorResult.getHttpStatus());
                    Log.d(TAG, "GCM PUSH 에러 코드 = " + errorResult.getErrorCode());
                }

                @Override
                public void onNotSignedUp() {
                    Log.d(TAG, "GCM PUSH Not SignedUp");
                }

                @Override
                public void onSuccess(Boolean result) {
                    Log.d(TAG, "GCM PUSH 성공");
                }
            }, json, userIds.get(i));
        }
    }


    public static void sendByHttp(final ArrayList<String> userIds, final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONArray array = new JSONArray();
                    for (String id: userIds) {
                        array.put(Integer.parseInt(id));
                    }

                    final String json = "{" +
                            "\"for_apns\":{" +
                            "\"badge\": 3," +
                            "\"sound\": \"sound_file\"," +
                            "\"push_alert\": true," +
                            "\"message\": \"홍길동님 외 2명이 댓글을 달았습니다.\"," +
                            "\"custom_field\": {" +
                            "\"article_id\": \"111\"," +
                            "\"comment_id\": \"222\"" +
                            "}" +
                            "}," +
                            "\"for_gcm\":{" +
                            "\"collapse\": \"articleId123\"," +
                            "\"delay_while_idle\":false," +
                            "\"custom_field\": {" +
                            "\"msg\": \"" + msg + "\"" +
                            "}" +
                            "}" +
                            "}";

                    URL url = new URL("https://kapi.kakao.com/v1/push/send");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "KakaoAK a6a1e884f2ecbbb1dcf154a7e49e40f0");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    conn.setRequestProperty("uuids", array.toString());
                    conn.setRequestProperty("push_message", json);

//                    StringBuilder builder = new StringBuilder();
//                    builder.append("uuids").append("=").append(array.toString()).append("&");
//                    builder.append("push_message").append("=").append(json);
//
//                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
//                    pw.write(builder.toString());
//                    pw.flush();
//                    pw.close();

                    int code = conn.getResponseCode();
                    String message = conn.getResponseMessage();
                    Log.d(TAG, "code = " + code);
                    Log.d(TAG, "message = " + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void sendReservationPushToMe(String msg) {
        GCMIntentService.generateNotification(BaseActivity.context, new AlarmEntity(msg, GCMIntentService.PUSH_TYPE_RESERVATION));
    }

    public static void sendSchoolRankUpdatedPushToMe(String msg) {
        GCMIntentService.generateNotification(BaseActivity.context, new AlarmEntity(msg, GCMIntentService.PUSH_TYPE_SCHOOL_RANK_UPDATED));
    }
}
