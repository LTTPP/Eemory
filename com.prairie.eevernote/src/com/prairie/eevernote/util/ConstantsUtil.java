package com.prairie.eevernote.util;

import org.apache.commons.lang3.StringUtils;


public final class ConstantsUtil {

    // common
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String SEMICOLON = ";";
    public static final String DOT = ".";
    public static final String POUND = "#";
    public static final String STAR = "*";
    public static final String MD5 = "MD5";
    public static final String LEFT_PARENTHESIS = "(";
    public static final String RIGHT_PARENTHESIS = ")";
    public static final String LEFT_BRACE = "{";
    public static final String RIGHT_BRACE = "}";
    public static final String LESS_THAN = "<";
    public static final String GREATER_THAN = ">";
    public static final String RIGHT_ANGLE_BRACKET = GREATER_THAN;
    public static final String LEFT_ANGLE_BRACKET = LESS_THAN;
    public static final String IMG_PNG = "png";
    public static final String IMG_JPG = "jpg";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String EQUAL = "=";
    public static final String EXCLAMATION = "!";
    public static final String DOUBLE_QUOTATION = "\"";
    public static String SLASH = "/";
    public static String BACKSLASH = "\\";
    public static String QUESTION_MARK = "?";
    public static final String TAB = "\t";
    public static final String HTML_NBSP = "&nbsp;";
    public static final String LOCALHOST = "localhost";

    // XML
    public static final String XHTML_1_0_LATIN_1_ENT = "xhtml-lat1.ent";
    public static final String XHTML_1_0_SYMBOL_ENT = "xhtml-symbol.ent";
    public static final String XHTML_1_0_SPECIAL_ENT = "xhtml-special.ent";
    public static final String XML_VERSION_1_0 = "1.0";

    // mime-util
    public static final String MimeDetector = "eu.medsea.mimeutil.detector.MagicMimeMimeDetector";

    // Eclipse
    public static final String PLUGIN_ORG_ECLIPSE_JDT_CORE_NAME = "org.eclipse.jdt.core";
    public static final String PLUGIN_ORG_ECLIPSE_JDT_CORE_PREF_FORMATTER_TABULATION_SIZE = "org.eclipse.jdt.core.formatter.tabulation.size";
    
    public static String url(String host, String port, String target, boolean isHttps) {
        return (isHttps ? "https" : "http") + "://" + host + (StringUtils.isNotBlank(port) ? ":" + port : StringUtils.EMPTY) + target;
    }

}
