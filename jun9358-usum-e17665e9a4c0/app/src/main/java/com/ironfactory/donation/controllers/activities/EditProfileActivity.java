package com.ironfactory.donation.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.views.UserProfileForm;
import com.ironfactory.donation.socketIo.SocketException;
import com.ironfactory.donation.socketIo.SocketService;

public class EditProfileActivity extends BaseActivity {

    private static final String TAG = "EditProfileActivity";

    private class ViewHolder {
        public UserProfileForm userProfileForm;

        public ViewHolder(View view) {
            userProfileForm = (UserProfileForm) view.findViewById(R.id.user_profile_form);
        }
    }

    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_edit_profile);
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(R.layout.activity_edit_profile);
        Log.d(TAG, "액티비티 시작");

        viewHolder = new ViewHolder(getWindow().getDecorView());
        viewHolder.userProfileForm.setMode(UserProfileForm.Mode.EDIT);
        viewHolder.userProfileForm.setOnSubmitListener(new UserProfileForm.OnSubmitListener() {
            @Override
            public void onClick(View v) {
                showLoadingView();

                Intent intent = new Intent(getApplicationContext(), SocketService.class);
                intent.putExtra(Global.COMMAND, Global.UPDATE_USER_PROFILE);
                intent.putExtra(Global.USER, viewHolder.userProfileForm.getUserEntity());
                startService(intent);

//                RequestManager.updateUserProfile(viewHolder.userProfileForm.getUserEntity(), new BaasioCallback<BaasioUser>() {
//                    @Override
//                    public void onResponse(BaasioUser baasioUser) {
//                        Baas.io().setSignedInUser(baasioUser);
//                        hideLoadingView();
//                        finish();
//                    }
//
//                    @Override
//                    public void onException(BaasioException e) {
//
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
                SocketException.printErrMsg(code);
                SocketException.toastErrMsg(code);

                if (code != -1) {
                    if (command.equals(Global.UPDATE_USER_PROFILE)) {
                        // 회원 프로필 수정 응답
                        processUpdateUserProfile(intent);
                    }
                }
            }
        }
    }


    // TODO: 15. 11. 21. 회원 프로필 수정 응답
    private void processUpdateUserProfile(Intent intent) {
        Global.userEntity = intent.getParcelableExtra(Global.USER);
        hideLoadingView();
        finish();
    }
}
