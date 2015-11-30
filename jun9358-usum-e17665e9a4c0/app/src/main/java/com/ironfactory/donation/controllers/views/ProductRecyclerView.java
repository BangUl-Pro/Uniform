package com.ironfactory.donation.controllers.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.activities.BaseActivity;
import com.ironfactory.donation.controllers.activities.ProductDetailActivity;
import com.ironfactory.donation.dtos.ProductCardDto;

import java.util.ArrayList;

public class ProductRecyclerView extends SuperRecyclerView {
    private ArrayList<ProductCardDto> productCardDtos;

    public ProductRecyclerView(Context context) {
        this(context, null);
    }

    public ProductRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView();
    }

    private void initView() {
        productCardDtos = new ArrayList<>();
        setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter(new ProductAdapter(productCardDtos));
    }

    public void setEmptyView(int resId) {
        if (mEmptyId != 0) {
            return;
        }
        mEmptyId = resId;

        this.mEmpty = (ViewStub) findViewById(com.malinskiy.superrecyclerview.R.id.empty);
        this.mEmpty.setLayoutResource(this.mEmptyId);
        if (this.mEmptyId != 0) {
            this.mEmptyView = this.mEmpty.inflate();
        }

        this.mEmpty.setVisibility(View.GONE);
    }

    public void setProductCardDtos(ArrayList<ProductCardDto> productCardDtos) {
        setEmptyView(R.layout.view_empty);

        this.productCardDtos.clear();
        this.productCardDtos.addAll(productCardDtos);
        getAdapter().notifyDataSetChanged();
    }

    private class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
        private ArrayList<ProductCardDto> productCardDtos;

        public ProductAdapter(ArrayList<ProductCardDto> productCardDtos) {
            this.productCardDtos = productCardDtos;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ProductCardView productCardView = new ProductCardView(parent.getContext());
            productCardView.setTag("ProductCardView");
            productCardView.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            return new ViewHolder(productCardView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ProductCardDto productCardDto = productCardDtos.get(position);
            holder.productCardView.setProductCardDto(productCardDto);
            holder.productCardView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productCardDto.productEntity.id == null) {
                        return;
                    }

                    Intent intent = new Intent(BaseActivity.context, ProductDetailActivity.class);
                    intent.putExtra("productCardDto", productCardDto);
                    BaseActivity.startActivityUsingStack(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return productCardDtos.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ProductCardView productCardView;

            public ViewHolder(View v) {
                super(v);

                productCardView = (ProductCardView) v.findViewWithTag("ProductCardView");
            }
        }
    }
}
