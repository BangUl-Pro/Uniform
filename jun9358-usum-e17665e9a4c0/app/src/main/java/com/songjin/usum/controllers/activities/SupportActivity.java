package com.songjin.usum.controllers.activities;

import android.os.Bundle;
import android.util.Log;

import com.songjin.usum.R;

public class SupportActivity extends BaseActivity {

    private static final String TAG = "SupportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_support);

        Log.d(TAG, "액티비티 시작");
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(R.layout.activity_support);
    }
}
