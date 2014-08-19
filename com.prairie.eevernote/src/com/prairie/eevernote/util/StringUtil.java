package com.prairie.eevernote.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.runtime.Platform;

import com.prairie.eevernote.Constants;

public final class StringUtil implements Constants {

	public static final String EMPTY = STRING_EMPTY;
	public static final String NON_BREAKING_SPACE = STRING_NON_BREAKING_SPACE;
	public static final String CRLF = org.apache.commons.lang3.StringUtils.CR + org.apache.commons.lang3.StringUtils.LF;

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

	public static String escapeEnml(String string) {
		String escapedXml = StringEscapeUtils.escapeXml10(string);

		escapedXml = escapedXml.replaceAll(NON_BREAKING_SPACE, HTML_NBSP);

		int tabWidth = Platform.getPreferencesService().getInt(PLUGIN_ORG_ECLIPSE_JDT_CORE_NAME, PLUGIN_ORG_ECLIPSE_JDT_CORE_PREF_FORMATTER_TABULATION_SIZE, ZERO, null);
		escapedXml = escapedXml.replaceAll(TAB, org.apache.commons.lang3.StringUtils.repeat(HTML_NBSP, tabWidth));

		return escapedXml;
	}
}
