package com.songjin.usum.controllers.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.songjin.usum.R;

/**
 * Thanks to StackOverflow
 *
 * @link http://stackoverflow.com/a/15264039/425050
 */

public class SquareImageView extends ImageView {
    private static final int SQUARED_BY_WIDTH = 1;
    private static final int SQUARED_BY_HEIGHT = 2;
    private int squredBy;

    public SquareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SquareImageView, 0, 0);
        squredBy = typedArray.getInteger(R.styleable.SquareImageView_squared_by, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        switch (squredBy) {
            case SQUARED_BY_WIDTH:
                setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
                break;
            case SQUARED_BY_HEIGHT:
                setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight());
                break;
        }
    }
}