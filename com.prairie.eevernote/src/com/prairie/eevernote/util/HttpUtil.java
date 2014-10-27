package com.prairie.eevernote.util;

import org.apache.commons.lang3.StringUtils;

public class HttpUtil {

    public static final String LOCALHOST = "localhost";

    public static String url(final String host, final String port, final String target, final boolean isHttps) {
        return (isHttps ? "https" : "http") + "://" + host + (StringUtils.isNotBlank(port) ? ":" + port : StringUtils.EMPTY) + target;
    }

}
