package org.lttpp.eemory.util;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

public class DateTimeUtil {

    public static String timestamp() {
        // 2014-02-21T18:35:32
        return DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date());
    }

    public static String formatCurrentTime(final String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

}
