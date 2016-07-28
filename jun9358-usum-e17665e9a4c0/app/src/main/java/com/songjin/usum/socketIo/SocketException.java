package com.songjin.usum.socketIo;

/**
 * Created by IronFactory on 15. 10. 26..
 */
public class SocketException extends Exception {

    private static final String TAG = "SocketException";

    // 성공
    public static final int SUCCESS = 200;

    // 학교 정보 로드 에러
    public static final int ERR_LOAD_SCHOOL = 400;
}
