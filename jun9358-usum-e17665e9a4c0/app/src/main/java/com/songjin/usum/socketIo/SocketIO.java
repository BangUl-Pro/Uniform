package com.songjin.usum.socketIo;

import android.content.Context;
import android.net.Uri;
import android.os.CountDownTimer;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ironFactory on 2015-08-03.
 */
public class SocketIO {

    private static Handler handler = new Handler();
    private static final String SERVER_URL = "http://uniform-donation.herokuapp.com";
//    private static final String SERVER_URL = "10.0.2.2";
    private static final String TAG = "SocketIO";

    public static Socket socket;
    private Context context;

    public SocketIO(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        try {
            socket = IO.socket(SERVER_URL);
        } catch (Exception e) {
        }

        if (socket != null) {
            socketConnect();
        }

        if (!Global.isCreated)
            setListener();
        Global.isCreated = true;

//        socket.emit("dropTransaction", "");
//        socket.emit("dropProduct", "");
//        socket.emit("dropComment", "");
//        socket.emit("dropFile", "");
//        socket.emit("dropLike", "");
//        socket.emit("dropTimeline", "");
//
//        socket.emit("createTransaction", "");
//        socket.emit("createProduct", "");
//        socket.emit("createComment", "");
//        socket.emit("createFile", "");
//        socket.emit("createLike", "");
//        socket.emit("createTimeline", "");
//        socket.emit("resetSchoolRank", "");
    }


    private void setListener() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 연결
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                // 연결 끊김
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

