package com.ironfactory.donation.controllers.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.views.UserProfileForm;
import com.ironfactory.donation.entities.UserEntity;
import com.ironfactory.donation.managers.RequestManager;

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

                RequestManager.updateUserProfile(viewHolder.userProfileForm.getUserEntity(), new RequestManager.OnUpdateUserProfile() {
                    @Override
                    public void onSuccess(UserEntity userEntity) {
                        Global.userEntity = userEntity;
                        hideLoadingView();
                        finish();
                    }

                    @Override
                    public void onException() {

                    }
                });

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
}
