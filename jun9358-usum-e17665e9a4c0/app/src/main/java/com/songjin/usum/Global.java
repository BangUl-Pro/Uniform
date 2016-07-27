package com.songjin.usum;

import com.songjin.usum.entities.UserEntity;

/**
 * Created by IronFactory on 15. 10. 26..
 */
public class Global {
    public static final String APP_NAME = "uniform";
    public static final String PACKAGE_NAME = "com.jerryjang.donation";

    public static final String COMMAND = "command";
    public static final String CODE = "code";
    public static final String GET_SCHOOL = "getSchool";
    public static final String GET_SCHOOL_RANKING = "getSchoolRanking";
    public static final String GET_MY_SCHOOL_RANKING = "getMySchoolRanking";
    public static final String SIGN_UP = "signUp";
    public static final String SIGN_IN = "signIn";
    public static final String SIGN_IN_KAKAO = "signInKakao";
    public static final String GET_TIMELINE_COMMENT = "getTimelineComment";
    public static final String GET_PRODUCT = "getProduct";
    public static final String GET_ALL_TIMELINE = "getAllTimeline";
    public static final String GET_MY_TIMELINE = "getMyTimeline";
    public static final String GET_MY_PRODUCT = "getMyProduct";
    public static final String SEARCH_PRODUCT = "searchProduct";
    public static final String UPDATE_USER_PROFILE = "updateUserProfile";
    public static final String UPDATE_TRANSACTION_STATUS = "updateTransactionStatus";
    public static final String UPDATE_TIMELINE = "updateTimeline";
    public static final String UPDATE_PRODUCT = "updateProduct";
    public static final String DELETE_FILE = "deleteFile";
    public static final String DELETE_COMMENT = "deleteComment";
    public static final String DELETE_PRODUCT = "deleteProduct";
    public static final String DELETE_TIMELINE = "deleteTimeline";
    public static final String DELETE_LIKE = "deleteLike";
    public static final String INSERT_PRODUCT = "insertProduct";
    public static final String INSERT_TIMELINE = "insertTimeline";
    public static final String INSERT_LIKE = "insertLike";
    public static final String INSERT_TRANSACTION = "insertTransaction";
    public static final String INSERT_TIMELINE_COMMENT = "insertTimelineComment";
    public static final String TRANSACTION_PUSH = "transactionPush";
    public static final String DELETE_USER = "deleteUser";
    public static final String SET_DEVICE_ID = "setDeviceId";
    public static final String SET_TOKEN = "setToken";
    public static final String SEND_GCM = "sendGcm";

    public static final String ID = "id";
    public static final String MSG = "msg";
    public static final String DEVICE_ID = "deviceId";
    public static final String HAS_EXTRA_PROFILE = "hasExtraProfile";
    public static final String SET_HAS_EXTRA_PROFILE = "setHasExtraProfile";
    public static final String USER = "user";
    public static final String SCHOOL = "school";
    public static final String USER_ID = "user_id";
    public static final String TIME = "time";
    public static final String REAL_NAME = "realName";
    public static final String SEX = "sex";
    public static final String CATEGORY = "category";
    public static final String POSITION = "position";
    public static final String SIZE = "size";
    public static final String USER_TYPE = "userType";
    public static final String PHONE = "phone";
    public static final String SCHOOL_ID = "school_id";
    public static final String RANK = "rank";
    public static final String POINT = "point";
    public static final String TIMELINE = "timeline";
    public static final String TIMELINE_COMMENT = "timelineComment";
    public static final String FILE = "file";
    public static final String TIMELINE_ITEM_ID = "timelineItemId";
    public static final String COMMENT_CONTENT = "commentContent";
    public static final String TIMELINE_CONTENT = "timelineContent";
    public static final String PRODUCT_ID = "productId";
    public static final String TRANSACTION = "transaction";
    public static final String STATUS = "status";
    public static final String PRODUCT = "product";
    public static final String LIKE = "like";
    public static final String TOKEN = "token";
    public static final String MESSAGE = "message";
    public static final String NICK_NAME = "nickName";
    public static final String PROFILE_IMAGE = "profileImage";
    public static final String THUMBNAIL_IMAGE = "thumbnailImage";


    public static boolean isCreated = false;

    // sex
    public static final int MAN = 1;
    public static final int WOMAN = 2;


    // user_type
    public static final int GUEST = 1;
    public static final int STUDENT = 2;
    public static final int PARENT = 3;


    // transaction
    public static final int REGISTERED = 1;
    public static final int REQUESTED = 2;
    public static final int SENDED = 3;
    public static final int RECEIVED = 4;

    public static UserEntity userEntity;
}
