package com.songjin.usum.controllers.activities;

import android.os.Bundle;

import com.songjin.usum.R;

public class SupportCompletedActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_support_completed);
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);
    }
}
