package com.prairie.eevernote.util;


public class NumberUtil implements ConstantsUtil {

    public static int number(final int single) {
        return single;
    }

    public static int number(final int tens, final int single) {
        return tens * TEN + single;
    }

    public static int number(final int hundreds, final int tens, final int single) {
        return hundreds * HUNDRED + tens * TEN + single;
    }

    public static int signedNumber(final int single, final int sign) {
        return sign * single;
    }

    public static int signedNumber(final int tens, final int single, final int sign) {
        return sign * (tens * TEN + single);
    }

    public static int signedNumber(final int hundreds, final int tens, final int single, final int sign) {
        return sign * (hundreds * HUNDRED + tens * TEN + single);
    }

}
