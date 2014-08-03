package com.prairie.eevernote;

import java.util.Properties;

import com.prairie.eevernote.util.StringUtil;

public class EEProperties implements Constants {

	private Properties properties;
	private static EEProperties eeProperties;

	private String errorOccurred;

	private EEProperties() {
		properties = new Properties();
		try {
			properties.load(EEProperties.class.getResourceAsStream(PropertiesFile));
			properties.load(EEProperties.class.getResourceAsStream(ErrorMessageFile));
		} catch (Throwable e) {
			errorOccurred = e.getLocalizedMessage();
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

	public String getProperty(String key) {
		return StringUtil.nullOrEmptyOrBlankString(errorOccurred) ? properties.getProperty(key) : errorOccurred;
	}

	public String getProperty(String key, String... replaces) {
		String value = properties.getProperty(key);
		if (StringUtil.nullString(value)) {
			return StringUtil.STRING_EMPTY;
		}
		for (int i = ZERO; i < replaces.length; i++) {
			value = value.replace(LEFT_BRACE + (i + ONE) + RIGHT_BRACE, replaces[i]);
		}
		return StringUtil.nullOrEmptyOrBlankString(errorOccurred) ? value : errorOccurred;
	}

}
