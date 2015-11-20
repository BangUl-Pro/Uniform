package com.songjin.usum.controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.melnykov.fab.FloatingActionButton;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.AddProductsActivity;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.views.ProductRecyclerView;
import com.songjin.usum.controllers.views.ProductSearchForm;
import com.songjin.usum.controllers.views.ProductSearchSlidingLayer;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.slidingtab.SlidingBaseFragment;
import com.songjin.usum.socketIo.SocketService;

import java.util.ArrayList;
import java.util.List;

public class BuyFragment extends SlidingBaseFragment {
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
//    private BaasioQuery productCardDtoQuery;
    private RequestManager.TypedBaasioQueryCallback<ProductCardDto> productCardDtoQueryCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productCardDtos = new ArrayList<>();
        initCallback();
    }

    private void initCallback() {
        productCardDtoQueryCallback = new RequestManager.TypedBaasioQueryCallback<ProductCardDto>() {
            @Override
            public void onResponse(List<ProductCardDto> entities) {
                productCardDtos.addAll(entities);
                viewHolder.products.setProductCardDtos(productCardDtos);
                viewHolder.products.hideMoreProgress();
            }

            @Override
            public void onException(BaasioException e) {
                viewHolder.products.hideMoreProgress();
            }
        };
    }

    @Override
    public void onPageSelected() {
        switch (AuthManager.getSignedInUserType()) {
            case GUEST:
                break;
            case STUDENT:
                break;
            case PARENT:
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
                getProduct();
//                RequestManager.getProductsInBackground(productCardDtoQuery, false, productCardDtoQueryCallback);
            }
        });
        viewHolder.products.hideMoreProgress();
        viewHolder.products.setOnMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
//                RequestManager.getNextProductsInBackground(productCardDtoQuery, false, productCardDtoQueryCallback);
                getProduct();
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
//                productCardDtoQuery = viewHolder.productSearchSlidingLayer.getProductSearchForm().getSearchQuery();

                getProduct();

                viewHolder.productSearchSlidingLayer.closeLayer(true);
                productCardDtos.clear();
//                RequestManager.getProductsInBackground(productCardDtoQuery, false, productCardDtoQueryCallback);
            }
        });

        return view;
    }


    private void getProduct() {
        ProductSearchForm form = viewHolder.productSearchSlidingLayer.getProductSearchForm();
        int schoolId = form.getSelectedSchoolId();
        int sex = form.getSex();
        int category = form.getCategory();
        int size = form.getSize();

        Intent intent = new Intent(getActivity(), SocketService.class);
        intent.putExtra(Global.COMMAND, Global.SEARCH_PRODUCT);
        intent.putExtra(Global.SCHOOL_ID, schoolId);
        intent.putExtra(Global.SEX, sex);
        intent.putExtra(Global.CATEGORY, category);
        intent.putExtra(Global.SIZE, size);
        getActivity().startService(intent);
    }


    @Override
    public void onStart() {
        super.onStart();

        viewHolder.productSearchSlidingLayer.getProductSearchForm().submit();
    }
}
