package com.songjin.usum.controllers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kth.baasio.Baas;
import com.kth.baasio.exception.BaasioException;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.views.ProductRecyclerView;
import com.songjin.usum.controllers.views.ProfileView;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.slidingtab.SlidingBaseFragment;

import java.util.ArrayList;
import java.util.List;

public class MyPageFragment extends SlidingBaseFragment {
    private class ViewHolder {
        public ProfileView profileView;
        public ProductRecyclerView dealingProducts;

        public ViewHolder(View view) {
            profileView = (ProfileView) view.findViewById(R.id.profile_view);
            dealingProducts = (ProductRecyclerView) view.findViewById(R.id.dealing_products);
        }
    }

    private ViewHolder viewHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (AuthManager.getSignedInUserType()) {
            case GUEST:
                break;
            case STUDENT:
                initProfileView();
                break;
            case PARENT:
                initProfileView();
                break;
        }
    }

    @Override
    public void onPageSelected() {
        switch (AuthManager.getSignedInUserType()) {
            case GUEST:
                BaseActivity.showGuestBlockedDialog();
                break;
            case STUDENT:
                break;
            case PARENT:
                break;
        }
    }

    private void initProfileView() {
        viewHolder.profileView.setUserEntity(new UserEntity(Baas.io().getSignedInUser()));
        RequestManager.getMyProductsInBackground(new RequestManager.TypedBaasioQueryCallback<ProductCardDto>() {
            @Override
            public void onResponse(List<ProductCardDto> entities) {
                viewHolder.dealingProducts.setProductCardDtos(new ArrayList<>(entities));
            }

            @Override
            public void onException(BaasioException e) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        viewHolder = new ViewHolder(view);
        return view;
    }
}
