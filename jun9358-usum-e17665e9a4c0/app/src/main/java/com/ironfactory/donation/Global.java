package com.ironfactory.donation;

import com.ironfactory.donation.entities.LikeEntity;
import com.ironfactory.donation.entities.UserEntity;

/**
 * Created by IronFactory on 15. 10. 26..
 */
public class Global {
    public static final String COMMAND = "command";
    public static final String CODE = "code";
    public static final String GET_SCHOOL = "getSchool";
    public static final String GET_SCHOOL_RANKING = "getSchoolRanking";
    public static final String LOGIN = "login";
    public static final String SIGN_UP = "signUp";
    public static final String SIGN_IN = "signIn";
    public static final String SIGN_IN_KAKAO = "signInKakao";
    public static final String INSERT_TIMELINE_COMMENT = "insertTimelineComment";
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
    public static final String INSERT_FILE = "insertFile";
    public static final String INSERT_LIKE = "insertLike";
    public static final String INSERT_TRANSACTION = "insertTransaction";
    public static final String CREATE = "create";

    public static final String ID = "id";
    public static final String USER = "user";
    public static final String SCHOOL = "school";
    public static final String USER_ID = "user_id";
    public static final String REAL_NAME = "realName";
    public static final String NAME = "name";
    public static final String HAS_EXTRA_PROFILE = "has_extra_profile";
    public static final String SEX = "sex";
    public static final String CATEGORY = "category";
    public static final String SIZE = "size";
    public static final String USER_TYPE = "userType";
    public static final String PICTURE = "picture";
    public static final String PHONE = "phone";
    public static final String SCHOOL_ID = "school_id";
    public static final String PRODUCT_CARD = "productCard";
    public static final String TIMELINE = "timeline";
    public static final String TIMELINE_COMMENT = "timelineComment";
    public static final String FROM = "from";
    public static final String FILE = "file";
    public static final String TIMELINE_ITEM_ID = "timelineItemId";
    public static final String COMMENT_CONTENT = "commentContent";
    public static final String TIMELINE_CONTENT = "timelineContent";
    public static final String PARENT_ID = "parentId";
    public static final String PRODUCT_ID = "productId";
    public static final String TRANSACTION = "transaction";
    public static final String STATUS = "status";
    public static final String COMMENT = "comment";
    public static final String PRODUCT = "product";
    public static final String PATH = "path";
    public static final String LIKE = "like";
    public static final String TOKEN = "token";
    public static final String CREATED = "created";
    public static final String CONTENT = "content";

    // sex
    public static final int MAN = 1;
    public static final int WOMAN = 2;


    // user_type
    public static final int GUEST = 1;
    public static final int STUDENT = 2;
    public static final int PARENT = 3;


    public static OnDeleted onDeleted;
    public static OnInsertFile onInsertFile;
    public static OnDeleteTimeline onDeleteTimeline;
    public static OnDeleteLike onDeleteLike;
    public static OnInsertLike onInsertLike;

    public interface OnDeleted {
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
        void onSuccess();
        void onException(int code);
    }

    public static UserEntity userEntity;
}
