package com.songjin.usum.controllers.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.songjin.usum.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewActivity extends Activity {

    private static final String TAG = "PhotoViewActivity";

    private class ViewHolder {
        public PhotoView photoView;

        public ViewHolder(View view) {
            photoView = (PhotoView) view.findViewById(R.id.photo_view);
        }
    }

    private ViewHolder viewHolder;

    private String imageUrl;
    private PhotoViewAttacher mAttacher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getIntent().getStringExtra("imageUrl");
        initViews(R.layout.activity_photo_view);
    }

    protected void initViews(int layoutResID) {
        setContentView(layoutResID);

        viewHolder = new ViewHolder(getWindow().getDecorView());
        loadImage(imageUrl);
    }

    private void loadImage(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(viewHolder.photoView)
                .setRequest(new Request() {
                    @Override
                    public void begin() {

                    }

                    @Override
                    public void pause() {

                    }

                    @Override
                    public void clear() {

                    }

                    @Override
                    public boolean isPaused() {
                        return false;
                    }

                    @Override
                    public boolean isRunning() {
                        return false;
                    }

                    @Override
                    public boolean isComplete() {
                        mAttacher = new PhotoViewAttacher(viewHolder.photoView);
                        return false;
                    }

                    @Override
                    public boolean isResourceSet() {
                        return false;
                    }

                    @Override
                    public boolean isCancelled() {
                        return false;
                    }

                    @Override
                    public boolean isFailed() {
                        return false;
                    }

                    @Override
                    public void recycle() {

                    }
                });
//        Ion.with(viewHolder.photoView)
//                .placeholder(R.drawable.ic_launcher)
//                .error(R.drawable.ic_launcher)
//                .load(url)
//                .setCallback(new FutureCallback<ImageView>() {
//                    @Override
//                    public void onCompleted(Exception e, ImageView result) {
//                        if (result != null)
//                            mAttacher = new PhotoViewAttacher(result);
//                    }
//                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAttacher != null)
            mAttacher.cleanup();
    }
}