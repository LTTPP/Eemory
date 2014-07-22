package com.prairie.eevernote.util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public final class MapUtil {

	public static <K, V> Map<K, V> map() {
		return new HashMap<K, V>();
	}

	public static <K, V> Map<K, V> map(int initialCapacity) {
		return new HashMap<K, V>(initialCapacity);
	}

	public static <K, V> Map<K, V> map(Map<? extends K, ? extends V> source) {
		return new HashMap<K, V>(source);
	}

	public static <K, V> ConcurrentHashMap<K, V> concurrentMap() {
		return new ConcurrentHashMap<K, V>();
	}

	public static <K, V> SortedMap<K, V> sortedMap() {
		return new TreeMap<K, V>();
	}

	public static <K, V> SortedMap<K, V> sortedMap(Comparator<K> comparator) {
		return new TreeMap<K, V>(comparator);
	}

	public static <K, V> SortedMap<K, V> sortedMap(SortedMap<? extends K, ? extends V> source) {
		return new TreeMap<K, V>(source);
	}

	public static boolean nullOrEmptyMap(Map<?, ?> map) {
		return (map == null) || (map.isEmpty());
	}

}
