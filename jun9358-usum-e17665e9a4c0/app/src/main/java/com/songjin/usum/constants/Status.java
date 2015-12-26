package com.songjin.usum.constants;

import com.songjin.usum.Global;
import com.songjin.usum.HashBiMap;

public class Status {
    public static HashBiMap<Integer, String> getHashBiMap() {
        HashBiMap<Integer, String> hashBiMap = HashBiMap.create();
        hashBiMap.put(Global.REGISTERED, "거래대기");
        hashBiMap.put(Global.REQUESTED, "요청완료");
        hashBiMap.put(Global.SENDED, "발송완료");
        hashBiMap.put(Global.RECEIVED, "거래완료");
        return hashBiMap;
    }
}
