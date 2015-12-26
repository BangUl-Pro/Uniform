package com.songjin.usum;

import java.util.TreeMap;

public class HashBiMap<K, V> extends TreeMap<K, V> {
    public static <K, V> HashBiMap<K, V> create() {
        return new HashBiMap<K, V>();
    }

    public HashBiMap<V, K> inverse() {
        HashBiMap<V, K> inverse = new HashBiMap<>();
        for (Entry<K, V> entry : this.entrySet()) {
            inverse.put(entry.getValue(), entry.getKey());
        }
        return inverse;
    }
}
