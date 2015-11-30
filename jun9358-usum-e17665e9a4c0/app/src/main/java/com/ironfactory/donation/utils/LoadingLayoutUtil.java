package com.ironfactory.donation.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ironfactory.donation.R;

public class LoadingLayoutUtil {
    private ViewGroup viewGroup;
    private View loadingView;

    public LoadingLayoutUtil(Context context, ViewGroup viewGroup) {
        this.viewGroup = viewGroup;

        LayoutInflater inflater = LayoutInflater.from(context);
        loadingView = inflater.inflate(R.layout.loading_layout, this.viewGroup, false);
    }

    public void showLoadingLayout() {
        hideLoadingLayout();
        viewGroup.addView(loadingView);
    }

    public void hideLoadingLayout() {
        viewGroup.removeView(loadingView);
    }
}
