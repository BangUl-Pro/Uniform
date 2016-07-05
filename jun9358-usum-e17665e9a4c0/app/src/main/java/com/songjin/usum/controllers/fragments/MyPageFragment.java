package com.songjin.usum.controllers.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.views.ProductRecyclerView;
import com.songjin.usum.controllers.views.ProfileView;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.slidingtab.SlidingBaseFragment;

import java.util.ArrayList;

public class MyPageFragment extends SlidingBaseFragment {
    private static final String TAG = "MyPageFragment";
    private UserEntity userEntity;

    private class ViewHolder {
        public ProfileView profileView;
        public ProductRecyclerView dealingProducts;

        public ViewHolder(View view) {
            profileView = (ProfileView) view.findViewById(R.id.profile_view);
            dealingProducts = (ProductRecyclerView) view.findViewById(R.id.dealing_products);

            profileView.setUserEntity(userEntity);
        }
    }

    private ViewHolder viewHolder;

    public static MyPageFragment newInstance(UserEntity userEntity) {
        MyPageFragment fragment = new MyPageFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Global.USER, userEntity);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userEntity = getArguments().getParcelable(Global.USER);
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (userEntity.userType) {
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
        switch (userEntity.userType) {
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
        viewHolder.profileView.setUserEntity(userEntity);

        RequestManager.getMyProduct(userEntity, new RequestManager.OnGetMyProduct() {
            @Override
            public void onSuccess(final ArrayList<ProductCardDto> productCardDtos) {
                setProduct(productCardDtos);
            }

            @Override
            public void onException(int code) {
                Log.d(TAG, "error");
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
