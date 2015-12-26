package com.songjin.usum.gcm;

import android.util.Log;

import com.kakao.APIErrorResult;
import com.kakao.PushSendHttpResponseHandler;
import com.kakao.PushService;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.entities.AlarmEntity;

import org.json.JSONArray;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class PushManager {
    private static final String TAG = "PushManager";

    public static void sendTransactionPush(ArrayList<String> userIds, String msg) {
        Log.d(TAG, "msg = " + msg);

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



        Log.d(TAG, "userList = " + array.toString());

//        sendByHttp(userIds, msg);

        PushService.sendPushMessage(new PushSendHttpResponseHandler() {
            @Override
            protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                Log.d(TAG, "GCM PUSH 에러 메세지 = " + errorResult.getErrorMessage());
                Log.d(TAG, "GCM PUSH 에러 URL = " + errorResult.getRequestURL());
                Log.d(TAG, "GCM PUSH 에러 코드 = " + errorResult.getErrorCodeInt());
            }
        }, json, array.toString());

//        for (final String userId : userIds) {
//            String json = "{" +
//                    "\"for_apns\":{" +
//                    "\"badge\": 3," +
//                    "\"sound\": \"sound_file\"," +
//                    "\"push_alert\": true," +
//                    "\"message\": \"홍길동님 외 2명이 댓글을 달았습니다.\"," +
//                    "\"custom_field\": {" +
//                    "\"article_id\": \"111\"," +
//                    "\"comment_id\": \"222\"" +
//                    "}" +
//                    "}," +
//                    "\"for_gcm\":{" +
//                    "\"collapse\": \"articleId123\"," +
//                    "\"delay_while_idle\":false," +
//                    "\"custom_field\": {" +
//                    "\"msg\": \"" + msg + "\"" +
//                    "}" +
//                    "}" +
//                    "}";
//            Log.d(TAG, "json = " + json);
//            JSONArray array = new JSONArray();
//
//            Log.d(TAG, "userId = " + userId);
//            PushService.sendPushMessage(new PushSendHttpResponseHandler() {
//                @Override
//                protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
//                    Log.d(TAG, "GCM PUSH 에러 메세지 = " + errorResult.getErrorMessage());
//                    Log.d(TAG, "GCM PUSH 에러 URL = " + errorResult.getRequestURL());
//                    Log.d(TAG, "GCM PUSH 에러 코드 = " + errorResult.getErrorCodeInt());
//                }
//            }, json, userId);
//        }
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

                    StringBuilder builder = new StringBuilder();
                    builder.append("uuids").append("=").append(array.toString()).append("&");
                    builder.append("push_message").append("=").append(json);

                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
                    pw.write(builder.toString());
                    pw.flush();
                    pw.close();

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
