package com.songjin.usum.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class LoginActivity extends BaseActivity {
    private final ISessionCallback mySessionCallback = new SessionStatusCallback();
    private ViewHolder viewHolder;
    private SchoolManager schoolManager;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_login);
        Log.d(TAG, "액티비티 시작");

        schoolManager = new SchoolManager(this);
        new SocketIO(getApplicationContext());

        if (schoolManager.isEmptyTable()) {
            showLoadingView("학교 DB 다운로드 중 입니다.");

            schoolManager.deleteAllSchools();
            schoolManager.copy();
            onLoadingSchoolsCompleted();
            hideLoadingView();

            // 학교 정보 로딩 요청
//            RequestManager.getSchool(new RequestManager.OnGetSchool() {
//                @Override
//                public void onSuccess(final ArrayList<SchoolEntity> schoolEntities) {
//                    schoolManager.insertSchools(schoolEntities);
//
//                    onLoadingSchoolsCompleted();
//                    hideLoadingView();
//                }
//
//                @Override
//                public void onException() {
//                    new MaterialDialog.Builder(context)
//                            .title(R.string.app_name)
//                            .content("학교정보를 가져오는데 실패하였습니다.")
//                            .show();
//                    hideLoadingView();
//                }
//            });
        } else {
            onLoadingSchoolsCompleted();
            schoolManager.open();
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
    }


    private void updateGuestExtraProfile(UserEntity guest) {
        showLoadingView();

        guest.realName = "방문자";
        guest.sex = Global.MAN;
        guest.userType = Global.GUEST;
        guest.phone = "0";
        guest.schoolId = 0;

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Log.e(TAG, "1");
        intent.putExtra(Global.USER, guest);
        startActivity(intent);
        finish();
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
        Log.d(TAG, "checkSession");
        if (Session.getCurrentSession().isOpened()) {
            Log.d(TAG, "열려 있음 ");
//            Intent intent = new Intent(this, SignUpActivity.class);
//            startActivity(intent);
//            Log.e(TAG, "sign 2");
//            finish();
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

//        super.onActivityResult(requestCode, resultCode, data);
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
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            viewHolder.kakaoLoginButton.setVisibility(View.VISIBLE);
        }
    }

    public boolean checkDB(Context mContext){
        String filePath = Environment.getExternalStorageState() + "/data/data/" + Global.PACKAGE_NAME + "/databases/" + SchoolEntity.COLLECTION_NAME;
        File file = new File(filePath);
        Log.d(TAG, "DB있나 " + file.exists());
        return file.exists();
    }

    // Dump DB
    public void dumpDB(Context mContext){
        AssetManager manager = mContext.getAssets();
        String folderPath = Environment.getExternalStorageDirectory().getPath() + "/data/data/" + Global.PACKAGE_NAME + "/databases";
//        String filePath = "/data/data/" + Global.PACKAGE_NAME + "/databases/bazar.db";
        String filePath = Environment.getExternalStorageState() + "/data/data/" + Global.PACKAGE_NAME + "/databases/" + SchoolEntity.COLLECTION_NAME;

        File folder = new File(folderPath);
        File file = new File(filePath);

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        try {
            InputStream is = manager.open(SchoolEntity.COLLECTION_NAME);
            BufferedInputStream bis = new BufferedInputStream(is);

            if (folder.exists()) {
                Log.d(TAG, "폴더 있음");
            }else{
                folder.mkdirs();
                Log.d(TAG, "폴더 없음");
            }

            Log.d(TAG, "파일 생성 준비");
            if (file.exists()) {
                file.delete();
                file.createNewFile();
                Log.d(TAG, "파일 생성");
            }
            Log.d(TAG, "파일 생성 끝");

            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read = -1;
            byte[] buffer = new byte[1024];
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }

            bos.flush();
            bos.close();
            fos.close();
            bis.close();
            is.close();

        } catch (IOException e) {
            Log.e("ErrorMessage : ", e.getMessage());
        }
    }
}
