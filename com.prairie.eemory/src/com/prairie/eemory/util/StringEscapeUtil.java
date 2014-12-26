package com.prairie.eemory.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.prairie.eemory.Constants;

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

        escapedXml = escapedXml.replaceAll(StringUtils.SPACE, Constants.HTML_NBSP);

        // Note: the tab width justification should depend on various develop platforms, e.g. JDT, CDT etc., but here just make it simple using JDT for all environments even JDT not installed.
        int tabWidth = Platform.getPreferencesService().getInt(Constants.PLUGIN_ORG_ECLIPSE_JDT_CORE_NAME, Constants.PLUGIN_ORG_ECLIPSE_JDT_CORE_PREF_FORMATTER_TABULATION_SIZE, 4, null);
        escapedXml = escapedXml.replaceAll(ConstantsUtil.TAB, org.apache.commons.lang3.StringUtils.repeat(Constants.HTML_NBSP, tabWidth));

        return escapedXml;
    }

}
