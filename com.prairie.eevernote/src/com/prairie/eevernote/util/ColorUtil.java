package com.prairie.eevernote.util;

import com.prairie.eevernote.Constants;

public class ColorUtil implements Constants {

	public final static java.awt.Color AWT_EVERNOTE_GREEN = new java.awt.Color(ONE * HUNDRED + ONE * TEN + ONE, ONE * HUNDRED + EIGHT * TEN + ONE, FIVE * TEN + FOUR);
	public final static org.eclipse.swt.graphics.Color SWT_DEFAULT_COLOR = new org.eclipse.swt.graphics.Color(null, ZERO,ZERO,ZERO);

	public static String toHexCode(int r, int g, int b) {
		return POUND + toHexString(r) + toHexString(g) + toHexString(b);
	}

	private static String toHexString(int number) {
		String hex = Integer.toHexString(number).toUpperCase();
		while (hex.length() < TWO) {
			hex = ZERO + hex;
		}
		return hex;
	}

}
