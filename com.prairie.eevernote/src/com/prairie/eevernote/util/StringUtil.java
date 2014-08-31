package com.prairie.eevernote.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;

/**
 * The StringUtil class defines String related utility.
 *
 * @author Liu, Jianwei
 *
 */
public final class StringUtil implements ConstantsUtil {

    public static final String CRLF = org.apache.commons.lang3.StringUtils.CR + org.apache.commons.lang3.StringUtils.LF;

    /**
     * <p>
     * Checks if a String is null. This has same effect to
     * {@code string == null}.
     * </p>
     *
     * <pre>
     * StringUtil.isNull(null)      = true
     * StringUtil.isNull("")        = true
     * StringUtil.isNull(" ")       = false
     * StringUtil.isNull("abc")     = false
     * </pre>
     *
     * @param string
     *            the String to check
     * @return {@code true} if the String is null
     */
    public static boolean isNull(final String string) {
        return string == null;
    }

    /**
     * <p>
     * Checks if a String is blank (whitespace only), empty ("") or null.
     * </p>
     *
     * <pre>
     * StringUtil.isNullOrEmptyOrBlank(null)      = true
     * StringUtil.isNullOrEmptyOrBlank("")        = true
     * StringUtil.isNullOrEmptyOrBlank(" ")       = false
     * StringUtil.isNullOrEmptyOrBlank("   ")       = false
     * StringUtil.isNullOrEmptyOrBlank("abc")     = false
     * StringUtil.isNullOrEmptyOrBlank("  abc  ") = false
     * </pre>
     *
     * @param string
     *            the String to check, may be null
     * @return {@code true} if the String is blank, empty or null
     */
    public static boolean isNullOrEmptyOrBlank(final String string) {
        int length = string != null ? string.length() : 0;
        if (length > 0) {
            for (int i = 0; i < length; ++i) {
                if (!Character.isWhitespace(string.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * <p>
     * Compares two strings given. The result is true if the two strings are
     * both null, empty or blank, or two strings that represent the same
     * sequence of characters, i.e. {@code str1.equals(str2)}.
     * </p>
     *
     * <pre>
     * StringUtil.equalsInLogic(null, null)   = true
     * StringUtil.equalsInLogic(null, "")  = true
     * StringUtil.equalsInLogic(null, " ")  = true
     * StringUtil.equalsInLogic("abc", "abc") = true
     * StringUtil.equalsIgnoreCase("abc", "ABC") = false
     * StringUtil.equalsIgnoreCase("abc", "abd") = false
     * </pre>
     *
     * @param str1
     *            the first CharSequence, may be null
     * @param str2
     *            the second CharSequence, may be null
     * @return true if the given two strings represent a logic equivalent, false
     *         otherwise
     */
    public static boolean equalsInLogic(final String str1, final String str2) {
        return isNullOrEmptyOrBlank(str1) ? isNullOrEmptyOrBlank(str2) : str1.equals(str2);
    }

    /**
     * <p>
     * Escapes the characters in a String using ENML entities.
     * </p>
     *
     * @param string
     *            the String to escape, may be null
     * @return a new escaped String, null if null string input
     */
    public static String escapeEnml(final String string) {
        if (isNull(string)) {
            return string;
        }
        String escapedXml = StringEscapeUtils.escapeXml10(string);

        escapedXml = escapedXml.replaceAll(StringUtils.SPACE, HTML_NBSP);

        int tabWidth = Platform.getPreferencesService().getInt(PLUGIN_ORG_ECLIPSE_JDT_CORE_NAME, PLUGIN_ORG_ECLIPSE_JDT_CORE_PREF_FORMATTER_TABULATION_SIZE, ZERO, null);
        escapedXml = escapedXml.replaceAll(TAB, org.apache.commons.lang3.StringUtils.repeat(HTML_NBSP, tabWidth));

        return escapedXml;
    }

    /**
     * <p>
     * Search the source string for the first substring that matched regular
     * expression.
     * </p>
     *
     * @param source
     *            The character sequence to be searched
     * @param regex
     *            The regular expression to be searched for
     *
     * @return the end offset of matched subsequence, -1 if no subsequence of
     *         the input sequence matches this matcher's pattern
     */
    public static int find(final String source, final String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);

        boolean find = matcher.find();

        return find ? matcher.end() : NEGATIVE;
    }
}
