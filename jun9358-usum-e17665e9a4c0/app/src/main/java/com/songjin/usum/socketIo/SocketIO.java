package com.songjin.usum.socketIo;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.songjin.usum.Global;
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
import com.songjin.usum.managers.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ironFactory on 2015-08-03.
 */
public class SocketIO {

    private static Handler handler = new Handler();
    private static final String SERVER_URL = "http://uniform-donation.herokuapp.com";
    private static final String TAG = "SocketIO";

    public static Socket socket;
    private Context context;

    public SocketIO(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        Log.d(TAG, "init");
        try {
            socket = IO.socket(SERVER_URL);
        } catch (Exception e) {
            Log.e(TAG, "init 에러 = " + e.getMessage());
        }

        if (socket != null) {
            socketConnect();
        }

        if (!Global.isCreated)
            setListener();
        Global.isCreated = true;
    }


    private void setListener() {
        Log.d(TAG, "setListener");
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 연결
                Log.d(TAG, "소켓 연결");
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 연결 끊김
                Log.d(TAG, "소켓 연결 끊김");
                socketConnect();
            }
        });
    }


    public static Socket getSocket() {
        return socket;
    }


    private void socketConnect() {
        socket.open();
        socket.connect();
    }


    public static void signUp(UserEntity userEntity, final RequestManager.OnSignUp onSignUp) {
        // 회원가입
        Log.d(TAG, "회원가입");

        String userId = userEntity.id;
        String realName = userEntity.realName;
        int sex = userEntity.sex;
        int userType = userEntity.userType;
        String phone = userEntity.phone;
        int schoolId = userEntity.schoolId;

        Log.i(TAG, "userId = " + userId);

        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.USER_ID, userId);
            object.put(Global.REAL_NAME, realName);
            object.put(Global.SEX, sex);
            object.put(Global.USER_TYPE, userType);
            object.put(Global.PHONE, phone);
            object.put(Global.SCHOOL_ID, schoolId);

            socket.emit(Global.SIGN_UP, object);
            socket.once(Global.SIGN_UP, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        if (code == SocketException.SUCCESS) {
                            JSONObject userObject = resObject.getJSONObject(Global.USER);
                            Log.d(TAG, "userObject = " + userObject);
                            final UserEntity user = new UserEntity(userObject);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSignUp.onSuccess(user);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSignUp.onException(code);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void signIn(String userId, final RequestManager.OnSignIn onSignIn) {
        // 로그인
        Log.d(TAG, "로그인");
        Log.i(TAG, "userId = " + userId);

        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.USER_ID, userId);

            socket.emit(Global.SIGN_IN, object);
            socket.once(Global.SIGN_IN, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

                        if (code == SocketException.SUCCESS) {
                            JSONObject userObject = resObject.getJSONObject(Global.USER);
                            final UserEntity guest = new UserEntity(userObject);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSignIn.onSuccess(guest);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSignIn.onException();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * TODO: 학교 정보 요청
     * */
    public static void getSchool(final RequestManager.OnGetSchool onGetSchool) {
        Log.d(TAG, "학교 정보 요청");
        if (!checkSocket())
            return;
        socket.emit(Global.GET_SCHOOL, "");
        socket.once(Global.GET_SCHOOL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject resObject = getJson(args);
                    final int code = getCode(resObject);
                    Log.d(TAG, "학교 정보 응답 resObject = " + resObject);

                    if (code == SocketException.SUCCESS) {
                        JSONArray array = resObject.getJSONArray(Global.SCHOOL);
                        final ArrayList<SchoolEntity> schoolEntities = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject schoolObject = array.getJSONObject(i);
                            schoolEntities.add(new SchoolEntity(schoolObject));
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetSchool.onSuccess(schoolEntities);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetSchool.onException();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
//        insertSchool();
    }


    private static void insertSchool() {
        Log.d(TAG, "학교 데이터 입력 ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    String serviceKey = "AgIevc%2B9UJQ8VK0tGD%2FcO1BTMIPNnklsq7Vsa7LT%2Bu6aBTy5b42HH2r9Y4cI1mNdf%2Bp%2BZ%2B%2Bsg5Unml1IJcChuw%3D%3D";
//                    serviceKey = URLEncoder.encode(serviceKey, "UTF-8");
                    String urlStr = "http://api.data.go.kr/openapi/4e1a3cda-db21-40b3-b4f8-a1e7de2993bd?s_page=1&s_list=10000&numOfRows=999&pageNo=1&type=xml&encoding=UTF-8&serviceKey=Lxbm1ybyU8N5PDZs85%2Fq7lPkVo9xuf2eienU0jAfV2YFMTZBElEvProHiKucWYZ5sS4R1fAA1nolBb2u7ttxcg%3D%3D";
//                    String urlStr = "http://api.data.go.kr/openapi/4e1a3cda-db21-40b3-b4f8-a1e7de2993bd?s_page=1&s_list=10000&numOfRows=999&pageNo=1&type=xml&serviceKey=AgIevc%2B9UJQ8VK0tGD%2FcO1BTMIPNnklsq7Vsa7LT%2Bu6aBTy5b42HH2r9Y4cI1mNdf%2Bp%2BZ%2B%2Bsg5Unml1IJcChuw%3D%3D";
//                    urlStr = URLEncoder.encode(urlStr, "UTF-8");
                    URL url = new URL(urlStr);
                    try {
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        XmlPullParser parser = factory.newPullParser();
                        parser.setInput(url.openStream(), "UTF-8");
                        int eventType = parser.getEventType();

                        final String CATEGORY = "학교급";
                        final String MIDDLE_SCHOOL = "중학교";
                        final String HIGH_SCHOOL = "고등학교";

                        final String ADDRESS = "소재지지번주소";
                        final String NAME = "학교명";
                        final String START = "com.google.gson.internal.LinkedTreeMap";

                        ArrayList<SchoolEntity> schoolEntities = new ArrayList<>();
                        SchoolEntity schoolEntity = null;

                        boolean isCategory = false;
                        boolean isAddress = false;
                        boolean isName = false;
                        boolean isTarget = false;
                        boolean isText = false;

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    String tag = parser.getName();
                                    if (tag.equals(START)) {
                                        schoolEntity = new SchoolEntity();
                                        Log.d(TAG, "시작");
                                    }
                                    isText = true;
                                    break;

                                case XmlPullParser.TEXT:
                                    String text = parser.getText();
                                    if (text.equals(CATEGORY)) {
                                        isCategory = true;
                                    } else if (text.equals(ADDRESS)) {
                                        isAddress = true;
                                    } else if (text.equals(NAME)) {
                                        isName = true;
                                    } else if (isCategory) {
                                        // 학교급
                                        if (text.equals(MIDDLE_SCHOOL) || text.equals(HIGH_SCHOOL)) {
                                            schoolEntity.category = text;
                                            Log.d(TAG, "타겟임 " + text);
                                            isTarget = true;
                                            isCategory = false;
                                        } else if (isText) {
                                            Log.d(TAG, "타겟아님 " + text);
                                            isTarget = false;
                                            isCategory = false;
                                        }
                                    } else if (isAddress) {
                                        // 주소
                                        if (isTarget && isText) {
                                            Log.d(TAG, text);
                                            schoolEntity = setAddress(schoolEntity, text);
                                            isAddress = false;
                                        }
                                    } else if (isName) {
                                        if (isTarget && isText) {
                                            Log.d(TAG, text);
                                            schoolEntity.schoolname = text;
                                            isName = false;
                                        }
                                    }
                                    break;

                                case XmlPullParser.END_TAG:
                                    String endTag = parser.getName();
                                    if (endTag.equals(START)) {
                                        if (isTarget) {
                                            schoolEntity.id = schoolEntities.size() + 1;
                                            schoolEntities.add(schoolEntity);
                                            Log.d(TAG, "끝");
                                        }
                                    }

                                    isText = false;
                                    break;
                            }

                            eventType = parser.next();
                        }

                        Gson gson = new Gson();
                        String json = gson.toJson(schoolEntities);
                        JSONArray array = new JSONArray(json);
                        Log.d(TAG, "array = " + array);
                        socket.emit("insertSchool", array);
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Log.d(TAG, "urlStr = " + urlStr);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    httpURLConnection.setRequestMethod("GET");
//                    if (httpURLConnection != null) {
//                        int resCode = httpURLConnection.getResponseCode();
//                        if (resCode == HttpURLConnection.HTTP_OK) {
//                            StringBuilder sb = new StringBuilder();
//                            BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
//                            while (true) {
//                                String line = reader.readLine();
//                                if (line == null)
//                                    break;
//                                sb.append(line);
//                            }
//                            String school = sb.toString();
//                            Log.d(TAG, "school = " + school);
//                            reader.close();
//                            processGetSchool(school);
//                    httpURLConnection.disconnect();
//
//
//                        }
//                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    private static SchoolEntity setAddress(SchoolEntity schoolEntity, String address) {
        schoolEntity.address = address;

        int firstSpace = address.indexOf(" ");
        int secondSpace = address.indexOf(" ", firstSpace + 1);

        schoolEntity.city = address.substring(0, firstSpace).trim();
        schoolEntity.gu = address.substring(firstSpace, secondSpace).trim();

        Log.d(TAG, "city = " + schoolEntity.city);
        Log.d(TAG, "gu = " + schoolEntity.gu);
        return schoolEntity;
    }


    private static boolean checkSocket() {
        if (socket == null)
            return false;
        return true;
    }


    // TODO: 15. 11. 20. 학교 랭킹 요청
    public static void getSchoolRanking(final RequestManager.OnGetSchoolRanking onGetSchoolRanking) {
        Log.d(TAG, "학교 랭킹 요청");
        if (!checkSocket())
            return;
        socket.emit(Global.GET_SCHOOL_RANKING, "");
        socket.once(Global.GET_SCHOOL_RANKING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject resObject = getJson(args);
                    final int code = getCode(resObject);
                    Log.d(TAG, "학교랭킹 응답 resObject = " + resObject);

                    if (code == SocketException.SUCCESS) {
                        // 성공
                        JSONArray schoolArray = resObject.getJSONArray(Global.SCHOOL);
                        final ArrayList<SchoolRanking> schoolRankingList = new ArrayList<>();
                        for (int i = 0; i < schoolArray.length(); i++) {
                            JSONObject schoolObject = schoolArray.getJSONObject(i);
                            SchoolRanking schoolRanking = new SchoolRanking(schoolObject);
                            schoolRankingList.add(schoolRanking);
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetSchoolRanking.onSuccess(schoolRankingList);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetSchoolRanking.onException();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // TODO: 15. 11. 20. 제품검색
    public static void searchProduct(int schoolId, int sex, int category, int size, int position, final RequestManager.OnSearchProduct onSearchProduct) {
        Log.d(TAG, "제품 검색 ");
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.SEX, sex);
            object.put(Global.CATEGORY, category);
            object.put(Global.SIZE, size);
            object.put(Global.POSITION, position);
            Log.d(TAG, "searchProduct Object = " + object);
            socket.emit(Global.SEARCH_PRODUCT, object);
            socket.once(Global.SEARCH_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject reqObject = getJson(args);
                        int code = getCode(reqObject);
                        if (code == SocketException.SUCCESS) {
                            final ArrayList<ProductCardDto> products = new ArrayList<>();
                            JSONArray array = reqObject.getJSONArray(Global.PRODUCT);
                            Log.d(TAG, "제품 검색 array = " + array);
                            Log.d(TAG, "제품 검색 arraySize = " + array.length());
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject productJson = array.getJSONObject(i);
                                ProductCardDto dto = new ProductCardDto(productJson);
                                if (i != 0) {
                                    int size = products.size() - 1;
                                    if (products.get(size).isSame(dto)) {
                                        products.get(size).addFile(dto.fileEntities.get(0));
                                        continue;
                                    }
                                }
                                products.add(dto);
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "제품 검색 크기 = " + products.size());
                                    onSearchProduct.onSuccess(products);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSearchProduct.onException();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static JSONObject getJson(Object... args) {
        JSONObject object = (JSONObject) args[0];
        return object;
    }


    private static int getCode(JSONObject object) throws JSONException{
        return object.getInt(Global.CODE);
    }


    // TODO: 15. 11. 20. 제품 등록
    public static void insertProduct(ProductCardDto productCardDto, final RequestManager.OnInsertProduct onInsertProduct) {
        try {
            if (!checkSocket())
                return;
            Gson gson = new Gson();
            JSONObject object = new JSONObject(gson.toJson(productCardDto.productEntity));
            Log.d(TAG, "insertProduct Object = " + object);
            socket.emit(Global.INSERT_PRODUCT, object);
            socket.once(Global.INSERT_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject reqObject = (JSONObject) args[0];
                        int code = reqObject.getInt(Global.CODE);
                        Log.d(TAG, "제품 등록 응답 = " + reqObject);

                        if (code == SocketException.SUCCESS) {
                            JSONObject productJson = reqObject.getJSONObject(Global.PRODUCT);
                            final ProductCardDto resProductCardDto = new ProductCardDto();
                            resProductCardDto.productEntity = new ProductEntity(productJson);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onInsertProduct.onSuccess(resProductCardDto);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onInsertProduct.onException();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 21. 유저 프로필 업데이트
    public static void updateUserProfile(UserEntity user, final RequestManager.OnUpdateUserProfile onUpdateUserProfile) {
        try {
            if (!checkSocket())
                return;
            Gson gson = new Gson();
            String json = gson.toJson(user, UserEntity.class);
            JSONObject object = new JSONObject(json);
            Log.d(TAG, "updateUserProfile Object = " + object);
            socket.emit(Global.UPDATE_USER_PROFILE, object);
            socket.once(Global.UPDATE_USER_PROFILE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "프로필 수정 응답 resObject = " + resObject);
                        if (code == SocketException.SUCCESS) {
                            // 성공
                            JSONObject userObject = resObject.getJSONObject(Global.USER);
                            final UserEntity user = new UserEntity(userObject);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onUpdateUserProfile.onSuccess(user);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onUpdateUserProfile.onException();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 게시글에 댓글 달기
    public static void insertTimelineComment(String timelineItemId, String commentContent, String userId, final RequestManager.OnInsertTimelineComment onInsertTimelineComment) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.TIMELINE_ITEM_ID, timelineItemId);
            object.put(Global.COMMENT_CONTENT, commentContent);
            object.put(Global.USER_ID, userId);
            Log.d(TAG, "insertTimelineComment Object = " + object);
            socket.emit(Global.INSERT_TIMELINE_COMMENT, object);
            socket.once(Global.INSERT_TIMELINE_COMMENT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "타임라인 댓글 입력 응답 resObject = " + resObject);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == SocketException.SUCCESS) {
                                    onInsertTimelineComment.onSuccess();
                                } else {
                                    onInsertTimelineComment.onException();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 게시글 댓글 불러오기
    public static void getTimelineComment(String timelineId, final RequestManager.OnGetTimelineComment onGetTimelineComment) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.ID, timelineId);
            Log.d(TAG, "getTimelineComment Object = " + object);
            socket.emit(Global.GET_TIMELINE_COMMENT, object);
            socket.once(Global.GET_TIMELINE_COMMENT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "타임라인 댓글 응답 resObject = " + resObject);

                        if (code == SocketException.SUCCESS) {
                            // 성공
                            final ArrayList<TimelineCommentCardDto> timelineCommentCardDtos = new ArrayList<>();
                            JSONArray timelineCommentArray = resObject.getJSONArray(Global.TIMELINE_COMMENT);
                            for (int i = 0; i < timelineCommentArray.length(); i++) {
                                JSONObject timelineCommentObject = timelineCommentArray.getJSONObject(i);
                                TimelineCommentCardDto comment = new TimelineCommentCardDto(timelineCommentObject);
                                timelineCommentCardDtos.add(comment);
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetTimelineComment.onSuccess(timelineCommentCardDtos);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetTimelineComment.onException();
                                }
                            });
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 글 모두 불러오기
    public static void getAllTimeline(int schoolId, String userId, final RequestManager.OnGetAllTimeline onGetAllTimeline) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.USER_ID, userId);
            Log.d(TAG, "getAllTimeline Object = " + object);
            socket.emit(Global.GET_ALL_TIMELINE, object);
            socket.once(Global.GET_ALL_TIMELINE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "모든 타임라인 리스트 응답 resObject = " + resObject);
                        if (code == SocketException.SUCCESS) {
                            // 성공
                            final ArrayList<TimelineCardDto> timelineCardDtos = new ArrayList<>();
                            JSONArray timelineArray = resObject.getJSONArray(Global.TIMELINE);
                            for (int i = 0; i < timelineArray.length(); i++) {
                                JSONObject timelineObject = timelineArray.getJSONObject(i);
                                TimelineCardDto timelineCardDto = new TimelineCardDto();
                                timelineCardDto.setTimeline(timelineObject);
                                timelineCardDto.setUser(timelineObject);
                                timelineCardDto.setLike(timelineObject);
                                int size = timelineCardDtos.size() - 1;
                                if (i != 0 && timelineCardDto.isSame(timelineCardDtos.get(size))) {
                                    timelineCardDtos.get(size).addFile(timelineObject);
                                } else {
                                    timelineCardDto.setFile(timelineObject);
                                    timelineCardDtos.add(timelineCardDto);
                                }
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetAllTimeline.onSuccess(timelineCardDtos);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetAllTimeline.onException(code);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 내 글 불러오기
    public static void getMyTimeline(int schoolId, String userId, final RequestManager.OnGetMyTimeline onGetMyTimeline) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.USER_ID, userId);
            Log.d(TAG, "getMyTimeline Object = " + object);
            socket.emit(Global.GET_MY_TIMELINE, object);
            socket.once(Global.GET_MY_TIMELINE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "내 타임라인 리스트 응답 resObject = " + resObject);
                        if (code == SocketException.SUCCESS) {
                            // 성공
                            final ArrayList<TimelineCardDto> timelineCardDtos = new ArrayList<>();
                            JSONArray timelineArray = resObject.getJSONArray(Global.TIMELINE);
                            for (int i = 0; i < timelineArray.length(); i++) {
                                JSONObject timelineObject = timelineArray.getJSONObject(i);
                                TimelineCardDto timelineCardDto = new TimelineCardDto();
                                timelineCardDto.setTimeline(timelineObject);
                                timelineCardDto.setUser(timelineObject);
                                timelineCardDto.setLike(timelineObject);
                                timelineCardDto.setFile(timelineObject);
                                timelineCardDtos.add(timelineCardDto);
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetMyTimeline.onSuccess(timelineCardDtos);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetMyTimeline.onException(code);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 글 쓰기
    public static void insertTimeline(int schoolId, String timelineContent, String id, ArrayList<Uri> files, final RequestManager.OnInsertTimeline onInsertTimeline) {
        try {
            if (!checkSocket())
                return;
            Log.d(TAG, "schoolId = " + schoolId);
            Log.d(TAG, "timelineContent = " + timelineContent);

            Gson gson = new Gson();
            JSONArray array = new JSONArray(gson.toJson(files));
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.USER_ID, id);
            object.put(Global.TIMELINE_CONTENT, timelineContent);
            object.put(Global.FILE, array);
            Log.d(TAG, "insertTimeline Object = " + object);
            socket.emit(Global.INSERT_TIMELINE, object);
            socket.once(Global.INSERT_TIMELINE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "타임라인 입력 응답 resObject = " + resObject);
                        if (code == SocketException.SUCCESS) {
                            final JSONObject timelineJson = resObject.getJSONObject(Global.TIMELINE);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    TimelineCardDto dto = new TimelineCardDto(timelineJson);
                                    onInsertTimeline.onSuccess(dto);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onInsertTimeline.onException();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 파일 지우기
    public static void deleteFile(ArrayList<FileEntity> files, final RequestManager.OnDeleteFile onDeleteFile) {
        try {
            if (!checkSocket())
                return;
            for (int i = 0; i < files.size(); i++) {
                Log.d(TAG, i + "번째 id = " + files.get(i).id);
                Log.d(TAG, i + "번째 parent_uuid = " + files.get(i).parent_uuid);

                if (files.get(i).id == null || files.get(i).id.equals(null))
                    return;

                if (files.get(i).parent_uuid == null || files.get(i).parent_uuid.equals(null))
                    return;
            }

            Gson gson = new Gson();
            String json = gson.toJson(files);
            JSONArray array = new JSONArray(json);
            JSONObject object = new JSONObject();
            object.put(Global.FILE, array);
            Log.d(TAG, "deleteFile Object = " + object);
            socket.emit(Global.DELETE_FILE, object);
            socket.once(Global.DELETE_FILE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "파일 삭제 응답 resObject = " + resObject);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == SocketException.SUCCESS) {
                                    onDeleteFile.onSuccess();
                                } else {
                                    onDeleteFile.onException();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 23. 타임라인 업데이트
    public static void updateTimeline(TimelineCardDto timelineCardDto, final RequestManager.OnInsertTimeline onInsertTimeline) {
        try {
            if (!checkSocket())
                return;
            Gson gson = new Gson();
            String json = gson.toJson(timelineCardDto);
            JSONObject object = new JSONObject(json);
            Log.d(TAG, "updateTimeline Object = " + object);
            socket.emit(Global.UPDATE_TIMELINE, object);
            socket.once(Global.UPDATE_TIMELINE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "타임라인 업데이트 응답 resObject = " + resObject);
                        if (code == SocketException.SUCCESS) {
                            JSONObject timelineJson = resObject.getJSONObject(Global.TIMELINE);
                            final TimelineCardDto resTimelineCardDto = new TimelineCardDto(timelineJson);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onInsertTimeline.onSuccess(resTimelineCardDto);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onInsertTimeline.onException();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 24. 내 제품 요청
    public static void getMyProduct(String userId, final RequestManager.OnGetMyProduct onGetMyProduct) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(TransactionEntity.PROPERTY_DONATOR_UUID, userId);
            object.put(TransactionEntity.PROPERTY_RECEIVER_UUID, userId);
            Log.d(TAG, "getMyProduct Object = " + object);
            socket.emit(Global.GET_MY_PRODUCT, object);
            socket.once(Global.GET_MY_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        Log.d(TAG, "내 제품 리스트 응답 resObject = " + resObject);
                        if (code == SocketException.SUCCESS) {
                            final ArrayList<ProductCardDto> productCardDtos = new ArrayList<>();
                            JSONArray array = resObject.getJSONArray(Global.PRODUCT);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject productObject = array.getJSONObject(i);
                                ProductCardDto dto = new ProductCardDto(productObject);
                                if (i != 0) {
                                    int size = productCardDtos.size() - 1;
                                    if (productCardDtos.get(size).isSame(dto)) {
                                        productCardDtos.get(size).addFile(dto.fileEntities.get(0));
                                        continue;
                                    }
                                }
                                productCardDtos.add(dto);
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetMyProduct.onSuccess(productCardDtos);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetMyProduct.onException(code);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 24. 댓글 삭제 요청
    public static void deleteComment(TimelineCommentCardDto timelineCommentCardDto, final RequestManager.OnDeleteComment onDeleteComment) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.ID, timelineCommentCardDto.commentEntity.id);
            object.put(Global.TIMELINE_ITEM_ID, timelineCommentCardDto.commentEntity.timeline_item_id);
            object.put(Global.USER_ID, timelineCommentCardDto.commentEntity.user_id);
            Log.d(TAG, "deleteComment Object = " + object);
            socket.emit(Global.DELETE_COMMENT, object);
            socket.once(Global.DELETE_COMMENT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "댓글 삭제 응답 resObject = " + resObject);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == SocketException.SUCCESS)
                                    onDeleteComment.onSuccess();
                                else
                                    onDeleteComment.onException();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void updateTransactionStatus(int status, TransactionEntity transactionEntity, final RequestManager.OnUpdateTransactionStatus onUpdateTransactionStatus) {
        try {
            if (!checkSocket())
                return;
            Gson gson = new Gson();
            String json = gson.toJson(transactionEntity, TransactionEntity.class);
            JSONObject transJson = new JSONObject(json);

            JSONObject object = new JSONObject();
            object.put(Global.STATUS, status);
            object.put(Global.TRANSACTION, transJson);
            Log.d(TAG, "updateTransactionStatus Object = " + object);
            socket.emit(Global.UPDATE_TRANSACTION_STATUS, object);
            socket.once(Global.UPDATE_TRANSACTION_STATUS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "트랜잭션 업데이트 응답 = " + resObject);

                        if (code == SocketException.SUCCESS) {
                            // 성공
                            JSONObject transactionJson = resObject.getJSONObject(Global.TRANSACTION);
                            final TransactionEntity transactionEntity = new TransactionEntity(transactionJson);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onUpdateTransactionStatus.onSuccess(transactionEntity);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onUpdateTransactionStatus.onException();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 제품 삭제
    public static void deleteProduct(String productId, final RequestManager.OnDeleteProduct onDeleteProduct) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.PRODUCT_ID, productId);
            Log.d(TAG, "deleteProduct Object = " + object);
            socket.emit(Global.DELETE_PRODUCT, object);
            socket.once(Global.DELETE_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "제품 삭제 응답 resObject = " + resObject);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == SocketException.SUCCESS) {
                                    onDeleteProduct.onSuccess();
                                } else {
                                    onDeleteProduct.onException();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 제품 수정
    public static void updateProduct(ProductCardDto productCardDto, boolean isDeleteFile, final RequestManager.OnUpdateProduct onUpdateProduct) {
        try {
            if (!checkSocket())
                return;
            Gson gson = new Gson();
            String json = gson.toJson(productCardDto);
            JSONObject object = new JSONObject(json);
            object.put("isDeleteFile", isDeleteFile);
            Log.d(TAG, "updateProduct Object = " + object);
            socket.emit(Global.UPDATE_PRODUCT, object);
            socket.once(Global.UPDATE_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "제품 수정 응답 resObject = " + resObject);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == SocketException.SUCCESS) {
                                    try {
                                        JSONObject productJson = resObject.getJSONObject(Global.PRODUCT);
                                        ProductEntity productEntity = new ProductEntity(productJson);
                                        onUpdateProduct.onSuccess(productEntity);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    onUpdateProduct.onException();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 파일 입력
    public static void insertFile(String id, String path, int position, RequestManager.OnInsertFile onInsertFile) {
//        try {
            String serverUrl = SERVER_URL + "/api/photo";

            Log.d(TAG, "productId = " + id);
            Log.d(TAG, "path = " + path);

            upload(serverUrl, path, id, position, onInsertFile);

//            object.put(Global.PRODUCT_ID, id);
//            object.put(Global.PATH, path);
//            object.put(Global.FILE, fileName);
//            Log.d(TAG, "insertFile Object = " + object);
//            socket.emit(Global.INSERT_FILE, object);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }


    private static void upload(final String serverUrl, final String fileUrl, final String id, final int position, final RequestManager.OnInsertFile onInsertFile) {
        Log.d(TAG, "fileUrl = " + fileUrl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                DataOutputStream dos = null;

                final String LINE_END = "\r\n";
                final String TWO_HYPHENS = "--";
                final String BOUNDARY = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                final int MAX_BUFFER_SIZE = 1024 * 1024;
                File file = new File(fileUrl);

                if (!file.isFile()) {
                    Log.e(TAG, "파일아님 = " + fileUrl);
                    onInsertFile.onException(1000);
                    return;
                }

                try {
                    Log.d(TAG, "파일은 맞음");
                    FileInputStream fis = new FileInputStream(file);
                    URL url = new URL(serverUrl);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
                    conn.setRequestProperty("uploaded_file", fileUrl);
                    conn.setRequestProperty("parent_id", id);

                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileUrl + "\"" + LINE_END);
                    dos.writeBytes(LINE_END);

                    bytesAvailable = fis.available();

                    bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
                    buffer = new byte[bufferSize];

                    bytesRead = fis.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fis.available();
                        bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
                        bytesRead = fis.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(LINE_END);
                    dos.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);
                    Log.d(TAG, "연결");
                    final int serverResCode = conn.getResponseCode();
                    String serverResMsg = conn.getResponseMessage();

                    if (serverResCode == 200) {
                        Log.d(TAG, "서버 메세지 = " + serverResMsg);
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        final StringBuilder sb = new StringBuilder();
                        String str = null;
                        while ((str = bufferedReader.readLine()) != null) {
                            sb.append(str);
                        }
                        serverResMsg = sb.toString();
                        Log.d(TAG, "서버 메세지2 = " + serverResMsg);
                        bufferedReader.close();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onInsertFile.onSuccess(position);
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onInsertFile.onException(serverResCode);
                            }
                        });
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    // TODO: 15. 11. 25. 타임라인 지우기
    public static void deleteTimeline(TimelineCardDto timelineCardDto, final RequestManager.OnDeleteTimeline onDeleteTimeline) {
        if (!checkSocket())
            return;
        String timelineId = timelineCardDto.timelineEntity.id;
        String userId = timelineCardDto.userEntity.id;

        try {
            JSONObject object = new JSONObject();
            object.put(Global.TIMELINE_ITEM_ID, timelineId);
            object.put(Global.USER_ID, userId);
            Log.d(TAG, "deleteTimeLine Object = " + object);
            socket.emit(Global.DELETE_TIMELINE, object);
            socket.once(Global.DELETE_TIMELINE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "타임라인 삭제 응답 resObject = " + resObject);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == SocketException.SUCCESS)
                                    onDeleteTimeline.onSuccess();
                                else
                                    onDeleteTimeline.onException(code);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 좋아요 지우기
    public static void deleteLike(LikeEntity likeEntity, final RequestManager.OnDeleteLike onDeleteLike) {
        try {
            if (!checkSocket())
                return;
            Gson gson = new Gson();
            String json = gson.toJson(likeEntity);
            JSONObject object = new JSONObject(json);
            Log.d(TAG, "deleteLike Object = " + object);
            socket.emit(Global.DELETE_LIKE, object);
            socket.once(Global.DELETE_LIKE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "좋아요 삭제 응답 resObject = " + resObject);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (code == SocketException.SUCCESS)
                                    onDeleteLike.onSuccess();
                                else
                                    onDeleteLike.onException(code);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 좋아요
    public static void insertLike(String timelineItemId, String userId, final RequestManager.OnInsertLike onInsertLike) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.TIMELINE_ITEM_ID, timelineItemId);
            object.put(Global.USER_ID, userId);
            Log.d(TAG, "insertLike Object = " + object);
            socket.emit(Global.INSERT_LIKE, object);
            socket.once(Global.INSERT_LIKE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "좋아요 입력 응답 resObject = " + resObject);

                        if (code == SocketException.SUCCESS) {
                            JSONObject likeObject = resObject.getJSONObject(Global.LIKE);
                            final LikeEntity likeEntity = new LikeEntity(likeObject);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onInsertLike.onSuccess(likeEntity);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onInsertLike.onException(code);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 25. 제품 요청
    public static void getProduct(String productJson, final RequestManager.OnGetProduct onGetProduct) {
        try {
            if (!checkSocket())
                return;
            JSONArray array = new JSONArray(productJson);
            Log.d(TAG, "getProduct Array = " + array.toString());
            socket.emit(Global.GET_PRODUCT, array);
            socket.once(Global.GET_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        if (code == SocketException.SUCCESS) {
                            JSONArray productArray = resObject.getJSONArray(Global.PRODUCT);
                            Gson gson = new Gson();
                            final ArrayList<ProductCardDto> productCardDtos = new ArrayList<>();

                            for (int i = 0; i < productArray.length(); i++) {
                                JSONObject productObject = productArray.getJSONObject(i);
                                ProductCardDto dto = gson.fromJson(productObject.toString(), ProductCardDto.class);
                                productCardDtos.add(dto);
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetProduct.onSuccess(productCardDtos);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetProduct.onException();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 28.
    public static void insertTransaction(TransactionEntity transactionEntity) {
        try {
            if (!checkSocket())
                return;
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(gson.toJson(transactionEntity));
            Log.d(TAG, "insertTransaction object = " + jsonObject);
            socket.emit(Global.INSERT_TRANSACTION, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 28. 카카오 로그인
    public static void signInKakao(long id, String nickName, String profileImage, String thumbnailImage, final RequestManager.OnSignInKakao onSignInKakao) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.ID, String.valueOf(id));
            object.put(Global.NICK_NAME, nickName);
            object.put(Global.PROFILE_IMAGE, profileImage);
            object.put(Global.THUMBNAIL_IMAGE, thumbnailImage);
            Log.d(TAG, "signInKakao Object = " + object);
            socket.emit(Global.SIGN_IN_KAKAO, object);
            socket.once(Global.SIGN_IN_KAKAO, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "카카오 로그인 응답 resObject = " + resObject);

                        if (code == SocketException.SUCCESS) {
                            // 성공
                            JSONObject userObject = resObject.getJSONObject(Global.USER);
                            final UserEntity user = new UserEntity(userObject);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "myId = " + user.getId());
                                    Log.d(TAG, "myKakaoId = " + user.getKakaotalk().id);
                                    onSignInKakao.onSuccess(user);
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSignInKakao.onException(code);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void transactionPush(ArrayList<String> userIds, String msg) {
        try {
            if (!checkSocket())
                return;
            Gson gson = new Gson();
            String userJson = gson.toJson(userIds);
            JSONArray userArray = new JSONArray(userJson);
            JSONObject object = new JSONObject();
            object.put(Global.USER, userArray);
            object.put(Global.MESSAGE, msg);
            socket.emit(Global.TRANSACTION_PUSH, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public static void deleteUser(String userId, Emitter.Listener listener) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.USER_ID, userId);
            socket.emit(Global.DELETE_USER, object);
            socket.once(Global.DELETE_USER, listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
