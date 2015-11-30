package com.ironfactory.donation.controllers.activities;

import android.os.Bundle;
import android.util.Log;

import com.ironfactory.donation.R;

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
