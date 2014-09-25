package com.prairie.eevernote.util;

public class HTMLUtil {

    public static final String FONT_STYLE_NORMAL = "normal";
    public static final String FONT_STYLE_BOLD = "bold";
    public static final String FONT_STYLE_ITALIC = "italic";
    public static final String FONT_STYLE_BOLD_ITALIC = "bold-italic";

    public static String hyperlink(final String label, final String hyperlink) {
        return "<a href=\"" + hyperlink + "\">" + label + "</a>";
    }

}
