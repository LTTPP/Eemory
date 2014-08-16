package com.prairie.eevernote.util;

import org.eclipse.jface.dialogs.IDialogSettings;

import com.prairie.eevernote.EEPlugin;

public class IDialogSettingsUtil {

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

	public static String get(String sectionName, String key) {
		IDialogSettings section = settings.getSection(sectionName);
		return section != null ? section.get(key) : null;
	}

	public static boolean getBoolean(String sectionName, String key) {
		IDialogSettings section = settings.getSection(sectionName);
		return section != null && section.getBoolean(key);
	}

	public static void set(String sectionName, String key, String value) {
		IDialogSettings section = getOrCreateSection(sectionName);
		section.put(key, value);
	}

	public static void set(String sectionName, String key, boolean value) {
		IDialogSettings section = getOrCreateSection(sectionName);
		section.put(key, value);
	}

	public static IDialogSettings getOrCreateSection(String sectionName) {
		IDialogSettings section = settings.getSection(sectionName);
		if (section == null) {
			section = settings.addNewSection(sectionName);
		}
		return section;
	}

}
