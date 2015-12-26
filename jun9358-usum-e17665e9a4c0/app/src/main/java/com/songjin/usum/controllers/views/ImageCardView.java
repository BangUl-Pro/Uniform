package com.songjin.usum.controllers.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.PhotoViewActivity;
import com.songjin.usum.entities.FileEntity;
import com.songjin.usum.managers.RequestManager;
import com.koushikdutta.ion.Ion;

import java.io.File;

public class ImageCardView extends CardView {
    private static final String TAG = "ImageCardView";

    private class ViewHolder {
        public SquareImageView image;

        public ViewHolder(View view) {
            image = (SquareImageView) view.findViewById(R.id.image);
        }
    }

    private ViewHolder viewHolder;

    private String imageUrl;

    public ImageCardView(Context context) {
        this(context, null);
    }

    public ImageCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.view_image_card, this);
        viewHolder = new ViewHolder(view);
        viewHolder.image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.context, PhotoViewActivity.class);
                intent.putExtra("imageUrl", imageUrl);
                BaseActivity.startActivityUsingStack(intent);
            }
        });
    }

    public void setUri(Uri uri) {
        imageUrl = uri.getPath();
        loadImage(imageUrl);
    }

    public void setFileEntity(final FileEntity fileEntity) {
        Log.d(TAG, "setFileEntity");
        File file = new File(BaseActivity.context.getCacheDir() + fileEntity.id);
        if (file.exists()) {
            // 성공
            imageUrl = BaseActivity.context.getCacheDir() + fileEntity.id;
            loadImage(imageUrl);
            return;
        }

        RequestManager.downloadImage("http://uniform-donation.herokuapp.com/imgs/" + fileEntity.id, file, new RequestManager.OnDownloadImage() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "이미지 다운로드 성공");
                imageUrl = BaseActivity.context.getCacheDir() + fileEntity.id;
                loadImage(imageUrl);
            }

            @Override
            public void onException() {
                Log.d(TAG, "이미지 다운로드 실패");
            }
        });


//        BaasioFile baasioFile = fileEntity.getBaasioFile();
//        baasioFile.fileDownloadInBackground(BaseActivity.context.getCacheDir() + fileEntity.id, baasioDownloadCallback);

//        RequestManager.downloadFile(fileEntity, new BaasioDownloadCallback() {
//            @Override
//            public void onResponse(String s) {
//                imageUrl = BaseActivity.context.getCacheDir() + fileEntity.uuid;
//                loadImage(imageUrl);
//            }
//
//            @Override
//            public void onException(BaasioException e) {
//
//            }
//
//            @Override
//            public void onProgress(long l, long l1) {
//
//            }
//        });
    }

    private void loadImage(String url) {
        Ion.with(viewHolder.image)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .load(url);
    }
}
