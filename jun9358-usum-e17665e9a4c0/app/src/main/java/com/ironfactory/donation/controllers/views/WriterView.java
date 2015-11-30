package com.ironfactory.donation.controllers.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.ironfactory.donation.R;
import com.ironfactory.donation.entities.UserEntity;

public class WriterView extends RelativeLayout {
    public SquareImageView thumbnailImage;
    public TextView name;
    public TextView writtenTime;
    public ImageButton moreButton;

    private int moreButtonVisibility;

    public WriterView(Context context) {
        super(context);
        initView();
    }

    public WriterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WriterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WriterView, 0, 0);
        moreButtonVisibility = typedArray.getInteger(R.styleable.WriterView_more_button_visibility, 0);
        setMoreButtonVisibility(moreButtonVisibility);
    }

    public void initView() {
        inflate(getContext(), R.layout.view_writer, this);
        thumbnailImage = (SquareImageView) findViewById(R.id.thumbnail_image);
        name = (TextView) findViewById(R.id.name);
        writtenTime = (TextView) findViewById(R.id.written_time);
        moreButton = (ImageButton) findViewById(R.id.more_button);
    }

    public void setUserEntity(UserEntity userEntity) {
        if (userEntity.id == null) {
            setName("탈퇴한 회원");
            return;
        }

        setThumbnailImage(userEntity.picture);
        setName(userEntity.realName);
    }

    public void setThumbnailImage(String url) {
        Ion.with(thumbnailImage)
                .placeholder(R.drawable.ic_launcher)
                .load(url);
    }

    public void setName(String writterName) {
        name.setText(writterName);
    }

    public void setWrittenTime(long writtenTimestamp) {
        writtenTime.setText(DateFormat.format("yyyy년 MM월 dd일 HH시 mm분에 작성", writtenTimestamp));
    }

    public void setMoreButtonVisibility(int visibility) {
        moreButton.setVisibility(visibility);
    }
}
