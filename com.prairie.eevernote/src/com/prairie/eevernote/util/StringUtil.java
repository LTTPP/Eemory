package com.prairie.eevernote.util;

import com.prairie.eevernote.Constants;

public final class StringUtil {

	public static boolean nullString(String string) {
		return string == null;
	}

	public static boolean nullOrEmptyString(String string) {
		return (string == null) || (string.length() == 0);
	}

	public static boolean nullOrEmptyOrBlankString(String string) {
		int length = (string != null) ? string.length() : 0;
		if (length > 0) {
			for (int i = 0; i < length; ++i) {
				if (!Character.isWhitespace(string.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean equalsInLogic(String str1, String str2) {
		return nullOrEmptyOrBlankString(str1) ? nullOrEmptyOrBlankString(str2) : str1.equals(str2);
	}

	public static final String STRING_EMPTY = Constants.STRING_EMPTY;

}
