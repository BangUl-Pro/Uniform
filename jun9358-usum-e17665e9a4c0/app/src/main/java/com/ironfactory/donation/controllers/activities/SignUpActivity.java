package com.ironfactory.donation.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.views.UserProfileForm;
import com.ironfactory.donation.entities.UserEntity;
import com.ironfactory.donation.managers.RequestManager;
import com.ironfactory.donation.socketIo.SocketException;
import com.ironfactory.donation.socketIo.SocketIO;
import com.ironfactory.donation.socketIo.SocketService;
import com.kakao.APIErrorResult;
import com.kakao.MeResponseCallback;
import com.kakao.PushRegisterHttpResponseHandler;
import com.kakao.PushService;
import com.kakao.PushTokenInfo;
import com.kakao.PushTokensHttpResponseHandler;
import com.kakao.Session;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.helper.SharedPreferencesCache;
import com.kakao.widget.PushActivity;

import java.util.Arrays;


public class SignUpActivity extends PushActivity {
    private static final String TAG = "SignUpActivity";
    private long id;

    private class ViewHolder {
        public CheckBox termsAgreementCheckBox;
        public UserProfileForm userProfileForm;

        public ViewHolder(View view) {
            termsAgreementCheckBox = (CheckBox) view.findViewById(R.id.terms_agreement);
            userProfileForm = (UserProfileForm) view.findViewById(R.id.user_profile_form);
        }
    }

    private ViewHolder viewHolder;

    @Override
    protected void redirectLoginActivity() {
        Log.d(TAG, "redirect");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected String getDeviceUUID() {
        SharedPreferencesCache cache = Session.getAppCache();
        String curId = cache.getString(Global.ID);
        if (curId == null) {
            Bundle bundle = new Bundle();
            curId = getUniqueId();
            bundle.putString(Global.ID, curId);
            cache.save(bundle);
            getPushToken();
        }
        Log.d(TAG, "getDeviceUUID = " + curId);
        return curId;
    }

    private String getUniqueId() {
        return Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        ) + System.currentTimeMillis();
    }


    private void getPushToken() {
        PushService.getPushTokens(new PushTokensHttpResponseHandler<PushTokenInfo[]>() {
            @Override
            protected void onHttpSuccess(PushTokenInfo[] resultObj) {
                String message = "succeeded to get push tokens."
                        + "\ncount="
                        + resultObj.length
                        + "\nstories="
                        + Arrays.toString(resultObj);
                Log.d(TAG, "푸시알림 토큰 = " + message);
                for (PushTokenInfo pushTokenInfo :
                        resultObj) {
                    String pushToken = pushTokenInfo.getPushToken();
                    String deviceId = pushTokenInfo.getDeviceId();

                    if (!TextUtils.isEmpty(pushToken) && !TextUtils.isEmpty(deviceId) && deviceId.length() > 10) {
                        Log.d(TAG, "푸시토큰 = " + pushToken);
                        Log.d(TAG, "디바이스 아이디 = " + deviceId);
                        Log.d(TAG, "토큰 등록");
                        PushService.registerPushToken(new PushRegisterHttpResponseHandler() {
                            @Override
                            protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                                Log.d(TAG, "GCM PUSH 등록 에러 메세지 = " + errorResult.getErrorMessage());
                                Log.d(TAG, "GCM PUSH 등록 에러 URL = " + errorResult.getRequestURL());
                                Log.d(TAG, "GCM PUSH 등록 에러 코드 = " + errorResult.getErrorCodeInt());
                            }
                        }, pushToken, deviceId);
                        break;
                    }
                }
            }

            @Override
            protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                Log.d(TAG, "GCM PUSH 연결 에러 메세지 = " + errorResult.getErrorMessage());
                Log.d(TAG, "GCM PUSH 연결 에러 URL = " + errorResult.getRequestURL());
                Log.d(TAG, "GCM PUSH 연결 에러 코드 = " + errorResult.getErrorCodeInt());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "액티비티 시작");
        requestCheckConfirmedUser();
    }