        String userId = userEntity.id;
        String realName = userEntity.realName;
        int sex = userEntity.sex;
        int userType = userEntity.userType;
        String phone = userEntity.phone;
        int schoolId = userEntity.schoolId;


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
        if (!checkSocket())
            return;
        socket.emit(Global.GET_SCHOOL, "");
        socket.once(Global.GET_SCHOOL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject resObject = getJson(args);
                    final int code = getCode(resObject);

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
                                            isTarget = true;
                                            isCategory = false;
                                        } else if (isText) {
                                            isTarget = false;
                                            isCategory = false;
                                        }
                                    } else if (isAddress) {
                                        // 주소
                                        if (isTarget && isText) {
                                            schoolEntity = setAddress(schoolEntity, text);
                                            isAddress = false;
                                        }
                                    } else if (isName) {
                                        if (isTarget && isText) {
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
                        socket.emit("insertSchool", array);
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

        return schoolEntity;
    }


    private static boolean checkSocket() {
        if (socket == null)
            return false;
        return true;
    }

    public static void setDeviceId(String id, String deviceId, final RequestManager.OnSetDeviceId onSetDeviceId) {
        if (!checkSocket())
            return;
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, id);
            object.put(Global.DEVICE_ID, deviceId);
            socket.emit(Global.SET_DEVICE_ID, object);
            socket.once(Global.SET_DEVICE_ID, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

                        if (code == SocketException.SUCCESS) {
                            // 성공
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSetDeviceId.onSuccess();
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSetDeviceId.onException();
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

    public static void setHasExtraProfile(String id, boolean hasExtraProfile, final RequestManager.OnSetHasExtraProfile sender) {
        if (!checkSocket())
            return;
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, id);
            object.put(Global.HAS_EXTRA_PROFILE, hasExtraProfile);
            socket.emit(Global.SET_HAS_EXTRA_PROFILE, object);
            socket.once(Global.SET_HAS_EXTRA_PROFILE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

                        if (code == SocketException.SUCCESS) {
                            // 성공
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sender.onSuccess();
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    sender.onException();
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

    public static void setToken(String id, String token, final RequestManager.OnSetToken onSetToken) {
        if (!checkSocket())
            return;
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, id);
            object.put(Global.TOKEN, token);
            socket.emit(Global.SET_TOKEN, object);
            socket.once(Global.SET_TOKEN, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

                        if (code == SocketException.SUCCESS) {
                            // 성공
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSetToken.onSuccess();
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSetToken.onException();
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

    public static void sendGcm(String id, String msg, final RequestManager.OnSetToken onSetToken) {
        if (!checkSocket())
            return;
        try {
            JSONObject object = new JSONObject();
            object.put(Global.ID, id);
            object.put(Global.MSG, msg);
            socket.emit(Global.SEND_GCM, object);
            socket.once(Global.SEND_GCM, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

                        if (code == SocketException.SUCCESS) {
                            // 성공
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSetToken.onSuccess();
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onSetToken.onException();
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


    // TODO: 15. 11. 20. 학교 랭킹 요청
    public static void getSchoolRanking(final RequestManager.OnGetSchoolRanking onGetSchoolRanking) {
        if (!checkSocket())
            return;
        socket.emit(Global.GET_SCHOOL_RANKING, "");
        socket.once(Global.GET_SCHOOL_RANKING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject resObject = getJson(args);
                    final int code = getCode(resObject);

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

    // TODO: 15. 11. 20. 학교 랭킹 요청
    public static void getSchoolRanking(final RequestManager.OnGetSchoolRanking onGetSchoolRanking, int schoolId) {
        if (!checkSocket())
            return;
        try {
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            socket.emit(Global.GET_SCHOOL_RANKING, object);
            socket.once(Global.GET_SCHOOL_RANKING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // TODO: 15. 11. 20. 학교 랭킹 요청
    public static void getMySchoolRanking(final RequestManager.OnGetMySchoolRanking onGetSchoolRanking, int schoolId) {
        if (!checkSocket())
            return;
        try {
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            socket.emit(Global.GET_MY_SCHOOL_RANKING, object);
            socket.once(Global.GET_MY_SCHOOL_RANKING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

                        if (code == SocketException.SUCCESS) {
                            // 성공
                            JSONObject rankJson = resObject.getJSONObject(Global.RANK);
                            final int rank = rankJson.getInt(Global.RANK);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetSchoolRanking.onSuccess(rank);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // TODO: 15. 11. 20. 제품검색
    public static void searchProduct(int schoolId, int sex, int category, int size, int position, final RequestManager.OnSearchProduct onSearchProduct) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.SEX, sex);
            object.put(Global.CATEGORY, category);
            object.put(Global.SIZE, size);
            object.put(Global.POSITION, position);
            socket.emit(Global.SEARCH_PRODUCT, object);
            socket.once(Global.SEARCH_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject reqObject = getJson(args);
                        int code = getCode(reqObject);
                        if (code == SocketException.SUCCESS) {
                            final ArrayList<ProductCardDto> products = new ArrayList<>();
                            final Map<String, ProductCardDto> productMap = new HashMap<String, ProductCardDto>();


                            JSONArray array = reqObject.getJSONArray(Global.PRODUCT);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject productJson = array.getJSONObject(i);
                                ProductCardDto dto = new ProductCardDto(productJson);

                                if (productMap.get(dto.productEntity.id) == null) {
                                    productMap.put(dto.productEntity.id, dto);
                                } else {
                                    productMap.put(dto.productEntity.id, productMap.get(dto.productEntity.id).addFile(dto.fileEntities.get(0)));
                                }
                            }
                            Iterator<String> iterator = productMap.keySet().iterator();
                            while (iterator.hasNext())
                                products.add(productMap.get(iterator.next()));

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
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
            socket.emit(Global.INSERT_PRODUCT, object);
            socket.once(Global.INSERT_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject reqObject = (JSONObject) args[0];
                        int code = reqObject.getInt(Global.CODE);

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
            socket.emit(Global.UPDATE_USER_PROFILE, object);
            socket.once(Global.UPDATE_USER_PROFILE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
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
    public static void insertTimelineComment(String timelineItemId, String commentContent, String userId, int type, final RequestManager.OnInsertTimelineComment onInsertTimelineComment) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.TIMELINE_ITEM_ID, timelineItemId);
            object.put(Global.COMMENT_CONTENT, commentContent);
            object.put(Global.USER_ID, userId);
            object.put(Global.TYPE, type);
            socket.emit(Global.INSERT_TIMELINE_COMMENT, object);
            socket.once(Global.INSERT_TIMELINE_COMMENT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
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
    public static void getTimelineComment(String timelineId, int type, final RequestManager.OnGetTimelineComment onGetTimelineComment) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.ID, timelineId);
            object.put(Global.TYPE, type);
            socket.emit(Global.GET_TIMELINE_COMMENT, object);
            socket.once(Global.GET_TIMELINE_COMMENT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

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
    public static void getAllTimeline(int schoolId, String userId, long time, final RequestManager.OnGetAllTimeline onGetAllTimeline) {
        try {
            if (!checkSocket())
                return;
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.USER_ID, userId);
            object.put(Global.TIME, time);
            socket.emit(Global.GET_ALL_TIMELINE, object);
            socket.once(Global.GET_ALL_TIMELINE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        if (code == SocketException.SUCCESS) {
                            // 성공
                            final ArrayList<TimelineCardDto> timelineCardDtos = new ArrayList<>();
                            final Map<String, TimelineCardDto> timelineMap = new HashMap<String, TimelineCardDto>();
                            JSONArray timelineArray = resObject.getJSONArray(Global.TIMELINE);
                            for (int i = 0; i < timelineArray.length(); i++) {
                                JSONObject timelineObject = timelineArray.getJSONObject(i);
                                TimelineCardDto timelineCardDto = new TimelineCardDto();
                                timelineCardDto.setTimeline(timelineObject);
                                timelineCardDto.setUser(timelineObject);
                                timelineCardDto.setLike(timelineObject);
                                timelineCardDto.setFile(timelineObject);


                                if (timelineMap.get(timelineCardDto.timelineEntity.id) == null)
                                    timelineMap.put(timelineCardDto.timelineEntity.id, timelineCardDto);
                                else
                                    timelineMap.put(timelineCardDto.timelineEntity.id, timelineMap.get(timelineCardDto.timelineEntity.id).addFile(timelineCardDto.fileEntities.get(0)));
                            }

                            Iterator<String> iterator = timelineMap.keySet().iterator();
                            while (iterator.hasNext())
                                timelineCardDtos.add(timelineMap.get(iterator.next()));

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
            socket.emit(Global.GET_MY_TIMELINE, object);
            socket.once(Global.GET_MY_TIMELINE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        if (code == SocketException.SUCCESS) {
                            // 성공
                            final ArrayList<TimelineCardDto> timelineCardDtos = new ArrayList<>();
                            final Map<String, TimelineCardDto> timelineMap = new HashMap<String, TimelineCardDto>();

                            JSONArray timelineArray = resObject.getJSONArray(Global.TIMELINE);
                            for (int i = 0; i < timelineArray.length(); i++) {
                                JSONObject timelineObject = timelineArray.getJSONObject(i);
                                TimelineCardDto timelineCardDto = new TimelineCardDto();
                                timelineCardDto.setTimeline(timelineObject);
                                timelineCardDto.setUser(timelineObject);
                                timelineCardDto.setLike(timelineObject);
                                timelineCardDto.setFile(timelineObject);


                                if (timelineMap.get(timelineCardDto.timelineEntity.id) == null)
                                    timelineMap.put(timelineCardDto.timelineEntity.id, timelineCardDto);
                                else
                                    timelineMap.put(timelineCardDto.timelineEntity.id, timelineMap.get(timelineCardDto.timelineEntity.id).addFile(timelineCardDto.fileEntities.get(0)));
                            }

                            Iterator<String> iterator = timelineMap.keySet().iterator();
                            while (iterator.hasNext())
                                timelineCardDtos.add(timelineMap.get(iterator.next()));

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
            Gson gson = new Gson();
            JSONArray array = new JSONArray(gson.toJson(files));
            JSONObject object = new JSONObject();
            object.put(Global.SCHOOL_ID, schoolId);
            object.put(Global.USER_ID, id);
            object.put(Global.TIMELINE_CONTENT, timelineContent);
            object.put(Global.FILE, array);
            socket.emit(Global.INSERT_TIMELINE, object);
            socket.once(Global.INSERT_TIMELINE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
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
            socket.emit(Global.DELETE_FILE, object);
            socket.once(Global.DELETE_FILE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
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
            socket.emit(Global.UPDATE_TIMELINE, object);
            socket.once(Global.UPDATE_TIMELINE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
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
            socket.emit(Global.GET_MY_PRODUCT, object);
            socket.once(Global.GET_MY_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = (JSONObject) args[0];
                        final int code = resObject.getInt(Global.CODE);
                        if (code == SocketException.SUCCESS) {
                            final ArrayList<ProductCardDto> productCardDtos = new ArrayList<>();
                            JSONArray array = resObject.getJSONArray(Global.PRODUCT);

                            final ArrayList<ProductCardDto> products = new ArrayList<>();
                            final Map<String, ProductCardDto> productMap = new HashMap<String, ProductCardDto>();


                            for (int i = 0; i < array.length(); i++) {
                                JSONObject productJson = array.getJSONObject(i);
                                ProductCardDto dto = new ProductCardDto(productJson);

                                if (productMap.get(dto.productEntity.id) == null)
                                    productMap.put(dto.productEntity.id, dto);
                                else
                                    productMap.put(dto.productEntity.id, productMap.get(dto.productEntity.id).addFile(dto.fileEntities.get(0)));
                            }
                            Iterator<String> iterator = productMap.keySet().iterator();
                            while (iterator.hasNext())
                                products.add(productMap.get(iterator.next()));

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onGetMyProduct.onSuccess(products);
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
            socket.emit(Global.DELETE_COMMENT, object);
            socket.once(Global.DELETE_COMMENT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
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
            socket.emit(Global.UPDATE_TRANSACTION_STATUS, object);
            socket.once(Global.UPDATE_TRANSACTION_STATUS, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

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
            socket.emit(Global.DELETE_PRODUCT, object);
            socket.once(Global.DELETE_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
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
            socket.emit(Global.UPDATE_PRODUCT, object);
            socket.once(Global.UPDATE_PRODUCT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        final JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
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
    public static void insertFile(String id, String path, int position, int type, RequestManager.OnInsertFile onInsertFile) {
            String serverUrl = SERVER_URL + "/api/photo";

            upload(serverUrl, path, id, position, type, onInsertFile);
    }


    private static void upload(final String serverUrl, final String fileUrl, final String id, final int position, final int type, final RequestManager.OnInsertFile onInsertFile) {
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
                    onInsertFile.onException(1000);
                    return;
                }

                try {
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
                    conn.setRequestProperty("file_type", String.valueOf(type));

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
                    final int serverResCode = conn.getResponseCode();
                    String serverResMsg = conn.getResponseMessage();

                    if (serverResCode == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        final StringBuilder sb = new StringBuilder();
                        String str = null;
                        while ((str = bufferedReader.readLine()) != null) {
                            sb.append(str);
                        }
                        serverResMsg = sb.toString();
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
            socket.emit(Global.DELETE_TIMELINE, object);
            socket.once(Global.DELETE_TIMELINE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

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
            socket.emit(Global.DELETE_LIKE, object);
            socket.once(Global.DELETE_LIKE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
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
            socket.emit(Global.INSERT_LIKE, object);
            socket.once(Global.INSERT_LIKE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);

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
    public static void getProduct(JSONObject object, final RequestManager.OnGetProduct onGetProduct) {
        if (!checkSocket())
            return;
        socket.emit(Global.GET_PRODUCT, object);
        socket.once(Global.GET_PRODUCT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject resObject = getJson(args);
                    final int code = getCode(resObject);
                    if (code == SocketException.SUCCESS) {
                        final ArrayList<ProductCardDto> products = new ArrayList<>();
                        final Map<String, ProductCardDto> productMap = new HashMap<String, ProductCardDto>();


                        JSONArray array = resObject.getJSONArray(Global.PRODUCT);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject productJson = array.getJSONObject(i);
                            ProductCardDto dto = new ProductCardDto(productJson);

                            if (productMap.get(dto.productEntity.id) == null) {
                                productMap.put(dto.productEntity.id, dto);
                            } else {
                                productMap.put(dto.productEntity.id, productMap.get(dto.productEntity.id).addFile(dto.fileEntities.get(0)));
                            }
                        }
                        Iterator<String> iterator = productMap.keySet().iterator();
                        while (iterator.hasNext())
                            products.add(productMap.get(iterator.next()));

                        // 정렬
                        for (int i = 0; i < products.size(); i++) {
                            for (int j = 1; j < products.size() - i; j++) {
                                if (products.get(j - 1).productEntity.created < products.get(j).productEntity.created) {
                                    ProductCardDto curDto = products.get(j - 1);
                                    products.set(j - 1, products.get(j));
                                    products.set(j, curDto);
                                }
                            }
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onGetProduct.onSuccess(products);
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
    }


    // TODO: 15. 11. 28.
    public static void insertTransaction(TransactionEntity transactionEntity) {
        try {
            if (!checkSocket())
                return;
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(gson.toJson(transactionEntity));
            socket.emit(Global.INSERT_TRANSACTION, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // TODO: 15. 11. 28. 카카오 로그인
    public static void signInKakao(final long id, final String nickName, final String profileImage, final String thumbnailImage, final RequestManager.OnSignInKakao onSignInKakao) {
        try {
            if (!checkSocket())
                return;
            final CountDownTimer timer = new CountDownTimer(5000, 5000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    socket.off(Global.SIGN_IN_KAKAO);
                    signInKakao(id, nickName, profileImage, thumbnailImage, onSignInKakao);
                }
            }.start();

            Log.d(TAG, "signInKAkao");
            JSONObject object = new JSONObject();
            object.put(Global.ID, String.valueOf(id));
            object.put(Global.NICK_NAME, nickName);
            object.put(Global.PROFILE_IMAGE, profileImage);
            object.put(Global.THUMBNAIL_IMAGE, thumbnailImage);
            socket.emit(Global.SIGN_IN_KAKAO, object);
            socket.once(Global.SIGN_IN_KAKAO, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject resObject = getJson(args);
                        final int code = getCode(resObject);
                        Log.d(TAG, "signInKakao = " + code);

                        if (code == SocketException.SUCCESS) {
                            // 성공
                            timer.cancel();
                            JSONObject userObject = resObject.getJSONObject(Global.USER);
                            final UserEntity user = new UserEntity(userObject);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
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
