package com.lttpp.eemory.util;

import org.eclipse.jface.dialogs.IDialogSettings;

import com.lttpp.eemory.EemoryPlugin;

public class IDialogSettingsUtil {

    private static IDialogSettings settings = EemoryPlugin.getDefault().getDialogSettings();

    public static String get(final String key) {
        return settings.get(key);
    }

    public static boolean getBoolean(final String key) {
        return settings.getBoolean(key);
    }

    public static void set(final String key, final String value) {
        settings.put(key, value);
    }

    public static void set(final String key, final boolean value) {
        settings.put(key, value);
    }

    public static String get(final String sectionName, final String key) {
        IDialogSettings section = settings.getSection(sectionName);
        return section != null ? section.get(key) : null;
    }

    public static boolean getBoolean(final String sectionName, final String key) {
        IDialogSettings section = settings.getSection(sectionName);
        return section != null && section.getBoolean(key);
    }

    public static void set(final String sectionName, final String key, final String value) {
        IDialogSettings section = getOrCreateSection(sectionName);
        section.put(key, value);
    }

    public static void set(final String sectionName, final String key, final boolean value) {
        IDialogSettings section = getOrCreateSection(sectionName);
        section.put(key, value);
    }

    public static IDialogSettings getOrCreateSection(final String sectionName) {
        IDialogSettings section = settings.getSection(sectionName);
        if (section == null) {
            section = settings.addNewSection(sectionName);
        }
        return section;
    }

}