    private void initViews(int layoutResID) {
        setContentView(layoutResID);

        // 컨트롤변수 초기화
        viewHolder = new ViewHolder(getWindow().getDecorView());
        viewHolder.userProfileForm.setUserId(id);
        viewHolder.userProfileForm.setOnSubmitListener(new UserProfileForm.OnSubmitListener() {
            @Override
            public void onClick(View v) {
                UserEntity userEntity = viewHolder.userProfileForm.getUserEntity();

                // 약관에 동의 했는지 체크하기
                if (!viewHolder.termsAgreementCheckBox.isChecked()) {
                    new MaterialDialog.Builder(BaseActivity.context)
                            .title(R.string.app_name)
                            .content("약관에 동의해주세요.")
                            .show();
                    return;
                }
                String validateMsg = viewHolder.userProfileForm.validateForm();
                if (!validateMsg.isEmpty()) {
                    new MaterialDialog.Builder(BaseActivity.context)
                            .title(R.string.app_name)
                            .content(validateMsg)
                            .show();
                    return;
                }

                // 회원가입 요청
                showProgress();

                Intent intent = new Intent(getApplicationContext(), SocketService.class);
                intent.putExtra(Global.COMMAND, Global.SIGN_UP);
                intent.putExtra(Global.USER, userEntity);
                startService(intent);

//                RequestManager.requestSignUp(userEntity, new BaasioSignInCallback() {
//                    @Override
//                    public void onResponse(BaasioUser baasioUser) {
//                        BaseActivity.hideLoadingView();
//
//                        BaseActivity.startActivityOnTopStack(MainActivity.class);
//                        finish();
//                    }
//
//                    @Override
//                    public void onException(BaasioException e) {
//                        BaseActivity.hideLoadingView();
//
//                        new MaterialDialog.Builder(BaseActivity.context)
//                                .title(R.string.app_name)
//                                .content("회원가입하는데 문제가 발생하였습니다.")
//                                .show();
//                    }
//                });
            }
        });
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND);
            if (command != null) {
                int code = intent.getIntExtra(Global.CODE, -1);
                if (code != -1) {
                    SocketException.printErrMsg(code);
                    SocketException.toastErrMsg(code);

                    if (command.equals(Global.SIGN_UP)) {
                        // 회원가입 응답
                        processSignUp(code, intent);
                    }
                }
            }
        }
    }


    // TODO 회원가입 응답
    private void processSignUp(int code, Intent intent) {
        hideLoadingView();

        if (code == SocketException.SUCCESS) {
            UserEntity user = intent.getParcelableExtra(Global.USER);
            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
            intent1.putExtra(Global.USER, user);
            Log.d(TAG, "1.userId = " + user.id);
            startActivity(intent1);
//            BaseActivity.startActivityOnTopStack(MainActivity.class);
            finish();
        } else {
            new MaterialDialog.Builder(BaseActivity.context)
                    .title(R.string.app_name)
                    .content("회원가입하는데 문제가 발생하였습니다.")
                    .show();
        }
    }

    // TODO RequestManager에 넣기
    private void requestCheckConfirmedUser() {
        setContentView(R.layout.activity_login);
        SharedPreferencesCache cache = Session.getAppCache();
        String kakaoToken = cache.getString(Global.TOKEN);
        Log.d(TAG, "kakaoToken = " + kakaoToken);

        if (kakaoToken == null) {
            kakaoToken = Session.getCurrentSession().getAccessToken();
            Bundle bundle = new Bundle();
            bundle.putString(Global.TOKEN, kakaoToken);
            cache.save(bundle);

            PushService.registerPushToken(new PushRegisterHttpResponseHandler() {
                @Override
                protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
                    Log.d(TAG, "GCM PUSH 등록 에러 메세지 = " + errorResult.getErrorMessage());
                    Log.d(TAG, "GCM PUSH 등록 에러 URL = " + errorResult.getRequestURL());
                    Log.d(TAG, "GCM PUSH 등록 에러 코드 = " + errorResult.getErrorCodeInt());
                }
            }, kakaoToken, getDeviceUUID());
        }

