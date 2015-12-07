package com.ironfactory.donation.controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.melnykov.fab.FloatingActionButton;
import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.activities.AddProductsActivity;
import com.ironfactory.donation.controllers.activities.BaseActivity;
import com.ironfactory.donation.controllers.views.ProductRecyclerView;
import com.ironfactory.donation.controllers.views.ProductSearchForm;
import com.ironfactory.donation.controllers.views.ProductSearchSlidingLayer;
import com.ironfactory.donation.dtos.ProductCardDto;
import com.ironfactory.donation.managers.AuthManager;
import com.ironfactory.donation.slidingtab.SlidingBaseFragment;
import com.ironfactory.donation.socketIo.SocketService;

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
//    private BaasioQuery productCardDtoQuery;
//    private RequestManager.TypedBaasioQueryCallback<ProductCardDto> productCardDtoQueryCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productCardDtos = new ArrayList<>();
        initCallback();
    }

    private void initCallback() {
//        productCardDtoQueryCallback = new RequestManager.TypedBaasioQueryCallback<ProductCardDto>() {
//            @Override
//            public void onResponse(List<ProductCardDto> entities) {
//                productCardDtos.addAll(entities);
//                viewHolder.products.setProductCardDtos(productCardDtos);
//                viewHolder.products.hideMoreProgress();
//            }
//
//            @Override
//            public void onException(BaasioException e) {
//                viewHolder.products.hideMoreProgress();
//            }
//        };
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
                position++;
//                RequestManager.getProductsInBackground(productCardDtoQuery, false, productCardDtoQueryCallback);
            }
        });
        viewHolder.products.hideMoreProgress();
        viewHolder.products.setOnMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
//                RequestManager.getNextProductsInBackground(productCardDtoQuery, false, productCardDtoQueryCallback);
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
//                productCardDtoQuery = viewHolder.productSearchSlidingLayer.getProductSearchForm().getSearchQuery();

                getSearchProduct(1);

                viewHolder.productSearchSlidingLayer.closeLayer(true);
                productCardDtos.clear();
//                RequestManager.getProductsInBackground(productCardDtoQuery, false, productCardDtoQueryCallback);
            }
        });

        return view;
    }


    private void getSearchProduct(int position) {
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
        intent.putExtra(Global.POSITION, position);
        intent.putExtra(Global.SIZE, size);
        getActivity().startService(intent);
    }


    @Override
    public void onStart() {
        super.onStart();

        viewHolder.productSearchSlidingLayer.getProductSearchForm().submit();
    }
}
