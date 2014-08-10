package com.prairie.eevernote.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ListUtil {

	public static String[] toStringArray(List<String> list) {
		if (list == null) {
			return null;
		}
		String[] array = new String[list.size()];
		return list.toArray(array);
	}

	public static String[] toStringArray(List<?> list, ListStringizer stringizer) {
		if (list == null) {
			return null;
		}
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = stringizer.element(list.get(i));
		}
		return array;
	}

	public static Map<String, String> toStringMap(List<?> list, MapStringizer stringizer) {
		if (list == null) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < list.size(); i++) {
			map.put(stringizer.key(list.get(i)), stringizer.value(list.get(i)));
		}
		return map;
	}

	public static <T> List<T> arrayToList(T[] array) {
		List<T> l = list(array.length);
		l.addAll(Arrays.asList(array));
		return l;
	}

	public static <T> List<T> list(int initialCapacity) {
		return new ArrayList<T>(initialCapacity);
	}

	public static <T> List<T> list() {
		return new ArrayList<T>();
	}

	public static boolean nullOrEmptyList(List<?> list) {
		return list == null || list.size() == 0;
	}

	public static boolean nullList(List<?> list) {
		return list == null;
	}

}
