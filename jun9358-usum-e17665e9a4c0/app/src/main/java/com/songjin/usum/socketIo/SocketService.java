package com.songjin.usum.socketIo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.songjin.usum.Global;

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
                if (command.equals(Global.GET_PRODUCT)) {
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


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
