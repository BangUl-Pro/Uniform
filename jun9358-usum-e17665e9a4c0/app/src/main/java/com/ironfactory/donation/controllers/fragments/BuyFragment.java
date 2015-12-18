package com.ironfactory.donation.controllers.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.activities.AddProductsActivity;
import com.ironfactory.donation.controllers.activities.BaseActivity;
import com.ironfactory.donation.controllers.views.ProductRecyclerView;
import com.ironfactory.donation.controllers.views.ProductSearchForm;
import com.ironfactory.donation.controllers.views.ProductSearchSlidingLayer;
import com.ironfactory.donation.dtos.ProductCardDto;
import com.ironfactory.donation.managers.AuthManager;
import com.ironfactory.donation.managers.RequestManager;
import com.ironfactory.donation.slidingtab.SlidingBaseFragment;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

public class BuyFragment extends SlidingBaseFragment {

    private static final String TAG = "BuyFragment";
    private int position = 1;
    private class ViewHolder {
        public ProductSearchSlidingLayer productSearchSlidingLayer;
        public ProductRecyclerView products;
        public FloatingActionButton writeProductsButton;

        public ViewHolder(View view) {
            productSearchSlidingLayer = (ProductSearchSlidingLayer) view.findViewById(R.id.product_search_sliding_layer);
            products = (ProductRecyclerView) view.findViewById(R.id.products);
            writeProductsButton = (FloatingActionButton) view.findViewById(R.id.donation_button);
        }
    }

    private ViewHolder viewHolder;

    private ArrayList<ProductCardDto> productCardDtos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productCardDtos = new ArrayList<>();
        initCallback();
    }

    private void initCallback() {
    }


    public void setProductCard(ArrayList<ProductCardDto> productCard) {
        if (productCard != null) {
            // 성공
            productCardDtos.addAll(productCard);
            viewHolder.products.setProductCardDtos(productCardDtos);
        }
        viewHolder.products.hideMoreProgress();
    }


    @Override
    public void onPageSelected() {
        switch (AuthManager.getSignedInUserType()) {
            case Global.GUEST:
                break;
            case Global.STUDENT:
                break;
            case Global.PARENT:
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy, container, false);
        viewHolder = new ViewHolder(view);

        viewHolder.products.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productCardDtos.clear();
                getSearchProduct(1);
            }
        });
        viewHolder.products.hideMoreProgress();
        viewHolder.products.setOnMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                getSearchProduct(position);
            }
        });

        viewHolder.writeProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.startActivityUsingStack(AddProductsActivity.class);
            }
        });

        viewHolder.productSearchSlidingLayer.getProductSearchForm().setOnSubmitListener(new ProductSearchForm.OnSubmitListener() {
            @Override
            public void onClick(View v) {
                getSearchProduct(1);
                viewHolder.productSearchSlidingLayer.closeLayer(true);
                productCardDtos.clear();
            }
        });

        return view;
    }


    private void getSearchProduct(int position) {
        this.position = position + 1;
        ProductSearchForm form = viewHolder.productSearchSlidingLayer.getProductSearchForm();
        int schoolId = form.getSelectedSchoolId();
        int sex = form.getSex();
        int category = form.getCategory();
        int size = form.getSize();

        RequestManager.searchProduct(schoolId, sex, category, position, size, new RequestManager.OnSearchProduct() {
            @Override
            public void onSuccess(ArrayList<ProductCardDto> productCardDtos) {
                setProductCard(productCardDtos);
            }

            @Override
            public void onException() {
                setProductCard(null);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        viewHolder.productSearchSlidingLayer.getProductSearchForm().submit();
    }
}
