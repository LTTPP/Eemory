package com.prairie.eevernote.util;

import com.prairie.eevernote.Constants;

public class NumberUtil implements Constants {

	public static int number(int single) {
		return single;
	}

	public static int number(int tens, int single) {
		return tens * TEN + single;
	}

	public static int number(int hundreds, int tens, int single) {
		return hundreds * HUNDRED + tens * TEN + single;
	}

	public static int signedNumber(int single, int sign) {
		return sign * single;
	}

	public static int signedNumber(int tens, int single, int sign) {
		return sign * (tens * TEN + single);
	}

	public static int signedNumber(int hundreds, int tens, int single, int sign) {
		return sign * (hundreds * HUNDRED + tens * TEN + single);
	}

}
