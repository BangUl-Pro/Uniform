package com.ironfactory.donation.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.kakao.Session;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.helper.SharedPreferencesCache;


public class SignUpActivity extends BaseActivity {
    private static final String TAG = "SignUpActivity";
    private long id;

    private class ViewHolder {
        public CheckBox termsAggreementCheckBox;
        public UserProfileForm userProfileForm;

        public ViewHolder(View view) {
            termsAggreementCheckBox = (CheckBox) view.findViewById(R.id.terms_agreement);
            userProfileForm = (UserProfileForm) view.findViewById(R.id.user_profile_form);
        }
    }

    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestCheckConfirmedUser();
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);
        Log.d(TAG, "액티비티 시작");

        // 컨트롤변수 초기화
        viewHolder = new ViewHolder(getWindow().getDecorView());
        viewHolder.userProfileForm.setUserId(id);
        viewHolder.userProfileForm.setOnSubmitListener(new UserProfileForm.OnSubmitListener() {
            @Override
            public void onClick(View v) {
                UserEntity userEntity = viewHolder.userProfileForm.getUserEntity();

                // 약관에 동의 했는지 체크하기
                if (!viewHolder.termsAggreementCheckBox.isChecked()) {
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
                BaseActivity.showLoadingView();

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
        BaseActivity.hideLoadingView();

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
                id = userProfile.getId();
                new SocketIO(getApplicationContext());
                RequestManager.signInKakao(id, new RequestManager.OnSignInKakao() {
                    @Override
                    public void onSuccess(UserEntity userEntity) {
                        if (userEntity.hasExtraProfile) {
                            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                            intent1.putExtra(Global.USER, userEntity);
                            Log.d(TAG, "2.userId = " + userEntity.id);
                            startActivity(intent1);
                            finish();
                        } else {
                            initViews(R.layout.activity_sign_up);
                        }
                    }

                    @Override
                    public void onException(int code) {
                        if (code == 482) {
                            RequestManager.signInKakao(id, new RequestManager.OnSignInKakao() {
                                @Override
                                public void onSuccess(UserEntity userEntity) {
                                    if (userEntity.hasExtraProfile) {
                                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                        intent1.putExtra(Global.USER, userEntity);
                                        Log.d(TAG, "2.userId = " + userEntity.id);
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
}
