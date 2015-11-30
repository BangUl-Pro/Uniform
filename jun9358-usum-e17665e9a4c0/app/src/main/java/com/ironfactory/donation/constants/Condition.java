package com.ironfactory.donation.constants;

import com.ironfactory.donation.HashBiMap;

public class Condition {
    public static final int ALL = 0;
    public static final int S = 10;
    public static final int A = 20;
    public static final int B = 30;
    public static final int C = 40;

    public static HashBiMap<Integer, String> getHashBiMapExceptAll() {
        HashBiMap<Integer, String> hashBiMap = new HashBiMap<>();
        hashBiMap.put(S, "S등급");
        hashBiMap.put(A, "A등급");
        hashBiMap.put(B, "B등급");
        hashBiMap.put(C, "C등급");

        return hashBiMap;
    }

    public static HashBiMap<Integer, String> getHashBiMap() {
        HashBiMap<Integer, String> hashBiMap = getHashBiMapExceptAll();
        hashBiMap.put(ALL, "모두");
        return hashBiMap;
    }
}
