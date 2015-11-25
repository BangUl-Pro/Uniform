package com.songjin.usum.socketIo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.songjin.usum.Global;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.dtos.TimelineCardDto;
import com.songjin.usum.dtos.TimelineCommentCardDto;
import com.songjin.usum.entities.FileEntity;
import com.songjin.usum.entities.LikeEntity;
import com.songjin.usum.entities.TransactionEntity;
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
                    processGetSchoolRanking(intent);
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
                } else if (command.equals(Global.GET_ALL_TIMELINE)) {
                    // 타임라인 글 모두 불러오기
                    processGetAllTimeline(intent);
                } else if (command.equals(Global.GET_MY_TIMELINE)) {
                    // 타임라인 글 내 글 불러오기
                    processGetMyTimeline(intent);
                } else if (command.equals(Global.DELETE_FILE)) {
                    // 파일 지우기
                    processDeleteFile(intent);
                } else if (command.equals(Global.UPDATE_TIMELINE)) {
                    // 타임라인 업데이트
                    processUpdateTimeline(intent);
                } else if (command.equals(Global.INSERT_TIMELINE)) {
                    // 타임라인 글 쓰기
                    processInsertTimeline(intent);
                } else if (command.equals(Global.GET_MY_PRODUCT)) {
                    // 내 글 쓰기
                    processGetMyProduct(intent);
                } else if (command.equals(Global.DELETE_COMMENT)) {
                    // 댓글 삭제
                    processDeleteComment(intent);
                } else if (command.equals(Global.UPDATE_TRANSACTION_STATUS)) {
                    //
                    processUpdateTransactionStatus(intent);
                } else if (command.equals(Global.DELETE_PRODUCT)) {
                    // 제품 삭제
                    processDeleteProduct(intent);
                } else if (command.equals(Global.UPDATE_PRODUCT)) {
                    // 제품 수정
                    processUpdateProduct(intent);
                } else if (command.equals(Global.INSERT_FILE)) {
                    // 파일 입력
                    processInsertFile(intent);
                } else if (command.equals(Global.DELETE_TIMELINE)) {
                    // 타임라인 지우기
                    processDeleteTimeline(intent);
                } else if (command.equals(Global.DELETE_LIKE)) {
                    // 좋아요 지우기
                    processDeleteLike(intent);
                } else if (command.equals(Global.INSERT_LIKE)) {
                    // 좋아요
                    processInsertLike(intent);
                } else if (command.equals(Global.GET_PRODUCT)) {
                    // 제품 요청
                    processGetProduct(intent);
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }


    // TODO: 15. 11. 25. 제품 요청
    private void processGetProduct(Intent intent) {
        String productJson = intent.getStringExtra(Global.PRODUCT);
        socketIO.getProduct(productJson);
    }


    // TODO: 15. 11. 25. 좋아요
    private void processInsertLike(Intent intent) {
        String timelineItemId = intent.getStringExtra(Global.TIMELINE_ITEM_ID);
        socketIO.insertLike(timelineItemId);
    }


    // TODO: 15. 11. 25. 좋아요 지우기
    private void processDeleteLike(Intent intent) {
        LikeEntity likeEntity = intent.getParcelableExtra(Global.LIKE);
        socketIO.deleteLike(likeEntity);
    }


    // TODO: 15. 11. 25. 타임라인 지우기
    private void processDeleteTimeline(Intent intent) {
        TimelineCardDto timelineCardDto = intent.getParcelableExtra(Global.TIMELINE);
        socketIO.deleteTimeline(timelineCardDto);
    }


    // TODO: 15. 11. 25. 파일 입력
    private void processInsertFile(Intent intent) {
        String id = intent.getStringExtra(Global.PRODUCT_ID);
        String path = intent.getStringExtra(Global.PATH);
        String fileName = intent.getStringExtra(Global.FILE);
        socketIO.insertFile(id, path, fileName);
    }



    // TODO: 15. 11. 25. 제품 수정
    private void processUpdateProduct(Intent intent) {
        ProductCardDto productCardDto = intent.getParcelableExtra(Global.PRODUCT_CARD);
        socketIO.updateProduct(productCardDto);
    }


    // TODO: 15. 11. 25. 제품 삭제
    private void processDeleteProduct(Intent intent) {
        ProductCardDto dto = intent.getParcelableExtra(Global.PRODUCT_CARD);
        socketIO.deleteProduct(dto);
    }


    // TODO: 15. 11. 24.
    private void processUpdateTransactionStatus(Intent intent) {
        int status = intent.getIntExtra(Global.STATUS, -1);
        TransactionEntity transactionEntity = intent.getParcelableExtra(Global.TRANSACTION);
        socketIO.updateTransactionStatus(status, transactionEntity);
    }



    // TODO: 15. 11. 24. 댓글 삭제
    private void processDeleteComment(Intent intent) {
        int from = intent.getIntExtra(Global.FROM, -1);
        ArrayList<TimelineCommentCardDto> commentCardDtos = (ArrayList) intent.getSerializableExtra(Global.TIMELINE_COMMENT);
        socketIO.deleteComment(commentCardDtos, from);
    }


    // TODO: 15. 11. 24. 내 제품 요청
    private void processGetMyProduct(Intent intent) {
        String donatorId = intent.getStringExtra(TransactionEntity.PROPERTY_DONATOR_UUID);
        String receiverId = intent.getStringExtra(TransactionEntity.PROPERTY_RECEIVER_UUID);
        socketIO.getMyProduct(donatorId, receiverId);
    }


    // TODO: 15. 11. 23. 타임라인 글 쓰기
    private void processInsertTimeline(Intent intent) {
        int schoolId = intent.getIntExtra(Global.SCHOOL_ID, -1);
        String timelineContent = intent.getStringExtra(Global.TIMELINE_CONTENT);

        socketIO.insertTimeline(schoolId, timelineContent);
    }


    // TODO: 15. 11. 23. 파일 지우기
    private void processDeleteFile(Intent intent) {
        ArrayList<FileEntity> files = intent.getParcelableArrayListExtra(Global.FILE);
        socketIO.deleteFile(files);
    }


    // TODO: 15. 11. 23. 타임라인 업데이트
    private void processUpdateTimeline(Intent intent) {
        TimelineCardDto timeline = intent.getParcelableExtra(Global.TIMELINE);
        socketIO.updateTimeline(timeline);
    }


    // TODO: 15. 11. 23. 타임라인 글 모두 불러오기
    private void processGetAllTimeline(Intent intent) {
        int schoolId = intent.getIntExtra(Global.SCHOOL_ID, -1);
        if (schoolId != -1)
            socketIO.getAllTimeline(schoolId);
    }


    // TODO: 15. 11. 23. 타임라인 내 글 불러오기
    private void processGetMyTimeline(Intent intent) {
        int schoolId = intent.getIntExtra(Global.SCHOOL_ID, -1);
        String userId = intent.getStringExtra(Global.USER_ID);
        if (schoolId != -1 && userId != null)
            socketIO.getMyTimeline(schoolId, userId);
    }


    // TODO: 15. 11. 23. 타임라인 글 댓글 불러오기
    private void processGetTimelineComment(Intent intent) {
        String id = intent.getStringExtra(Global.ID);
        int from = intent.getIntExtra(Global.FROM, -1);

        socketIO.getTimelineComment(id, from);
    }


    // TODO: 15. 11. 23. 타임라인 글에 댓글 달기
    private void processInsertTimelineComment(Intent intent) {
        String timelineItemId = intent.getStringExtra(Global.TIMELINE_ITEM_ID);
        String commentContent = intent.getStringExtra(Global.COMMENT_CONTENT);
        int from = intent.getIntExtra(Global.FROM, -1);

        socketIO.insertTimelineComment(timelineItemId, commentContent, from);
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
    private void processGetSchoolRanking(Intent intent) {
        int from = intent.getIntExtra(Global.FROM, -1);
        socketIO.getSchoolRanking(from);
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
