package com.songjin.usum.reservation;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.songjin.usum.Global;
import com.songjin.usum.HashBiMap;
import com.songjin.usum.constants.Category;
import com.songjin.usum.constants.Sex;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.entities.ProductEntity;
import com.songjin.usum.entities.ReservedCategoryEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.gcm.PushManager;
import com.songjin.usum.managers.RequestManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReservationPushService extends IntentService {


    private static final String TAG = "ReservationPushService";
    private ReservationCheckThread reservationCheckThread;
    private int objectIndex = 0;
    private JSONArray array;


    private RequestManager.OnGetProduct onGetProduct = new RequestManager.OnGetProduct() {
        @Override
        public void onSuccess(ArrayList<ProductCardDto> productCardDtos) {
            Log.d(TAG, "getProduct");
            HashBiMap<Integer, String> categories = Category.getHashBiMap(Sex.ALL);
            for (ProductCardDto productCardDto : productCardDtos) {
                if (productCardDto.productEntity.user_id.equals(Global.userEntity.id)) {
                    continue;
                }

                String msg = "";
                msg += categories.get(productCardDto.productEntity.category);
                msg += "에 해당하는 상품이 등록되었습니다.";
                PushManager.sendReservationPushToMe(msg);

                SettingFragment.updateReservedCategoryTimestamp(
                        productCardDto.productEntity.school_id,
                        productCardDto.productEntity.category
                );
            }

            if (array.length() > ++objectIndex) {
                try {
                    JSONObject object = array.getJSONObject(objectIndex);
                    RequestManager.getProduct(object, onGetProduct);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onException() {

        }
    };

    public ReservationPushService() {
        super("ReservationPushService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        reservationCheckThread = new ReservationCheckThread();
        reservationCheckThread.run();
    }

    private class ReservationCheckThread extends Thread {
        public void run() {
            while (true) {
                try {
                    checkRegisteredNewProduct();
                    sleep(60 * 1000);   // 60초마다
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkRegisteredNewProduct() {
        try {
            Log.d(TAG, "checkRegisteredNewProduct");
            ArrayList<ReservedCategoryEntity> reservedCategories = SettingFragment.getReservedCategories();
            if (reservedCategories == null || reservedCategories.size() == 0) {
                return;
            }

            UserEntity userEntity = Global.userEntity;
            if (userEntity == null || userEntity.id == null) {
                return;
            }
            array = new JSONArray();
            for (ReservedCategoryEntity reservedCategory : reservedCategories) {
                JSONObject object = new JSONObject();
                object.put(ProductEntity.PROPERTY_SCHOOL_ID, reservedCategory.schoolId);
                object.put(ProductEntity.PROPERTY_CATEGORY, reservedCategory.category);
                object.put(ProductEntity.PROPERTY_CREATED, reservedCategory.lastCheckedTimestamp);
                array.put(object);

//            where += "(" +
//                    ProductEntity.PROPERTY_SCHOOL_ID + "=" + reservedCategory.schoolId + " AND " +
//                    ProductEntity.PROPERTY_CATEGORY + "=" + reservedCategory.category + " AND " +
//                    ProductEntity.PROPERTY_CREATED + ">=" + reservedCategory.lastCheckedTimestamp +
//                    ") OR ";
            }
//        where = where.substring(0, where.length() - String.valueOf(" OR ").length());
//        query.setWheres(where);
//        Log.d("USUM", where);

            objectIndex = 0;
            final JSONObject object = array.getJSONObject(objectIndex);

            RequestManager.getProduct(object, onGetProduct);


//            RequestManager.getProductsInBackground(query, false, new RequestManager.TypedBaasioQueryCallback<ProductCardDto>() {
//                @Override
//                public void onResponse(List<ProductCardDto> entities) {
//                    HashBiMap<Integer, String> categories = Category.getHashBiMap(Sex.ALL);
//                    for (ProductCardDto productCardDto : entities) {
//                        if (productCardDto.productEntity.user_uuid.equals(Baas.io().getSignedInUser().getUuid().toString())) {
//                            continue;
//                        }
//
//                        String msg = "";
//                        msg += categories.get(productCardDto.productEntity.category);
//                        msg += "에 해당하는 상품이 등록되었습니다.";
//                        PushManager.sendReservationPushToMe(msg);
//
//                        SettingFragment.updateReservedCategoryTimestamp(
//                                productCardDto.productEntity.school_id,
//                                productCardDto.productEntity.category
//                        );
//                    }
//                }
//
//                @Override
//                public void onException(BaasioException e) {
//
//                }
//            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
