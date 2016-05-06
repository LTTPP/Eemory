package com.lttpp.eemory.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.lttpp.eemory.Constants;

public class StringEscapeUtil {

    /**
     * <p>
     * Escapes the characters in a String using ENML entities.
     * </p>
     *
     * @param string
     *            the String to escape, may be null
     * @return a new escaped String, null if null string input
     */
    public static String escapeEnml(final String string, final int tabWidth) {
        if (StringUtil.isNull(string)) {
            return string;
        }
        String escapedXml = StringEscapeUtils.escapeXml10(string);

        escapedXml = escapedXml.replaceAll(StringUtils.SPACE, Constants.HTML_NBSP);

        escapedXml = escapedXml.replaceAll(ConstantsUtil.TAB, StringUtils.repeat(Constants.HTML_NBSP, tabWidth));

        return escapedXml;
    }

    public static String escapeEnml(final String string) {
        return escapeEnml(string, Constants.TAB_WIDTH);
    }

}
