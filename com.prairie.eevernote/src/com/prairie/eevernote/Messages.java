package com.prairie.eevernote;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.StringUtil;

public class Messages {

    private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private Messages() {

    }

    public static String getString(final String key) {
        if (StringUtil.isNull(key)) {
            return StringUtils.EMPTY;
        }
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    public static String getString(final String key, final String... replacements) {
        String value = getString(key);
        for (int i = ConstantsUtil.ZERO; i < replacements.length; i++) {
            value = value.replace(ConstantsUtil.LEFT_BRACE + (i + ConstantsUtil.ONE) + ConstantsUtil.RIGHT_BRACE, replacements[i]);
        }
        return value;
    }

}
