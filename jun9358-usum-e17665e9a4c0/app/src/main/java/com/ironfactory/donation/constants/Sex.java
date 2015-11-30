package com.ironfactory.donation.constants;

import com.ironfactory.donation.HashBiMap;

public class Sex {
    public static final int ALL = 0;
    public static final int MAN = 1;
    public static final int WOMAN = 2;

    public static HashBiMap<Integer, String> getHashBiMapExceptAll() {
        HashBiMap<Integer, String> hashBiMap = HashBiMap.create();
        hashBiMap.put(MAN, "남자");
        hashBiMap.put(WOMAN, "여자");
        return hashBiMap;
    }

    public static HashBiMap<Integer, String> getHashBiMap() {
        HashBiMap<Integer, String> hashBiMap = getHashBiMapExceptAll();
        hashBiMap.put(ALL, "모두");
        return hashBiMap;
    }
}
