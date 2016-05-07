package com.songjin.usum.controllers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.songjin.usum.R;

public abstract class BaseActivity extends ActionBarActivity {
    public static Context context;
    private static MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
    }

    @Override
    public void onResume() {
        super.onResume();

        context = this;
    }

    protected abstract void initViews(int layoutResID);

    public static void startActivityUsingStack(Class<?> cls) {
        Intent intent = new Intent(context, cls);
        startActivityUsingStack(intent);
    }

    public static void startActivityUsingStack(Intent intent) {
        context.startActivity(intent);
    }

    public static void startActivityOnTopStack(Class<?> cls) {
        Intent intent = new Intent(context, cls);
        startActivityOnTopStack(intent);
    }

    public static void startActivityOnTopStack(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void showGuestBlockedDialog() {
        new MaterialDialog.Builder(context)
                .title(R.string.app_name)
                .content("비회원은 사용할 수 없는 메뉴입니다.\n로그인페이지로 이동합니다.")
                .positiveText("확인")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        BaseActivity.startActivityOnTopStack(LoginActivity.class);
                        ((Activity)MainActivity.context).finish();
                    }
                })
                .cancelable(false)
                .show();
    }

    public static void showLoadingView() {
        if (materialDialog != null) {
            materialDialog.hide();
        }

        Log.d("USUM", "BaseActivity.context: " + context);
        materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.app_name)
                .content("Waiting...")
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    public static void showLoadingView(String msg) {
        if (materialDialog != null) {
            materialDialog.hide();
        }

        Log.d("USUM", "BaseActivity.context: " + context);
        materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.app_name)
                .content(msg)
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    public static void hideLoadingView() {
        if (materialDialog == null) {
            return;
        }

        materialDialog.hide();
    }
}
