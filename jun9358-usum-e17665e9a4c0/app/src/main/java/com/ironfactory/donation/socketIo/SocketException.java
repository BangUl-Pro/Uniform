package com.ironfactory.donation.socketIo;

import android.util.Log;

/**
 * Created by IronFactory on 15. 10. 26..
 */
public class SocketException extends Exception {

    private static final String TAG = "SocketException";

    // 성공
    public static final int SUCCESS = 200;

    // 학교 정보 로드 에러
    public static final int ERR_LOAD_SCHOOL = 400;

    public static void printErrMsg(int code) {
        Log.d(TAG, "code = " + code);

        switch (code) {
            case SUCCESS:
                Log.d(TAG, "성공했습니다.");
                break;

            case ERR_LOAD_SCHOOL:
                Log.e(TAG, "데이터 로드 실패");
                break;

            case 411:
                Log.e(TAG, "서버 값 누락");
                break;

            case 412:
            case 413:
                Log.e(TAG, "DB insert 에러");
                break;

            default:

                break;
        }
    }


    public static void toastErrMsg(int code) {
        switch (code) {
            case SUCCESS:
                Log.d(TAG, "성공했습니다.");
                break;

            default:

                break;
        }
    }
}
