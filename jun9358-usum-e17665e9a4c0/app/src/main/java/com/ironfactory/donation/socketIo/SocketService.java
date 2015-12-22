package com.ironfactory.donation.socketIo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ironfactory.donation.Global;
import com.ironfactory.donation.entities.UserEntity;

public class SocketService extends Service {
    private static final String TAG = "SocketService";
    private SocketIO socketIO;

    public SocketService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (socketIO == null)
            socketIO = new SocketIO(getApplicationContext());

        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND);
            if (command != null) {
                Log.d(TAG, "command = " + command);
                if (command.equals(Global.SIGN_UP)) {
                    // 회원 가입
                    processSignUp(intent);
                } else if (command.equals(Global.SIGN_IN)) {
                    // 로그인
                    processSignIn(intent);
                } else if (command.equals(Global.GET_PRODUCT)) {
                    // 제품 요청
                    processGetProduct(intent);
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }



    // TODO: 15. 11. 28.
//    private void processInsertTransaction(Intent intent) {
//        ArrayList<ProductEntity> productEntities = intent.getParcelableArrayListExtra(Global.PRODUCT);
//        ArrayList<TransactionEntity> transactionEntities = new ArrayList<>();
//
//        for (ProductEntity product :
//                productEntities) {
//            TransactionEntity transactionEntity = new TransactionEntity();
//            transactionEntity.status = Global.REGISTERED;
//            transactionEntity.donator_id = product.user_id;
//            transactionEntity.receiver_id = "";
//            transactionEntity.product_id = product.id;
//            transactionEntity.product_name = product.product_name;
//            transactionEntities.add(transactionEntity);
//        }
//
//        socketIO.insertTransaction(transactionEntities);
//    }


    // TODO: 15. 11. 25. 제품 요청
    private void processGetProduct(Intent intent) {
        String productJson = intent.getStringExtra(Global.PRODUCT);
        socketIO.getProduct(productJson);
    }


    private void processSignIn(Intent intent) {
        // 로그인
        String userId = intent.getStringExtra(Global.USER_ID);
        socketIO.signIn(userId);
    }


    private void processSignUp(Intent intent) {
        // 회원가입
        UserEntity userEntity = intent.getParcelableExtra(Global.USER);
        socketIO.signUp(userEntity);
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
