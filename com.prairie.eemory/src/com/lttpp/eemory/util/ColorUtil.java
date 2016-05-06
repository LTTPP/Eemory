package com.lttpp.eemory.util;

import org.eclipse.swt.SWT;


public class ColorUtil {

    public final static java.awt.Color AWT_EVERNOTE_GREEN = new java.awt.Color(32, 192, 92);
    public final static org.eclipse.swt.graphics.Color SWT_COLOR_DEFAULT = new org.eclipse.swt.graphics.Color(null, 0, 0, 0);
    public final static int SWT_COLOR_GRAY = SWT.COLOR_GRAY;

    public static String toHexCode(final int r, final int g, final int b) {
        return ConstantsUtil.POUND + toHexString(r) + toHexString(g) + toHexString(b);
    }

    private static String toHexString(final int number) {
        String hex = Integer.toHexString(number).toUpperCase();
        while (hex.length() < 2) {
            hex = 0 + hex;
        }
        return hex;
    }

}
