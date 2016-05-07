package com.songjin.usum.controllers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.push.PushActivity;
import com.kakao.push.PushService;
import com.kakao.push.response.model.PushTokenInfo;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.SharedPreferencesCache;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.views.UserProfileForm;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.socketIo.SocketIO;

import java.util.List;


public class SignUpActivity extends PushActivity {
    private static final String TAG = "SignUpActivity";
    private long id;
    private Activity activity = this;

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
        String curId = cache.getString("device_id");
        if (curId == null) {
            Bundle bundle = new Bundle();
            curId = getUniqueId();
            bundle.putString("device_id", curId);
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
        PushService.getPushTokens(new ApiResponseCallback<List<PushTokenInfo>>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(List<PushTokenInfo> result) {
                String message = "succeeded to get push tokens."
                        + "\ncount="
                        + result.size()
                        + "\nstories="
                        + result;
                Log.d(TAG, "푸시알림 토큰 = " + message);
                for (PushTokenInfo pushTokenInfo :
                        result) {
                    String pushToken = pushTokenInfo.getPushToken();
                    String deviceId = pushTokenInfo.getDeviceId();

                    if (!TextUtils.isEmpty(pushToken) && !TextUtils.isEmpty(deviceId) && deviceId.length() > 10) {
                        Log.d(TAG, "푸시토큰 = " + pushToken);
                        Log.d(TAG, "디바이스 아이디 = " + deviceId);
                        Log.d(TAG, "토큰 등록");
                        PushService.registerPushToken(new ApiResponseCallback<Integer>() {
                            @Override
                            public void onSessionClosed(ErrorResult errorResult) {
                                Log.d(TAG, "GCM PUSH 등록 에러 메세지 = " + errorResult.getErrorMessage());
                                Log.d(TAG, "GCM PUSH 등록 에러 URL = " + errorResult.getHttpStatus());
                                Log.d(TAG, "GCM PUSH 등록 에러 코드 = " + errorResult.getErrorCode());
                            }

                            @Override
                            public void onNotSignedUp() {

                            }

                            @Override
                            public void onSuccess(Integer result) {

                            }
                        }, pushToken, deviceId, Global.APP_VER);
                        break;
                    }
                }
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
                    new MaterialDialog.Builder(activity)
                            .title(R.string.app_name)
                            .content("약관에 동의해주세요.")
                            .show();
                    return;
                }
                String validateMsg = viewHolder.userProfileForm.validateForm();
                if (!validateMsg.isEmpty()) {
                    new MaterialDialog.Builder(activity)
                            .title(R.string.app_name)
                            .content(validateMsg)
                            .show();
                    return;
                }

                // 회원가입 요청
                showProgress();

                RequestManager.signUp(userEntity, new RequestManager.OnSignUp() {
                    @Override
                    public void onSuccess(UserEntity userEntity) {
                        hideLoadingView();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(Global.USER, userEntity);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onException(int code) {
                        new MaterialDialog.Builder(activity)
                                .title(R.string.app_name)
                                .content("회원가입하는데 문제가 발생하였습니다.")
                                .show();
                    }
                });

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



    // TODO RequestManager에 넣기
    private void requestCheckConfirmedUser() {
        setContentView(R.layout.activity_login);
        final SharedPreferencesCache cache = Session.getAppCache();
        final String kakaoToken = cache.getString(Global.TOKEN);
        Log.d(TAG, "kakaoToken = " + kakaoToken);

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                final String nickName = userProfile.getNickname();
                final String profileImage = userProfile.getProfileImagePath();
                final String thumbnailImage = userProfile.getThumbnailImagePath();

                id = userProfile.getId();

//                new SocketIO(getApplicationContext());
//                Log.d(TAG, "11111");
                RequestManager.signInKakao(id, nickName, profileImage, thumbnailImage, new RequestManager.OnSignInKakao() {
                    @Override
                    public void onSuccess(final UserEntity userEntity) {

                        if (userEntity.deviceId == null) {
                            SocketIO.setDeviceId(userEntity.id, getDeviceUUID(), new RequestManager.OnSetDeviceId() {
                                @Override
                                public void onSuccess() {
                                    userEntity.setDeviceId(userEntity.id);
                                    Log.d(TAG, "성공");

                                    String token = kakaoToken;
                                    if (token == null) {
                                        token = Session.getCurrentSession().getAccessToken();
                                        Bundle bundle = new Bundle();
                                        bundle.putString(Global.TOKEN, token);
                                        cache.save(bundle);

                                        PushService.registerPushToken(new ApiResponseCallback<Integer>() {
                                            @Override
                                            public void onSessionClosed(ErrorResult errorResult) {
                                                Log.d(TAG, "GCM PUSH 등록 에러 메세지 = " + errorResult.getErrorMessage());
                                                Log.d(TAG, "GCM PUSH 등록 에러 URL = " + errorResult.getHttpStatus());
                                                Log.d(TAG, "GCM PUSH 등록 에러 코드 = " + errorResult.getErrorCode());
                                            }

                                            @Override
                                            public void onNotSignedUp() {

                                            }

                                            @Override
                                            public void onSuccess(Integer result) {

                                            }
                                        }, kakaoToken, userEntity.deviceId, Global.APP_VER);
                                    }

                                    Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                    intent1.putExtra(Global.USER, userEntity);
                                    startActivity(intent1);
                                    finish();
                                }

                                @Override
                                public void onException() {
                                    Log.d(TAG, "실패");
                                }
                            });
                        } else if (!getDeviceUUID().equals(userEntity.deviceId)) {
                            Toast.makeText(SignUpActivity.this, "이미 다른 기기에서 접속 중입니다.", Toast.LENGTH_SHORT).show();
                            requestLogout();
                        } else if (userEntity.hasExtraProfile) {
                            String token = kakaoToken;
                            if (token == null) {
                                token = Session.getCurrentSession().getAccessToken();
                                Bundle bundle = new Bundle();
                                bundle.putString(Global.TOKEN, token);
                                cache.save(bundle);

                                PushService.registerPushToken(new ApiResponseCallback<Integer>() {
                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        Log.d(TAG, "GCM PUSH 등록 에러 메세지 = " + errorResult.getErrorMessage());
                                        Log.d(TAG, "GCM PUSH 등록 에러 URL = " + errorResult.getHttpStatus());
                                        Log.d(TAG, "GCM PUSH 등록 에러 코드 = " + errorResult.getErrorCode());
                                    }

                                    @Override
                                    public void onNotSignedUp() {

                                    }

                                    @Override
                                    public void onSuccess(Integer result) {

                                    }
                                }, kakaoToken, userEntity.deviceId, Global.APP_VER);
                            }

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
                                public void onSuccess(final UserEntity userEntity) {

                                    if (userEntity.deviceId == null) {
                                        SocketIO.setDeviceId(userEntity.id, getDeviceUUID(), new RequestManager.OnSetDeviceId() {
                                            @Override
                                            public void onSuccess() {
                                                userEntity.setDeviceId(userEntity.id);
                                                Log.d(TAG, "성공");

                                                String token = kakaoToken;
                                                if (token == null) {
                                                    token = Session.getCurrentSession().getAccessToken();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(Global.TOKEN, token);
                                                    cache.save(bundle);

                                                    PushService.registerPushToken(new ApiResponseCallback<Integer>() {
                                                        @Override
                                                        public void onSessionClosed(ErrorResult errorResult) {
                                                            Log.d(TAG, "GCM PUSH 등록 에러 메세지 = " + errorResult.getErrorMessage());
                                                            Log.d(TAG, "GCM PUSH 등록 에러 URL = " + errorResult.getHttpStatus());
                                                            Log.d(TAG, "GCM PUSH 등록 에러 코드 = " + errorResult.getErrorCode());
                                                        }

                                                        @Override
                                                        public void onNotSignedUp() {

                                                        }

                                                        @Override
                                                        public void onSuccess(Integer result) {

                                                        }
                                                    }, kakaoToken, userEntity.deviceId, Global.APP_VER);
                                                }

                                                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                                intent1.putExtra(Global.USER, userEntity);
                                                startActivity(intent1);
                                                finish();
                                            }

                                            @Override
                                            public void onException() {
                                                Log.d(TAG, "실패");
                                            }
                                        });
                                    } else if (!getDeviceUUID().equals(userEntity.deviceId)) {
                                        Toast.makeText(SignUpActivity.this, "이미 다른 기기에서 접속 중입니다.", Toast.LENGTH_SHORT).show();
                                        requestLogout();
                                    } else if (userEntity.hasExtraProfile) {
                                        String token = kakaoToken;
                                        if (token == null) {
                                            token = Session.getCurrentSession().getAccessToken();
                                            Bundle bundle = new Bundle();
                                            bundle.putString(Global.TOKEN, token);
                                            cache.save(bundle);

                                            PushService.registerPushToken(new ApiResponseCallback<Integer>() {
                                                @Override
                                                public void onSessionClosed(ErrorResult errorResult) {
                                                    Log.d(TAG, "GCM PUSH 등록 에러 메세지 = " + errorResult.getErrorMessage());
                                                    Log.d(TAG, "GCM PUSH 등록 에러 URL = " + errorResult.getHttpStatus());
                                                    Log.d(TAG, "GCM PUSH 등록 에러 코드 = " + errorResult.getErrorCode());
                                                }

                                                @Override
                                                public void onNotSignedUp() {

                                                }

                                                @Override
                                                public void onSuccess(Integer result) {

                                                }
                                            }, kakaoToken, userEntity.deviceId, Global.APP_VER);
                                        }

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
                        } else if (code == 483) {
                            initViews(R.layout.activity_sign_up);
                        } else {
                            new MaterialDialog.Builder(activity)
                                    .title(R.string.app_name)
                                    .content("로그인하는데 문제가 발생하였습니다.")
                                    .show();
                        }
                    }
                });
            }
        });
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

    private void requestLogout() {
        BaseActivity.showLoadingView();
        PushService.deregisterPushToken(new ApiResponseCallback<Boolean>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d(TAG, "토큰 제거 실패 = " + errorResult.getErrorMessage());
                Log.d(TAG, "코드 = " + errorResult.getErrorCode());
            }

            @Override
            public void onNotSignedUp() {
                Log.d(TAG, "푸시토큰 제거 not signedUp");
            }

            @Override
            public void onSuccess(Boolean result) {
                Log.d(TAG, "푸시토큰 제거 세션 = " + result);
            }
        }, Global.userEntity.id);

        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
//                BaseActivity.hideLoadingView();
//                BaseActivity.startActivityOnTopStack(LoginActivity.class);
//                ((Activity) MainActivity.context).finish();
//
//                SharedPreferencesCache cache = Session.getAppCache();
//                cache.clearAll();
            }
        });

        SocketIO.setDeviceId(Global.userEntity.id, null, new RequestManager.OnSetDeviceId() {
            @Override
            public void onSuccess() {
                SharedPreferencesCache cache = Session.getAppCache();
                cache.clearAll();

                //        BaseActivity.hideLoadingView();
//        BaseActivity.startActivityOnTopStack(LoginActivity.class);
                ((Activity) MainActivity.context).finish();
            }

            @Override
            public void onException() {

            }
        });
    }
}