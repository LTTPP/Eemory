package com.prairie.eevernote;

import java.util.Properties;

import com.prairie.eevernote.util.StringUtil;

public class EEProperties {

	private Properties properties;
	private static EEProperties eeProperties;

	private String errorOccurred;

	private EEProperties() {
		properties = new Properties();
		try {
			properties.load(EEProperties.class.getResourceAsStream(Constants.PropertiesFile));
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

	public String getProperty(String key, String defaultValue) {
		return StringUtil.nullOrEmptyOrBlankString(errorOccurred) ? properties.getProperty(key, defaultValue) : errorOccurred;
	}

	public String getProperty(String key) {
		return StringUtil.nullOrEmptyOrBlankString(errorOccurred) ? properties.getProperty(key) : errorOccurred;
	}
}
