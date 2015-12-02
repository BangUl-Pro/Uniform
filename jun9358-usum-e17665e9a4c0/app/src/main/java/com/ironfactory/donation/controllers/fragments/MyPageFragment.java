package com.ironfactory.donation.controllers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.activities.BaseActivity;
import com.ironfactory.donation.controllers.views.ProductRecyclerView;
import com.ironfactory.donation.controllers.views.ProfileView;
import com.ironfactory.donation.dtos.ProductCardDto;
import com.ironfactory.donation.entities.TransactionEntity;
import com.ironfactory.donation.managers.AuthManager;
import com.ironfactory.donation.slidingtab.SlidingBaseFragment;
import com.ironfactory.donation.socketIo.SocketService;

import java.util.ArrayList;

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

        Intent intent = new Intent(getActivity(), SocketService.class);
        intent.putExtra(Global.COMMAND, Global.GET_MY_PRODUCT);
        intent.putExtra(TransactionEntity.PROPERTY_DONATOR_UUID, Global.userEntity.id);
        intent.putExtra(TransactionEntity.PROPERTY_RECEIVER_UUID, Global.userEntity.id);
        getActivity().startService(intent);

//        RequestManager.getMyProductsInBackground(new RequestManager.TypedBaasioQueryCallback<ProductCardDto>() {
//            @Override
//            public void onResponse(List<ProductCardDto> entities) {
//                viewHolder.dealingProducts.setProductCardDtos(new ArrayList<>(entities));
//            }
//
//            @Override
//            public void onException(BaasioException e) {
//
//            }
//        });
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
