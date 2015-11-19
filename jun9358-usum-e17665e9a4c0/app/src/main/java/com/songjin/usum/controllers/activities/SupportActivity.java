package com.songjin.usum.controllers.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.songjin.usum.R;

public class SupportActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_support);
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(R.layout.activity_support);
    }
}
