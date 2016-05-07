package com.songjin.usum.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.usermgmt.LoginButton;
import com.kakao.util.exception.KakaoException;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.managers.SchoolManager;
import com.songjin.usum.socketIo.SocketIO;

import java.util.ArrayList;
import java.util.Random;

public class LoginActivity extends BaseActivity {
    private final ISessionCallback mySessionCallback = new SessionStatusCallback();
//    private final BaasioQueryCallback schoolsQueryCallback = new SchoolsQueryCallback();
    private ViewHolder viewHolder;
    private SchoolManager schoolManager;
    private static final String TAG = "LoginActivity";
//    private BaasioQuery schoolsQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_login);
        Log.d(TAG, "액티비티 시작");

        schoolManager = new SchoolManager(this);
        new SocketIO(getApplicationContext());
        if (schoolManager.isEmptyTable()) {
            showLoadingView("학교 DB 다운로드 중 입니다.");

            // 학교 정보 로딩 요청
            RequestManager.getSchool(new RequestManager.OnGetSchool() {
                @Override
                public void onSuccess(final ArrayList<SchoolEntity> schoolEntities) {
                    schoolManager.insertSchools(schoolEntities);

                    onLoadingSchoolsCompleted();
                    hideLoadingView();
                }

                @Override
                public void onException() {
                    new MaterialDialog.Builder(context)
                            .title(R.string.app_name)
                            .content("학교정보를 가져오는데 실패하였습니다.")
                            .show();
                    hideLoadingView();
                }
            });


//            schoolsQuery = new BaasioQuery();
//            schoolsQuery.setType(SchoolEntity.COLLECTION_NAME);
//            schoolsQuery.setOrderBy(SchoolEntity.PROPERTY_ID, BaasioQuery.ORDER_BY.ASCENDING);
//            schoolsQuery.setLimit(500);
//            RequestManager.getSchoolsInBackground(schoolsQuery, schoolsQueryCallback);
        } else {
            onLoadingSchoolsCompleted();
        }
    }

    private void onLoadingSchoolsCompleted() {
        SettingFragment.context = getApplicationContext();
        SettingFragment.setSchoolsLoaded(true);
        showLoginButtons();
        checkSession();
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);

        // 컨트롤변수 초기화
        viewHolder = new ViewHolder(getWindow().getDecorView());
        viewHolder.guestLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpViaGuest();
            }
        });

        // View 초기화
        hideLoginButtons();
    }

    private void signUpViaGuest() {
        showLoadingView();
        UserEntity userEntity = new UserEntity();
        userEntity.id = getRandomName();
        RequestManager.signUp(userEntity, new RequestManager.OnSignUp() {
            @Override
            public void onSuccess(UserEntity userEntity) {
                hideLoadingView();
                signInViaGuest(userEntity.id);
            }

            @Override
            public void onException(int code) {
                hideLoadingView();
                if (code == 412 || code == 413) { // 이미 가입된 사용자
//                    signInViaGuest();
                } else {
                    new MaterialDialog.Builder(context)
                            .title(R.string.app_name)
                            .content("비회원 로그인중에 문제가 발생하였습니다.\n나중에 다시 시도해주세요.")
                            .show();
                }
            }
        });


//        BaasioUser.signUpInBackground(
//                "guest" + androidId,
//                "guest" + androidId,
//                null,
//                androidId,
//                new BaasioSignUpCallback() {
//                    @Override
//                    public void onResponse(BaasioUser baasioUser) {
//                        hideLoadingView();
//                        signInViaGuest();
//                    }
//
//                    @Override
//                    public void onException(BaasioException e) {
//                        hideLoadingView();
//                        if (e.getErrorCode() == 913) { // 이미 가입된 사용자
//                            signInViaGuest();
//                            return;
//                        }
//
//                        new MaterialDialog.Builder(BaseActivity.context)
//                                .title(R.string.app_name)
//                                .content("비회원 로그인중에 문제가 발생하였습니다.\n나중에 다시 시도해주세요.")
//                                .show();
//                    }
//                }
//        );
    }


    private String getRandomName() {
        String character = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(character.charAt(random.nextInt(character.length())));
        }
        return sb.toString();
    }


    private void signInViaGuest(String id) {
        showLoadingView();
        RequestManager.signIn(id, new RequestManager.OnSignIn() {
            @Override
            public void onSuccess(UserEntity userEntity) {
                hideLoadingView();
                updateGuestExtraProfile(userEntity);
            }

            @Override
            public void onException() {
                hideLoadingView();
                new MaterialDialog.Builder(context)
                        .title(R.string.app_name)
                        .content("비회원 로그인중에 문제가 발생하였습니다.\n나중에 다시 시도해주세요.")
                        .show();
            }
        });
//        BaasioUser.signInInBackground(
//                BaseActivity.context,
//                "guest" + getUniqueGuestId(),
//                getUniqueGuestId(),
//                new BaasioSignInCallback() {
//                    @Override
//                    public void onResponse(BaasioUser baasioUser) {
//                        hideLoadingView();
//                        updateGuestExtraProfile();
//                    }
//
//                    @Override
//                    public void onException(BaasioException e) {
//                        hideLoadingView();
//                        new MaterialDialog.Builder(BaseActivity.context)
//                                .title(R.string.app_name)
//                                .content("비회원 로그인중에 문제가 발생하였습니다.\n나중에 다시 시도해주세요.")
//                                .show();
//                    }
//                }
//        );
    }


    private void updateGuestExtraProfile(UserEntity guest) {
        showLoadingView();

//        UserEntity guest = new UserEntity(Baas.io().getSignedInUser());
        guest.realName = "방문자";
        guest.sex = Global.MAN;
        guest.userType = Global.GUEST;
        guest.phone = "0";
        guest.schoolId = 0;

//        BaseActivity.startActivityOnTopStack(MainActivity.class);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(Global.USER, guest);
        startActivity(intent);
        finish();
//        RequestManager.requestSignUp(guest, new BaasioSignInCallback() {
//            @Override
//            public void onResponse(BaasioUser baasioUser) {
//                hideLoadingView();
//                BaseActivity.startActivityOnTopStack(MainActivity.class);
//                finish();
//            }
//
//            @Override
//            public void onException(BaasioException e) {
//                hideLoadingView();
//                new MaterialDialog.Builder(BaseActivity.context)
//                        .title(R.string.app_name)
//                        .content("비회원 로그인중에 문제가 발생하였습니다.\n나중에 다시 시도해주세요.")
//                        .show();
//            }
//        });
    }

    private void hideLoginButtons() {
        viewHolder.kakaoLoginButton.setVisibility(View.GONE);
        viewHolder.guestLoginButton.setVisibility(View.GONE);
    }

    private void showLoginButtons() {
        viewHolder.kakaoLoginButton.setVisibility(View.VISIBLE);
        viewHolder.guestLoginButton.setVisibility(View.VISIBLE);
    }

    private class KakaoSDKAdapter extends KakaoAdapter {
        @Override
        public IApplicationConfig getApplicationConfig() {
            return new IApplicationConfig() {
                @Override
                public Activity getTopActivity() {
                    return LoginActivity.this;
                }

                @Override
                public Context getApplicationContext() {
                    return LoginActivity.this;
                }
            };
        }
    }

    private void checkSession() {
        if (KakaoSDK.getAdapter() == null)
            KakaoSDK.init(new KakaoSDKAdapter());
        Session.getCurrentSession().addCallback(mySessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
//        Session.getCurrentSession()
//        Session.initializeSession(this, mySessionCallback);
        Log.d(TAG, "checkSession");
        if (Session.getCurrentSession().isOpened()) {
            Log.d(TAG, "열려 있음 ");
            startActivityUsingStack(SignUpActivity.class);
            finish();
        } else if (Session.getCurrentSession().isClosed()) {
            Log.d(TAG, "닫혀 있음");
            showLoginButtons();
        } else {
            new MaterialDialog.Builder(context)
                    .title(R.string.app_name)
                    .content("세션에 문제가 있습니다.")
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(mySessionCallback);
    }

    private class ViewHolder {
        public LoginButton kakaoLoginButton;
        public Button guestLoginButton;

        public ViewHolder(View view) {
            kakaoLoginButton = (LoginButton) view.findViewById(R.id.login_with_kakao);
            guestLoginButton = (Button) view.findViewById(R.id.login_with_guest);
        }
    }


    private class SessionStatusCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            startActivityUsingStack(SignUpActivity.class);
            finish();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            viewHolder.kakaoLoginButton.setVisibility(View.VISIBLE);
        }
    }
}
