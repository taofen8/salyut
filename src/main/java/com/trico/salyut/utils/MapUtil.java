package com.trico.salyut.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtil {
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByValue()
                        .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    public static <K extends Comparable<? super K>, V > Map<K, V> sortByKey(Map<K, V> map, boolean desc) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(desc ? Map.Entry.<K, V>comparingByKey()
                        .reversed() : Map.Entry.<K, V>comparingByKey()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
}
