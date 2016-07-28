package com.songjin.usum.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;

import com.github.nkzawa.emitter.Emitter;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.dtos.SchoolRanking;
import com.songjin.usum.dtos.TimelineCardDto;
import com.songjin.usum.dtos.TimelineCommentCardDto;
import com.songjin.usum.entities.FileEntity;
import com.songjin.usum.entities.LikeEntity;
import com.songjin.usum.entities.ProductEntity;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.TransactionEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.socketIo.SocketIO;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RequestManager {
    public static Context context;
    private static final String TAG = "RequestManager";
    private static Handler handler = new Handler();

    public static void downloadImage(final String path, final File file, final OnDownloadImage onDownloadImage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    if (conn != null) {
                        int statusCode = conn.getResponseCode();

                        if (statusCode != HttpURLConnection.HTTP_OK) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onDownloadImage.onException();
                                }
                            });
                            return;
                        }

                        InputStream is = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);

                        FileOutputStream fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onDownloadImage.onSuccess();
                            }
                        });
                        fos.close();
                        is.close();
                    }

                    conn.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    onDownloadImage.onException();
                } catch (IOException e) {
                    e.printStackTrace();
                    onDownloadImage.onException();
                }
            }
        }).start();
    }


    public static void insertProduct(ProductCardDto productCardDto, OnInsertProduct onInsertProduct) {
        SocketIO.insertProduct(productCardDto, onInsertProduct);
    }


    public static void insertTransaction(final TransactionEntity transactionEntity) {
        SocketIO.insertTransaction(transactionEntity);
    }


    public static void getMyProduct(UserEntity userEntity, RequestManager.OnGetMyProduct onGetMyProduct) {
        SocketIO.getMyProduct(userEntity.id, onGetMyProduct);
    }


    public static void searchProduct(int schoolId, int sex, int category, int position, int size, OnSearchProduct onSearchProduct) {
        SocketIO.searchProduct(schoolId, sex, category, size, position, onSearchProduct);
    }


    public static void deleteProduct(String productId, OnDeleteProduct onDeleteProduct) {
        SocketIO.deleteProduct(productId, onDeleteProduct);
    }

    public static void updateProduct(ProductCardDto productCardDto, boolean isDeleteFile, OnUpdateProduct onUpdateProduct) {
        SocketIO.updateProduct(productCardDto, isDeleteFile, onUpdateProduct);
    }


    public static void insertFile(String productId, String path, int position, int type, OnInsertFile onInsertFile) {
        SocketIO.insertFile(productId, path, position, type, onInsertFile);
    }

    public static void deleteFile(ArrayList<FileEntity> fileEntities, OnDeleteFile onDeleteFile) {
        SocketIO.deleteFile(fileEntities, onDeleteFile);
    }


    public static void updateTimeline(TimelineCardDto timelineCardDto, OnInsertTimeline onInsertTimeline) {
        SocketIO.updateTimeline(timelineCardDto, onInsertTimeline);
    }


    public static void insertTimeline(int schoolId, ArrayList<Uri> uris, String content, String userId, OnInsertTimeline onInsertTimeline) {
        SocketIO.insertTimeline(schoolId, content, userId, uris, onInsertTimeline);
    }

    public static void insertLike(String timelineItemId, String userId, OnInsertLike onInsertLike) {
        SocketIO.insertLike(timelineItemId, userId, onInsertLike);
    }


    public static void getAllTimeline(String userId, int schoolId, OnGetAllTimeline onGetAllTimeline) {
        SocketIO.getAllTimeline(schoolId, userId, 0, onGetAllTimeline);
    }

    public static void getAllTimeline(String userId, int schoolId, long time, OnGetAllTimeline onGetAllTimeline) {
        SocketIO.getAllTimeline(schoolId, userId, time, onGetAllTimeline);
    }

    public static void getMyTimeline(String userId, int schoolId, OnGetMyTimeline onGetMyTimeline) {
        SocketIO.getMyTimeline(schoolId, userId, onGetMyTimeline);
    }

    public static void deleteComment(TimelineCommentCardDto timelineCommentCardDto, OnDeleteComment onDeleteComment) {
        SocketIO.deleteComment(timelineCommentCardDto, onDeleteComment);
    }

    public static void deleteLike(LikeEntity likeEntity, OnDeleteLike onDeleteLike) {
        SocketIO.deleteLike(likeEntity, onDeleteLike);
    }

    public static void deleteTimeline(TimelineCardDto timelineCardDto, OnDeleteTimeline onDeleteTimeline) {
        SocketIO.deleteTimeline(timelineCardDto, onDeleteTimeline);
    }

    public static void signInKakao(long id, String nickName, String profileImage, String thumbnailImage, OnSignInKakao onSignInKakao) {
        SocketIO.signInKakao(id, nickName, profileImage, thumbnailImage, onSignInKakao);
    }


    public static void getSchool(OnGetSchool onGetSchool) {
    }


    public static void insertTimelineComment(String timelineId, String comment, String userId, OnInsertTimelineComment onInsertTimelineComment) {
    }


    public static void getTimelineComment(String timelineId, OnGetTimelineComment onGetTimelineComment) {
        SocketIO.getTimelineComment(timelineId, onGetTimelineComment);
    }


    public static void updateUserProfile(UserEntity userEntity, OnUpdateUserProfile onUpdateUserProfile) {
        SocketIO.updateUserProfile(userEntity, onUpdateUserProfile);
    }


    public static void getSchoolRanking(OnGetSchoolRanking onGetSchoolRanking) {
        SocketIO.getSchoolRanking(onGetSchoolRanking);
    }

    public static void getSchoolRanking(OnGetSchoolRanking onGetSchoolRanking, int schoolId) {
        SocketIO.getSchoolRanking(onGetSchoolRanking, schoolId);
    }

    public static void getMySchoolRanking(OnGetMySchoolRanking onGetSchoolRanking, int rank) {
        SocketIO.getMySchoolRanking(onGetSchoolRanking, rank);
    }


    public static void updateTransactionStatus(TransactionEntity transactionEntity, int status, OnUpdateTransactionStatus onUpdateTransactionStatus) {
        SocketIO.updateTransactionStatus(status, transactionEntity, onUpdateTransactionStatus);
    }

    public static void deleteUser(String userId, Emitter.Listener listener) {
        SocketIO.deleteUser(userId, listener);
    }


    public static void signUp(UserEntity userEntity, OnSignUp onSignUp) {
        SocketIO.signUp(userEntity, onSignUp);
    }

    public static void signIn(String userId, OnSignIn onSignIn) {
        SocketIO.signIn(userId, onSignIn);
    }


    public static void getProduct(JSONObject object, OnGetProduct onGetProduct) {
        SocketIO.getProduct(object, onGetProduct);
    }


    public interface OnGetProduct {
        void onSuccess(ArrayList<ProductCardDto> productCardDtos);
        void onException();
    }


    public interface OnSignIn {
        void onSuccess(UserEntity userEntity);
        void onException();
    }

    public interface OnSignUp {
        void onSuccess(UserEntity userEntity);
        void onException(int code);
    }


    public interface OnUpdateTransactionStatus {
        void onSuccess(TransactionEntity transactionEntity);
        void onException();
    }

    public interface OnGetSchoolRanking {
        void onSuccess(ArrayList<SchoolRanking> schoolRankings);
        void onException();
    }

    public interface OnGetMySchoolRanking {
        void onSuccess(int rank);
        void onException();
    }

    public interface OnSetDeviceId {
        void onSuccess();
        void onException();
    }

    public interface OnSetToken {
        void onSuccess();
        void onException();
    }

    public interface OnSetHasExtraProfile {
        void onSuccess();
        void onException();
    }

    public interface OnUpdateUserProfile {
        void onSuccess(UserEntity userEntity);
        void onException();
    }


    public interface OnGetTimelineComment {
        void onSuccess(ArrayList<TimelineCommentCardDto> timelineCommentCardDtos);
        void onException();
    }


    public interface OnInsertTimelineComment {
        void onSuccess();
        void onException();
    }

    public interface OnGetSchool {
        void onSuccess(ArrayList<SchoolEntity> schoolEntities);
        void onException();
    }


    public interface OnSignInKakao {
        void onSuccess(UserEntity userEntity);
        void onException(int code);
    }

    public interface OnUpdateProduct {
        void onSuccess(ProductEntity productEntity);
        void onException();
    }


    public interface OnDeleteProduct {
        void onSuccess();
        void onException();
    }


    public interface OnSearchProduct {
        void onSuccess(ArrayList<ProductCardDto> productCardDtos);
        void onException();
    }


    public interface OnInsertProduct {
        void onSuccess(ProductCardDto productCardDto);
        void onException();
    }


    public interface OnDownloadImage {
        void onSuccess();
        void onException();
    }

    public interface OnDeleteFile {
        void onSuccess();
        void onException();
    }

    public interface OnInsertTimeline {
        void onSuccess(TimelineCardDto timelineCardDto);
        void onException();
    }


    public interface OnGetMyProduct {
        void onSuccess(ArrayList<ProductCardDto> productCardDtos);
        void onException(int code);
    }

    public interface OnDeleteComment {
        void onSuccess();
        void onException();
    }

    public interface OnDeleteTimeline {
        void onSuccess();
        void onException(int code);
    }

    public interface OnDeleteLike {
        void onSuccess();
        void onException(int code);
    }

    public interface OnInsertLike {
        void onSuccess(LikeEntity likeEntity);
        void onException(int code);
    }

    public interface OnInsertFile {
        void onSuccess(int position);
        void onException(int code);
    }

    public interface OnGetAllTimeline {
        void onSuccess(ArrayList<TimelineCardDto> timelineCardDtos);
        void onException(int code);
    }

    public interface OnGetMyTimeline {
        void onSuccess(ArrayList<TimelineCardDto> timelineCardDtos);
        void onException(int code);
    }
}
