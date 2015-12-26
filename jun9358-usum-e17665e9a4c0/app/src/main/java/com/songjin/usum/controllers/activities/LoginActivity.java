package com.songjin.usum.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kakao.Session;
import com.kakao.SessionCallback;
import com.kakao.exception.KakaoException;
import com.kakao.widget.LoginButton;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.managers.SchoolManager;
import com.songjin.usum.socketIo.SocketIO;
import com.songjin.usum.socketIo.SocketService;

import java.util.ArrayList;

public class LoginActivity extends BaseActivity {
    private final SessionCallback mySessionCallback = new SessionStatusCallback();
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
        if (schoolManager.isEmptyTable()) {
            showLoadingView();
            new SocketIO(getApplicationContext());

            // 학교 정보 로딩 요청
            RequestManager.getSchool(new RequestManager.OnGetSchool() {
                @Override
                public void onSuccess(final ArrayList<SchoolEntity> schoolEntities) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // DB에 저장한다.
                            schoolManager.insertSchools(schoolEntities);
                        }
                    }).start();

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

    private String getUniqueGuestId() {
        return Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    private void signUpViaGuest() {
        showLoadingView();
        final String androidId = getUniqueGuestId();
        final String userId = "guest" + androidId;
        UserEntity userEntity = new UserEntity();
        userEntity.id = userId;
        RequestManager.signUp(userEntity, new RequestManager.OnSignUp() {
            @Override
            public void onSuccess(UserEntity userEntity) {
                hideLoadingView();
                signInViaGuest();
            }

            @Override
            public void onException(int code) {
                hideLoadingView();
                if (code == 412 || code == 413) { // 이미 가입된 사용자
                    signInViaGuest();
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


    private void signInViaGuest() {
        showLoadingView();
        RequestManager.signIn("guest" + getUniqueGuestId(), new RequestManager.OnSignIn() {
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

    private void checkSession() {
        Session.initializeSession(this, mySessionCallback);

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

    private class ViewHolder {
        public LoginButton kakaoLoginButton;
        public Button guestLoginButton;

        public ViewHolder(View view) {
            kakaoLoginButton = (LoginButton) view.findViewById(R.id.login_with_kakao);
            guestLoginButton = (Button) view.findViewById(R.id.login_with_guest);
        }
    }


    private class SessionStatusCallback implements SessionCallback {
        @Override
        public void onSessionOpened() {
            startActivityUsingStack(SignUpActivity.class);
            finish();
        }

        @Override
        public void onSessionClosed(final KakaoException exception) {
            viewHolder.kakaoLoginButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(getApplicationContext(), SocketService.class);
        stopService(intent);
    }

    //    private class SchoolsQueryCallback implements BaasioQueryCallback {
//        @Override
//        public void onResponse(List<BaasioBaseEntity> baasioBaseEntities, List<Object> objects, BaasioQuery baasioQuery, long l) {
//            // 가져온 데이터를 변환하여 리스트로 만든다.
//            ArrayList<SchoolEntity> schoolEntities = new ArrayList<>();
//            for (BaasioBaseEntity entity : baasioBaseEntities) {
//                schoolEntities.add(new SchoolEntity(entity));
//            }
//
//            // DB에 저장한다.
//            schoolManager.insertSchools(schoolEntities);
//
//            // 다음 로딩
//            schoolsQuery = baasioQuery;
//            RequestManager.getNextSchoolsInBackground(schoolsQuery, this);
//        }
//
//        @Override
//        public void onException(BaasioException e) {
//            hideLoadingView();
//
//            if (e.getErrorCode() == 212) {
//                if (e.getStatusCode().compareTo("406") == 0) {
//                    new MaterialDialog.Builder(BaseActivity.context)
//                            .title(R.string.app_name)
//                            .content("서버 작업중입니다.")
//                            .show();
//                    return;
//                }
//            }
//
//            if (e.getErrorCode() == 0) {
//                // 더 이상 데이터가 없을 경우(로딩 완료)
//                if (e.getStatusCode() == null) {
//                    onLoadingSchoolsCompleted();
//                    return;
//                }
//            }
//
//            new MaterialDialog.Builder(BaseActivity.context)
//                    .title(R.string.app_name)
//                    .content("학교정보를 가져오는데 실패하였습니다.")
//                    .show();
//        }
//    }
}
