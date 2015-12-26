package com.songjin.usum.controllers.activities;

import android.os.Bundle;
import android.util.Log;

import com.songjin.usum.R;

public class SupportCompletedActivity extends BaseActivity {

    private static final String TAG = "SupportCompleteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_support_completed);

        Log.d(TAG, "액티비티 시작");
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);
    }
}
