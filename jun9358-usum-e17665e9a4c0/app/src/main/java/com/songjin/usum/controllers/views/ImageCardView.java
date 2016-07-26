package com.songjin.usum.controllers.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.activities.PhotoViewActivity;
import com.songjin.usum.entities.FileEntity;

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
        Glide.with(getContext()).load(imageUrl)
                .into(viewHolder.image);
    }

    public void setFileEntity(final FileEntity fileEntity) {
        imageUrl = "http://uniform-donation.herokuapp.com/imgs/" + fileEntity.id;
        Glide.with(getContext()).load(imageUrl)
                .into(viewHolder.image);
    }
}