//        Intent intent = new Intent(getApplicationContext(), SocketService.class);
//        intent.putExtra(Global.COMMAND, Global.SIGN_IN_KAKAO);
//        intent.putExtra(Global.TOKEN, kakaoToken);
//        startService(intent);

//        KakaoTalkService.requestProfile(new KakaoTalkHttpResponseHandler<KakaoTalkProfile>() {
//            @Override
//            protected void onNotKakaoTalkUser() {
//
//            }
//
//            @Override
//            protected void onFailure(APIErrorResult errorResult) {
//
//            }
//
//            @Override
//            protected void onHttpSuccess(KakaoTalkProfile resultObj) {
//            }
//
//            @Override
//            protected void onHttpSessionClosedFailure(APIErrorResult errorResult) {
//
//            }
//        });

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            protected void onSuccess(UserProfile userProfile) {
                final String nickName = userProfile.getNickname();
                final String profileImage = userProfile.getProfileImagePath();
                final String thumbnailImage = userProfile.getThumbnailImagePath();

                id = userProfile.getId();
                new SocketIO(getApplicationContext());
                RequestManager.signInKakao(id, nickName, profileImage, thumbnailImage, new RequestManager.OnSignInKakao() {
                    @Override
                    public void onSuccess(UserEntity userEntity) {
                        if (userEntity.hasExtraProfile) {
                            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                            intent1.putExtra(Global.USER, userEntity);
                            startActivity(intent1);
                            finish();
                        } else {
                            initViews(R.layout.activity_sign_up);
                        }
                    }

                    @Override
                    public void onException(int code) {
                        if (code == 482) {
                            RequestManager.signInKakao(id, nickName, profileImage, thumbnailImage, new RequestManager.OnSignInKakao() {
                                @Override
                                public void onSuccess(UserEntity userEntity) {
                                    if (userEntity.hasExtraProfile) {
                                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                        intent1.putExtra(Global.USER, userEntity);
                                        startActivity(intent1);
                                        finish();
                                    } else {
                                        initViews(R.layout.activity_sign_up);
                                    }
                                }

                                @Override
                                public void onException(int code) {

                                }
                            });
                        } else {
                            new MaterialDialog.Builder(BaseActivity.context)
                                    .title(R.string.app_name)
                                    .content("로그인하는데 문제가 발생하였습니다.")
                                    .show();
                        }
                    }
                });
            }

            @Override
            protected void onNotSignedUp() {

            }

            @Override
            protected void onSessionClosedFailure(APIErrorResult errorResult) {

            }

            @Override
            protected void onFailure(APIErrorResult errorResult) {

            }
        });

//        BaasioUser.signInViaKakaotalkInBackground(
//                this,
//                Session.getCurrentSession().getAccessToken(),
//                new BaasioSignInCallback() {
//                    @Override
//                    public void onResponse(BaasioUser baasioUser) {
//                        UserEntity userEntity = new UserEntity(baasioUser);
//                        if (userEntity.hasExtraProfile) {
//                            BaseActivity.startActivityOnTopStack(MainActivity.class);
//                            finish();
//                        } else {
//                            initViews(R.layout.activity_sign_up);
//                        }
//                    }
//
//                    @Override
//                    public void onException(BaasioException e) {
//                        new MaterialDialog.Builder(BaseActivity.context)
//                                .title(R.string.app_name)
//                                .content("로그인하는데 문제가 발생하였습니다.")
//                                .show();
//                    }
//                });
    }
    
    MaterialDialog materialDialog;
    
    private void showProgress() {
        materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.app_name)
                .content("Waiting...")
                .progress(true, 0)
                .cancelable(false)
                .show();
    }


    public void hideLoadingView() {
        if (materialDialog == null) {
            return;
        }

        materialDialog.hide();
    }
}
