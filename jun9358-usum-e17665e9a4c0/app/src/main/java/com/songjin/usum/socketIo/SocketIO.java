package com.songjin.usum.socketIo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.songjin.usum.Global;
import com.songjin.usum.controllers.activities.LoginActivity;
import com.songjin.usum.controllers.activities.MainActivity;
import com.songjin.usum.controllers.activities.SignUpActivity;
import com.songjin.usum.dtos.SchoolRanking;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.UserEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ironFactory on 2015-08-03.
 */
public class SocketIO {

    private static final String URL = "http://sms-application.herokuapp.com";
    private static final String TAG = "SocketIO";

    public static Socket socket;
    private Context context;

    public SocketIO(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        try {
            socket = IO.socket(URL);
        } catch (Exception e) {
            Log.e(TAG, "init 에러 = " + e.getMessage());
        }

        if (socket != null) {
            socketConnect();
        }


        setListener();
    }


    private void setListener() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 연결
                Log.d(TAG, "소켓 연결");
            }
        }).on(Global.LOGIN, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 로그인
                JSONObject object = (JSONObject) args[0];
                processLogin(object);
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 연결 끊김
                Log.d(TAG, "소켓 연결 끊김");
                socketConnect();
            }
        }).on(Global.GET_SCHOOL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 학교 정보 응답
                JSONObject object = (JSONObject) args[0];
                processGetSchool(object);
            }
        }).on(Global.SIGN_UP, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processSingUp(object);
            }
        }).on(Global.SIGN_IN, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processSignIn(object);
            }
        }).on(Global.GET_SCHOOL_RANKING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject) args[0];
                processGetSchoolRanking(object);
            }
        });
    }


    // TODO: 15. 11. 20. 학교 랭킹 응답
    private void processGetSchoolRanking(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(Global.GET_SCHOOL_RANKING, code);

            if (code == SocketException.SUCCESS) {
                // 성공
                JSONArray schoolArray = object.getJSONArray(Global.SCHOOL);
                ArrayList<SchoolRanking> schoolRankingList = new ArrayList<>();
                for (int i = 0; i < schoolArray.length(); i++) {
                    JSONObject schoolObject = schoolArray.getJSONObject(i);
                    SchoolRanking schoolRanking = new SchoolRanking(schoolObject);
                    schoolRankingList.add(schoolRanking);
                }
                intent.putExtra(Global.SCHOOL, schoolRankingList);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 20. 로그인 응답
    private void processSignIn(JSONObject object) {
        Log.d(TAG, "로그인 응답");
        try {
            int code = object.getInt(Global.CODE);

            Intent intent = new Intent(context, LoginActivity.class);
            if (code == SocketException.SUCCESS) {
                // 성공
                JSONObject userObject = object.getJSONObject(Global.USER);
                UserEntity guest = new UserEntity(userObject);
                intent.putExtra(Global.USER, guest);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void processSingUp(JSONObject object) {
        // 회원가입 응답
        Log.d(TAG, "회원가입 응답");
        try {
            int code = object.getInt(Global.CODE);
            int userType = object.getInt(Global.USER_TYPE);

            Intent intent;
            if (userType == 0) {
                // 게스트모드
                intent = new Intent(context, LoginActivity.class);
            } else {
                // 정식모드
                intent = new Intent(context, SignUpActivity.class);
            }

            if (code == SocketException.SUCCESS) {
                // 성공
                JSONObject userObject = object.getJSONObject(Global.USER);
                UserEntity user = new UserEntity(userObject);
                intent.putExtra(Global.USER, user);
            }
            intent.putExtra(Global.COMMAND, Global.SIGN_UP);
            intent.putExtra(Global.CODE, code);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO: 학교 정보
     * */
    private void processGetSchool(JSONObject object) {
        try {
            int code = object.getInt(Global.CODE);
            JSONArray array = object.getJSONArray(Global.SCHOOL);
            ArrayList<SchoolEntity> schoolEntities = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject schoolObject = array.getJSONObject(i);
                schoolEntities.add(new SchoolEntity(schoolObject));
            }

            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra(Global.COMMAND, Global.GET_SCHOOL);
            intent.putExtra(Global.SCHOOL, schoolEntities);

            SocketException.printErrMsg(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO : 로그인 응답
     * */
    private void processLogin(JSONObject object) {
        Log.d(TAG, "로그인 응답");
        try {
            int code = object.getInt(Global.CODE);
            SocketException.printErrMsg(code);

            String id = null;
            if (code == SocketException.SUCCESS)
                id = object.getString(Global.ID);

            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra(Global.COMMAND, Global.LOGIN);
            intent.putExtra(Global.ID, id);
            intent.putExtra(Global.CODE, code);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static Socket getSocket() {
        return socket;
    }


    private void socketConnect() {
        socket.open();
        socket.connect();
    }


    public void signUp(UserEntity userEntity) {
        // 회원가입
        Log.d(TAG, "회원가입");

        String userId = userEntity.id;
        String realName = userEntity.realName;
        int sex = userEntity.sex.ordinal();
        int userType = userEntity.userType.ordinal();
        String phone = userEntity.phone;
        int schoolId = userEntity.schoolId;

        Log.i(TAG, "userId = " + userId);

        try {
            JSONObject object = new JSONObject();
            object.put(Global.USER_ID, userId);
            object.put(Global.REAL_NAME, realName);
            object.put(Global.SEX, sex);
            object.put(Global.USER_TYPE, userType);
            object.put(Global.PHONE, phone);
            object.put(Global.SCHOOL_ID, schoolId);

            socket.emit(Global.SIGN_UP, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void signIn(String userId) {
        // 로그인
        Log.d(TAG, "로그인");
        Log.i(TAG, "userId = " + userId);

        try {
            JSONObject object = new JSONObject();
            object.put(Global.USER_ID, userId);

            socket.emit(Global.SIGN_IN, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO: 학교 정보 받기
     * */
    public void getSchool() {
        socket.emit(Global.GET_SCHOOL, "");
    }


    // TODO: 15. 11. 20. 학교 랭킹 요청
    public void getSchoolRanking() {
        socket.emit(Global.GET_SCHOOL_RANKING, "");
    }
}
