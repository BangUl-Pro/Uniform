package com.songjin.usum.controllers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.views.ProductRecyclerView;
import com.songjin.usum.controllers.views.ProfileView;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.slidingtab.SlidingBaseFragment;

import java.util.ArrayList;

public class MyPageFragment extends SlidingBaseFragment {
    private static final String TAG = "MyPageFragment";
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
            case Global.GUEST:
                break;
            case Global.STUDENT:
                initProfileView();
                break;
            case Global.PARENT:
                initProfileView();
                break;
        }
    }

    @Override
    public void onPageSelected() {
        switch (AuthManager.getSignedInUserType()) {
            case Global.GUEST:
                BaseActivity.showGuestBlockedDialog();
                break;
            case Global.STUDENT:
                break;
            case Global.PARENT:
                break;
        }
    }

    private void initProfileView() {
        viewHolder.profileView.setUserEntity(Global.userEntity);

        RequestManager.getMyProduct(Global.userEntity, new RequestManager.OnGetMyProduct() {
            @Override
            public void onSuccess(final ArrayList<ProductCardDto> productCardDtos) {
                setProduct(productCardDtos);
            }

            @Override
            public void onException(int code) {
            }
        });

    }


    public void setProduct(ArrayList<ProductCardDto> productCardDtos) {
        viewHolder.dealingProducts.setProductCardDtos(productCardDtos);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_page, container, false);
        viewHolder = new ViewHolder(view);
        return view;
    }
}
