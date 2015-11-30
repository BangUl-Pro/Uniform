package com.ironfactory.donation.controllers.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.slidinglayer.SlidingLayer;
import com.ironfactory.donation.R;

public class ProductSearchSlidingLayer extends LinearLayout {
    private class ViewHolder {
        public SlidingLayer slidingLayer;
        public LinearLayout slidingContainer;
        public LinearLayout slidingHandle;
        public ImageView slidngArrow;

        public ProductSearchForm productSearchForm;

        public ViewHolder(View view) {
            slidingLayer = (SlidingLayer) view.findViewById(R.id.sliding_layer);
            slidingContainer = (LinearLayout) view.findViewById(R.id.sliding_container);
            slidingHandle = (LinearLayout) view.findViewById(R.id.sliding_handle);
            slidngArrow = (ImageView) view.findViewById(R.id.sliding_arrow);

            productSearchForm = (ProductSearchForm) view.findViewById(R.id.product_search_form);
        }
    }

    private ViewHolder viewHolder;

    public ProductSearchSlidingLayer(Context context) {
        this(context, null);
    }

    public ProductSearchSlidingLayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductSearchSlidingLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView();
    }

    public void openLayer(boolean smoothAnim) {
        viewHolder.slidingLayer.openLayer(smoothAnim);
    }

    public void closeLayer(boolean smoothAnim) {
        viewHolder.slidingLayer.closeLayer(smoothAnim);
    }

    private void initView() {
        inflate(getContext(), R.layout.product_search_sliding_layer, this);

        viewHolder = new ViewHolder(this);

        viewHolder.slidingContainer.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.slidingLayer.getLayoutParams();
                viewHolder.slidingLayer.setStickTo(SlidingLayer.STICK_TO_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                layoutParams.height = viewHolder.slidingContainer.getHeight();
                viewHolder.slidingLayer.setLayoutParams(layoutParams);
            }
        });
        viewHolder.slidingHandle.post(new Runnable() {
            @Override
            public void run() {
                viewHolder.slidingLayer.setOffsetWidth(viewHolder.slidingHandle.getHeight());
            }
        });
        viewHolder.slidingLayer.setOnInteractListener(new SlidingLayer.OnInteractListener() {
            @Override
            public void onOpen() {
                viewHolder.slidngArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
            }

            @Override
            public void onClose() {
                viewHolder.slidngArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
            }

            @Override
            public void onOpened() {

            }

            @Override
            public void onClosed() {

            }
        });
    }

    public ProductSearchForm getProductSearchForm() {
        return viewHolder.productSearchForm;
    }
}
