package com.lttpp.eemory.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ObjectUtils;

public final class MapUtil {

    public static <K, V> Map<K, V> map() {
        return new HashMap<K, V>();
    }

    public static <K, V> LinkedHashMap<K, V> orderedMap() {
        return new LinkedHashMap<K, V>();
    }

    public static <K, V> Map<K, V> map(final int initialCapacity) {
        return new HashMap<K, V>(initialCapacity);
    }

    public static <K, V> Map<K, V> map(final Map<? extends K, ? extends V> source) {
        return new HashMap<K, V>(source);
    }

    public static <K, V> Map<K, V> map(final K k, final V v) {
        Map<K, V> map = MapUtil.map();
        map.put(k, v);
        return map;
    }

    public static <K, V> ConcurrentHashMap<K, V> concurrentMap() {
        return new ConcurrentHashMap<K, V>();
    }

    public static <K, V> SortedMap<K, V> sortedMap() {
        return new TreeMap<K, V>();
    }

    public static <K, V> SortedMap<K, V> sortedMap(final Comparator<K> comparator) {
        return new TreeMap<K, V>(comparator);
    }

    public static <K, V> SortedMap<K, V> sortedMap(final SortedMap<? extends K, ? extends V> source) {
        return new TreeMap<K, V>(source);
    }

    public static <K, V> boolean isEmpty(final Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> boolean isNull(final Map<K, V> map) {
        return map == null;
    }

    public static boolean isEqualList(final Map<?, ?> map1, final Map<?, ?> map2) {
        if (map1 == map2) {
            return true;
        }
        if (isNull(map1) || isNull(map2)) {
            return false;
        }
        if (map1.size() != map2.size()) {
            return false;
        }
        for (Entry<?, ?> e : map1.entrySet()) {
            if (!ObjectUtil.isEqualObject(map1.get(e.getKey()), map2.get(e.getKey()))) {
                return false;
            }
        }
        return true;
    }

    public static <K, V> Map<K, V> cloneMap(final HashMap<K, V> source) {
        return cloneMap(source, false);
    }

    public static <K, V> Map<K, V> cloneMap(final HashMap<K, V> source, final boolean deep) {
        if (!deep) {
            return ObjectUtils.clone(source);
        }
        if (isNull(source)) {
            return null;
        }
        Map<K, V> map = map(source.size());
        for (Entry<K, V> e : source.entrySet()) {
            map.put(ObjectUtil.cloneObject(e.getKey(), deep), ObjectUtil.cloneObject(e.getValue(), deep));
        }
        return map;
    }

    /**
     * The first key that value is matched, preferred to be used when value is
     * unique.
     */
    public static <K, V> K getKeyByValue(final Map<K, V> map, final V value) {
        for (K k : map.keySet()) {
            if (map.get(k).equals(value)) {
                return k;
            }
        }
        return null;
    }

}
