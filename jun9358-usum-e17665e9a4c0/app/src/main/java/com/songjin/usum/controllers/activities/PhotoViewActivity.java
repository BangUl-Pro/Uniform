package com.songjin.usum.controllers.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.songjin.usum.R;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewActivity extends Activity {
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
        Ion.with(viewHolder.photoView)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .load(url)
                .setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, ImageView result) {
                        mAttacher = new PhotoViewAttacher(result);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAttacher.cleanup();
    }
}