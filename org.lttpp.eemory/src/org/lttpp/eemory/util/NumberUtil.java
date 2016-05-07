package org.lttpp.eemory.util;

public class NumberUtil {

    public static int gtZero(final int sourceValue, final int defaultValue) {
        return sourceValue > 0 ? sourceValue : defaultValue;
    }

}
