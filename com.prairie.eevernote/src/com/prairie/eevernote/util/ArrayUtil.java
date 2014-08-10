package com.prairie.eevernote.util;

import java.util.List;

public final class ArrayUtil {

	public static String[] stringArray() {
		return new String[0];
	}

	public static Object[] array() {
		return new Object[0];
	}

	public static Object[] array(Object... a) {
		return new Object[] { a };
	}

	public static Object[] array(List<?> vector) {
		if (vector == null) {
			return new Object[0];
		}
		return vector.toArray();
	}

	public static Object[] prepend(Object a, Object[] in) {
		Object[] out = new Object[in.length + 1];
		out[0] = a;
		System.arraycopy(in, 0, out, 1, in.length);
		return out;
	}

	public static Object[] append(Object[] in, Object a) {
		Object[] out = new Object[in.length + 1];
		out[in.length] = a;
		System.arraycopy(in, 0, out, 0, in.length);
		return out;
	}

	public static boolean nullOrEmptyArray(Object[] array) {
		return (array == null) || (array.length == 0);
	}

	public static boolean nullOrEmptyIntArray(int[] array) {
		return (array == null) || (array.length == 0);
	}

	public static void reverse(Object[] objects) {
		int midpoint = objects.length / 2;
		for (int i = 0; i < midpoint; ++i) {
			int oppositeIndex = objects.length - 1 - i;
			Object temp = objects[i];
			objects[i] = objects[oppositeIndex];
			objects[oppositeIndex] = temp;
		}
	}
}
