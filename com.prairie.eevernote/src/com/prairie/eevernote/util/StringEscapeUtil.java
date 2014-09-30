package com.prairie.eevernote.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;

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
    public static String escapeEnml(final String string) {
        if (StringUtil.isNull(string)) {
            return string;
        }
        String escapedXml = StringEscapeUtils.escapeXml10(string);

        escapedXml = escapedXml.replaceAll(StringUtils.SPACE, ConstantsUtil.HTML_NBSP);

        int tabWidth = Platform.getPreferencesService().getInt(ConstantsUtil.PLUGIN_ORG_ECLIPSE_JDT_CORE_NAME, ConstantsUtil.PLUGIN_ORG_ECLIPSE_JDT_CORE_PREF_FORMATTER_TABULATION_SIZE, 0, null);
        escapedXml = escapedXml.replaceAll(ConstantsUtil.TAB, org.apache.commons.lang3.StringUtils.repeat(ConstantsUtil.HTML_NBSP, tabWidth));

        return escapedXml;
    }

}
