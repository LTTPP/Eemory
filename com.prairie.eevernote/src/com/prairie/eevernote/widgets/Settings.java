package com.prairie.eevernote.widgets;

import org.eclipse.jface.dialogs.IDialogSettings;

import com.prairie.eevernote.EEPlugin;

public class Settings {

	private static IDialogSettings settings = EEPlugin.getDefault().getDialogSettings();

	public static String get(String key) {
		return settings.get(key);
	}

	public static boolean getBoolean(String key) {
		return settings.getBoolean(key);
	}

	public static void set(String key, String value) {
		settings.put(key, value);
	}

	public static void set(String key, boolean value) {
		settings.put(key, value);
	}

}
