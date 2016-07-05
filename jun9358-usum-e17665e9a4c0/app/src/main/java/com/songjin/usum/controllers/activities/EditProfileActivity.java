package com.songjin.usum.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.views.UserProfileForm;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.RequestManager;

public class EditProfileActivity extends BaseActivity {

    private static final String TAG = "EditProfileActivity";
    private UserEntity userEntity;

    private class ViewHolder {
        public UserProfileForm userProfileForm;

        public ViewHolder(View view) {
            userProfileForm = (UserProfileForm) view.findViewById(R.id.user_profile_form);
            userProfileForm.setUserEntity(userEntity);
        }
    }

    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        userEntity = intent.getParcelableExtra(Global.USER);
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(R.layout.activity_edit_profile);
        Log.d(TAG, "액티비티 시작");

        viewHolder = new ViewHolder(getWindow().getDecorView());
        viewHolder.userProfileForm.setRealName(userEntity.realName);
        viewHolder.userProfileForm.setSex(userEntity.sex);
        viewHolder.userProfileForm.setType(userEntity.userType);
        viewHolder.userProfileForm.setMode(UserProfileForm.Mode.EDIT);
        viewHolder.userProfileForm.setOnSubmitListener(new UserProfileForm.OnSubmitListener() {
            @Override
            public void onClick(View v) {
                showLoadingView();

                RequestManager.updateUserProfile(viewHolder.userProfileForm.getUserEntity(), new RequestManager.OnUpdateUserProfile() {
                    @Override
                    public void onSuccess(UserEntity userEntity) {
                        EditProfileActivity.this.userEntity = userEntity;
                        hideLoadingView();
                        finish();
                    }

                    @Override
                    public void onException() {

                    }
                });
            }
        });
    }
}
