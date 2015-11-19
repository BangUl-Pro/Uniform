package com.songjin.usum.constants;

import com.songjin.usum.HashBiMap;
import com.songjin.usum.entities.TransactionEntity;

public class Status {
    public static HashBiMap<TransactionEntity.STATUS_TYPE, String> getHashBiMap() {
        HashBiMap<TransactionEntity.STATUS_TYPE, String> hashBiMap = HashBiMap.create();
        hashBiMap.put(TransactionEntity.STATUS_TYPE.REGISTERED, "거래대기");
        hashBiMap.put(TransactionEntity.STATUS_TYPE.REQUESTED, "요청완료");
        hashBiMap.put(TransactionEntity.STATUS_TYPE.SENDED, "발송완료");
        hashBiMap.put(TransactionEntity.STATUS_TYPE.RECEIVED, "거래완료");
        return hashBiMap;
    }
}
