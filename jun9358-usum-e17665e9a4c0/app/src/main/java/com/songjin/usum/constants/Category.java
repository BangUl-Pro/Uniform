package com.songjin.usum.constants;

import com.songjin.usum.HashBiMap;

public class Category {
    public static final int ALL = 0;

    public static final int MAN_JACKET = 11;
    public static final int MAN_VEST_AND_CARDIGAN = 12;
    public static final int MAN_WINTER_SHIRT = 13;
    public static final int MAN_SUMMER_SHIRT = 14;
    public static final int MAN_WINTER_PATNTS = 15;
    public static final int MAN_SUMMER_PATNTS = 16;
    public static final int MAN_CASUAL_AND_GYM = 17;

    public static final int WOMAN_JACKET = 21;
    public static final int WOMAN_VEST_AND_CARDIGAN = 22;
    public static final int WOMAN_WINTER_BLOUSE = 23;
    public static final int WOMAN_SUMMER_BLOUSE = 24;
    public static final int WOMAN_WINTER_SKIRT_OR_PANTS = 25;
    public static final int WOMAN_SUMMER_SKIRT_OR_PANTS = 26;
    public static final int WOMAN_CASUAL_AND_GYM = 27;

    public static HashBiMap<Integer, String> getHashBiMap(int sex) {
        HashBiMap<Integer, String> hashBiMap = getHashBiMapExceptAll(sex);
        hashBiMap.put(ALL, "모두");
        return hashBiMap;
    }

    public static HashBiMap<Integer, String> getHashBiMapExceptAll(int sex) {
        HashBiMap<Integer, String> hashBiMap = HashBiMap.create();
        switch (sex) {
            case Sex.ALL:
                hashBiMap.putAll(getManCategories());
                hashBiMap.putAll(getWomanCategories());
                break;
            case Sex.MAN:
                hashBiMap.putAll(getManCategories());
                break;
            case Sex.WOMAN:
                hashBiMap.putAll(getWomanCategories());
                break;
        }

        return hashBiMap;
    }

    private static HashBiMap<Integer, String> getManCategories() {
        HashBiMap<Integer, String> manCategories = HashBiMap.create();
        manCategories.put(MAN_JACKET, "재킷");
        manCategories.put(MAN_VEST_AND_CARDIGAN, "조끼/가디건");
        manCategories.put(MAN_WINTER_SHIRT, "동복셔츠");
        manCategories.put(MAN_SUMMER_SHIRT, "하복셔츠");
        manCategories.put(MAN_WINTER_PATNTS, "동복바지");
        manCategories.put(MAN_SUMMER_PATNTS, "하복바지");
        manCategories.put(MAN_CASUAL_AND_GYM, "생활복/체육복");
        return manCategories;
    }

    private static HashBiMap<Integer, String> getWomanCategories() {
        HashBiMap<Integer, String> womanCategories = HashBiMap.create();
        womanCategories.put(WOMAN_JACKET, "재킷");
        womanCategories.put(WOMAN_VEST_AND_CARDIGAN, "조끼/가디건");
        womanCategories.put(WOMAN_WINTER_BLOUSE, "동복블라우스");
        womanCategories.put(WOMAN_SUMMER_BLOUSE, "하복블라우스");
        womanCategories.put(WOMAN_WINTER_SKIRT_OR_PANTS, "동복치마(바지)");
        womanCategories.put(WOMAN_SUMMER_SKIRT_OR_PANTS, "하복치마(바지)");
        womanCategories.put(WOMAN_CASUAL_AND_GYM, "생활복/체육복");
        return womanCategories;
    }
}
