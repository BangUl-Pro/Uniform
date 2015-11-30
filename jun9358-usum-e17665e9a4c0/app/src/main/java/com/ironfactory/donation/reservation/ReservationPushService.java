package com.ironfactory.donation.reservation;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.ironfactory.donation.Global;
import com.ironfactory.donation.HashBiMap;
import com.ironfactory.donation.constants.Category;
import com.ironfactory.donation.constants.Sex;
import com.ironfactory.donation.controllers.fragments.SettingFragment;
import com.ironfactory.donation.dtos.ProductCardDto;
import com.ironfactory.donation.entities.ProductEntity;
import com.ironfactory.donation.entities.ReservedCategoryEntity;
import com.ironfactory.donation.entities.UserEntity;
import com.ironfactory.donation.socketIo.SocketException;
import com.ironfactory.donation.socketIo.SocketService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReservationPushService extends IntentService {
    private static final String TAG = "ReservationPushService";
    private ReservationCheckThread reservationCheckThread;

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
            ArrayList<ReservedCategoryEntity> reservedCategories = SettingFragment.getReservedCategories();
            if (reservedCategories.size() == 0) {
                return;
            }

//        BaasioQuery query = new BaasioQuery();
//        query.setType(ProductEntity.COLLECTION_NAME);
//        String where = "";
            UserEntity userEntity = Global.userEntity;
            if (userEntity.id == null) {
                return;
            }
            JSONArray array = new JSONArray();
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

            Intent intent = new Intent(getApplicationContext(), SocketService.class);
            intent.putExtra(Global.COMMAND, Global.GET_PRODUCT);
            intent.putExtra(Global.PRODUCT, array.toString());
            startService(intent);


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


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND);
            if (command != null) {
                int code = intent.getIntExtra(Global.CODE, -1);
                if (command.equals(Global.GET_PRODUCT)) {
                    // 제품 요청 응답
                    processGetProduct(code, intent);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    // TODO: 15. 11. 25. 제품 요청 응답
    private void processGetProduct(int code, Intent intent) {
        if (code == SocketException.SUCCESS) {
            ArrayList<ProductCardDto> productCardDtos = intent.getParcelableArrayListExtra(Global.PRODUCT);
            HashBiMap<Integer, String> categories = Category.getHashBiMap(Sex.ALL);
            for (ProductCardDto productCardDto : productCardDtos) {
                if (productCardDto.productEntity.user_id.equals(Global.userEntity.id)) {
                    continue;
                }

                String msg = "";
                msg += categories.get(productCardDto.productEntity.category);
                msg += "에 해당하는 상품이 등록되었습니다.";
//                PushManager.sendReservationPushToMe(msg);
                Log.d(TAG, "푸시알림 구현해야함");

                SettingFragment.updateReservedCategoryTimestamp(
                        productCardDto.productEntity.school_id,
                        productCardDto.productEntity.category
                );
            }
        }
    }
}
