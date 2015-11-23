package com.songjin.usum.socketIo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.songjin.usum.Global;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.entities.UserEntity;

import java.util.ArrayList;

public class SocketService extends Service {
    private static final String TAG = "SocketService";
    private SocketIO socketIO;

    public SocketService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (socketIO == null)
            socketIO = new SocketIO(getApplicationContext());

        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND);
            if (command != null) {
                Log.d(TAG, "command = " + command);
                if (command.equals(Global.GET_SCHOOL)) {
                    // 학교 정보 요청
                    processGetSchool();
                } else if (command.equals(Global.SIGN_UP)) {
                    // 회원 가입
                    processSignUp(intent);
                } else if (command.equals(Global.SIGN_IN)) {
                    // 로그인
                    processSignIn(intent);
                } else if (command.equals(Global.GET_SCHOOL_RANKING)) {
                    // 학교 랭킹
                    processGetSchoolRanking();
                } else if (command.equals(Global.SEARCH_PRODUCT)) {
                    // 제품 검색
                    processSearchProduct(intent);
                } else if (command.equals(Global.INSERT_PRODUCT)) {
                    // 제품 등록
                    processInsertProduct(intent);
                } else if (command.equals(Global.UPDATE_USER_PROFILE)) {
                    // 유저 프로필 업데이트
                    processUpdateUserProfile(intent);
                } else if (command.equals(Global.INSERT_TIMELINE_COMMENT)) {
                    // 타임라인 글에 댓글 달기
                    processInsertTimelineComment(intent);
                } else if (command.equals(Global.GET_TIMELINE_COMMENT)) {
                    // 타임라인 글 댓글 불러오기
                    processGetTimelineComment(intent);
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }


    // TODO: 15. 11. 23. 타임라인 글 댓글 불러오기
    private void processGetTimelineComment(Intent intent) {
        String id = intent.getStringExtra(Global.ID);
        socketIO.getTimelineComment(id);
    }


    // TODO: 15. 11. 23. 타임라인 글에 댓글 달기
    private void processInsertTimelineComment(Intent intent) {
        String timelineItemId = intent.getStringExtra(Global.TIMELINE_ITEM_ID);
        String commentContent = intent.getStringExtra(Global.COMMENT_CONTENT);

        socketIO.insertTimelineComment(timelineItemId, commentContent);
    }


    // TODO: 15. 11. 21. 유저 프로필 업데이트
    private void processUpdateUserProfile(Intent intent) {
        UserEntity user = intent.getParcelableExtra(Global.USER);
        socketIO.updateUserProfile(user);
    }


    // TODO: 15. 11. 20. 제품 등록
    private void processInsertProduct(Intent intent) {
        ArrayList<ProductCardDto> productCardDtos = intent.getParcelableArrayListExtra(Global.PRODUCT_CARD);
        socketIO.insertProduct(productCardDtos);
    }


    // TODO: 15. 11. 20. 제품검색
    private void processSearchProduct(Intent intent) {
        int schoolId = intent.getIntExtra(Global.SCHOOL_ID, -1);
        int sex = intent.getIntExtra(Global.SEX, -1);
        int category = intent.getIntExtra(Global.CATEGORY, -1);
        int size = intent.getIntExtra(Global.SIZE, -1);

        socketIO.searchProduct(schoolId, sex, category, size);
    }


    // TODO: 15. 11. 20. 학교 랭킹 요청
    private void processGetSchoolRanking() {
        socketIO.getSchoolRanking();
    }


    // TODO 학교 정보 요청
    private void processGetSchool() {
        socketIO.getSchool();
    }


    private void processSignIn(Intent intent) {
        // 로그인
        String userId = intent.getStringExtra(Global.USER_ID);
        socketIO.signIn(userId);
    }


    private void processSignUp(Intent intent) {
        // 회원가입
        UserEntity userEntity = intent.getParcelableExtra(Global.USER);
        socketIO.signUp(userEntity);
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
