package com.prairie.eevernote.util;


public class ColorUtil implements ConstantsUtil {

    public final static java.awt.Color AWT_EVERNOTE_GREEN = new java.awt.Color(ONE * HUNDRED + ONE * TEN + ONE, ONE * HUNDRED + EIGHT * TEN + ONE, FIVE * TEN + FOUR);
    public final static org.eclipse.swt.graphics.Color SWT_DEFAULT_COLOR = new org.eclipse.swt.graphics.Color(null, ZERO,ZERO,ZERO);

    public static String toHexCode(final int r, final int g, final int b) {
        return POUND + toHexString(r) + toHexString(g) + toHexString(b);
    }

    private static String toHexString(final int number) {
        String hex = Integer.toHexString(number).toUpperCase();
        while (hex.length() < TWO) {
            hex = ZERO + hex;
        }
        return hex;
    }

}
