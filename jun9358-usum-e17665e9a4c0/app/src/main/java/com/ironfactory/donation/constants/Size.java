package com.ironfactory.donation.constants;


import com.ironfactory.donation.HashBiMap;

public class Size {
    public static final int ALL = 0;
    private static final int[] MAN_TOP_AVAILABLE_SIZES = {79, 82, 85, 88, 91, 94, 97, 100, 103, 106, 109};
    private static final int[] MAN_BOTTOM_AVAILABLE_SIZES = {62, 65, 68, 71, 77, 80, 83, 86, 89, 92, 95};
    private static final int[] WOMAN_TOP_AVAILABLE_SIZES = {79, 82, 85, 88, 91, 94, 97, 100, 103, 106, 109};
    private static final int[] WOMAN_BOTTOM_AVAILABLE_SIZES = {62, 65, 68, 71, 77, 80, 83, 86, 89, 92, 95};

    public static HashBiMap<Integer, String> getHashBiMap(int category) {
        HashBiMap<Integer, String> hashBiMap = getHashBiMapExceptAll(category);
        hashBiMap.put(ALL, "모두");
        return hashBiMap;
    }

    public static HashBiMap<Integer, String> getHashBiMapExceptAll(int category) {
        HashBiMap<Integer, String> hashBiMap = HashBiMap.create();

        int[] availableSizes = getAvailableSizes(category);
        for (int availableSize : availableSizes) {
            hashBiMap.put(availableSize, cmToString(availableSize));
        }

        return hashBiMap;
    }

    private static int[] getAvailableSizes(int category) {
        int[] availableSizes = {};
        switch (category) {
            case Category.MAN_JACKET:
            case Category.MAN_VEST_AND_CARDIGAN:
            case Category.MAN_WINTER_SHIRT:
            case Category.MAN_SUMMER_SHIRT:
                availableSizes = MAN_TOP_AVAILABLE_SIZES;
                break;
            case Category.MAN_WINTER_PATNTS:
            case Category.MAN_SUMMER_PATNTS:
            case Category.MAN_CASUAL_AND_GYM:
                availableSizes = MAN_BOTTOM_AVAILABLE_SIZES;
                break;
            case Category.WOMAN_JACKET:
            case Category.WOMAN_VEST_AND_CARDIGAN:
            case Category.WOMAN_WINTER_BLOUSE:
            case Category.WOMAN_SUMMER_BLOUSE:
                availableSizes = WOMAN_TOP_AVAILABLE_SIZES;
                break;
            case Category.WOMAN_WINTER_SKIRT_OR_PANTS:
            case Category.WOMAN_SUMMER_SKIRT_OR_PANTS:
            case Category.WOMAN_CASUAL_AND_GYM:
                availableSizes = WOMAN_BOTTOM_AVAILABLE_SIZES;
                break;
        }

        return availableSizes;
    }

    private static String cmToString(int cm) {
        return cm + "cm";
    }
}
