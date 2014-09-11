package com.prairie.eevernote;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.StringUtil;

public class EEProperties implements ConstantsUtil, Constants {

    private final Properties properties;
    private static EEProperties eeProperties;

    private String errorOccurred;

    private EEProperties() {
        properties = new Properties();
        try {
            properties.load(EEProperties.class.getResourceAsStream(PropertiesFile));
            properties.load(EEProperties.class.getResourceAsStream(ErrorMessageFile));
        } catch (Throwable e) {
            errorOccurred = ExceptionUtils.getRootCauseMessage(e);
        }
    }

    public static EEProperties getProperties() {
        if (eeProperties == null) {
            synchronized (EEProperties.class) {
                if (eeProperties == null) {
                    eeProperties = new EEProperties();
                }
            }
        }
        return eeProperties;
    }

    public String getProperty(final String key) {
        return StringUtils.isBlank(errorOccurred) ? properties.getProperty(key) : errorOccurred;
    }

    public String getProperty(final String key, final String... replaces) {
        String value = properties.getProperty(key);
        if (StringUtil.isNull(value)) {
            return StringUtils.EMPTY;
        }
        for (int i = ZERO; i < replaces.length; i++) {
            value = value.replace(LEFT_BRACE + (i + ONE) + RIGHT_BRACE, replaces[i]);
        }
        return StringUtils.isBlank(errorOccurred) ? value : errorOccurred;
    }

}
