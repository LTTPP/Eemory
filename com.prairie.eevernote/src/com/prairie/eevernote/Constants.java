package com.prairie.eevernote;

public interface Constants {

    public static final String PLUGIN_ID = "com.prairie.eevernote"; //$NON-NLS-1$

    public static final String FileNamePartSimpleDateFormat = "yyyy-MM-dd'T'HH-mm-ss-";
    public static final String PropertiesFile = "EEProperties.properties";
    public static final String ErrorMessageFile = "ErrorMessage.properties";

    // Settings Keys

    public static final String SETTINGS_KEY_TOKEN = "token";

    public static final String SETTINGS_SECTION_NOTEBOOK = "notebook";
    public static final String SETTINGS_SECTION_NOTE = "note";
    public static final String SETTINGS_SECTION_TAGS = "tags";
    public static final String SETTINGS_SECTION_COMMENTS = "comments";

    public static final String SETTINGS_KEY_NAME = "name";
    public static final String SETTINGS_KEY_GUID = "guid";
    public static final String SETTINGS_KEY_CHECKED = "isChecked";
    public static final String SETTINGS_KEY_UUID = "uuid";

    // Command IDs
    public static final String EEPLUGIN_COMMAND_ID_CLIP_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipToEvernote";
    public static final String EEPLUGIN_COMMAND_ID_CLIP_SELECTION_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipSelectionToEvernote";
    public static final String EEPLUGIN_COMMAND_ID_CLIP_FILE_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipFileToEvernote";
    public static final String EEPLUGIN_COMMAND_ID_CLIP_SCREENSHOT_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipScreenshotToEvernote";
    public static final String EEPLUGIN_COMMAND_ID_CONFIGURATIONS = "com.prairie.eevernote.commands.Configurations";

    public static final int EEPLUGIN_SCREENSHOT_HINT_HEIGHT = 18;
    public static final int EEPLUGIN_SCREENSHOT_HINT_WIDTH = 235;
    public static float EEPLUGIN_SCREENSHOT_HINT_SCALEFACTOR = 0.3F;
    public static final int EEPLUGIN_SCREENSHOT_HINT_TEXT_START_X = 5;
    public static final int EEPLUGIN_SCREENSHOT_HINT_TEXT_START_Y = -6;

    public static final float EEPLUGIN_SCREENSHOT_MASK_FULLSCREEN_SCALEFACTOR = 0.7F;

    // Messages

    public static final String EECLIPPERPLUGIN_EECLIPPERIMPL_EXCEPTION_MESSAGE = "EEClipperPlugin.EEClipperImpl.Exception.Message";

    public static final String EECLIPPERPLUGIN_CONFIGURATIONSDIALOG_SHELL_TITLE = "EEClipperPlugin.ConfigurationsDialog.Shell.Title";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONSDIALOG_TITLE = "EEClipperPlugin.ConfigurationsDialog.Title";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONSDIALOG_MESSAGE = "EEClipperPlugin.ConfigurationsDialog.Message";
    public static final String EECLIPPERPLUGIN_HOTINPUTDIALOG_SHELL_TITLE = "EEClipperPlugin.HotInputDialog.Shell.Title";
    public static final int EECLIPPERPLUGIN_HOTINPUTDIALOG_SHOULD_NOT_SHOW_ID = -1;

    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_OAUTH = "EEClipperPlugin.Configurations.OAuth";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN = "EEClipperPlugin.Configurations.Token";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_EVERNOTEPREFERENCES = "EEClipperPlugin.Configurations.EvernotePreferences";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK = "EEClipperPlugin.Configurations.Notebook";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_NOTE = "EEClipperPlugin.Configurations.Note";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_TAGS = "EEClipperPlugin.Configurations.Tags";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE = "EEClipperPlugin.Configurations.Tags.HintMessage";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE = "EEClipperPlugin.Configurations.Note.HintMessage";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK_HINTMESSAGE = "EEClipperPlugin.Configurations.Notebook.HintMessage";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS = "EEClipperPlugin.Configurations.Comments";
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED = "EEClipperPlugin.Configurations.ErrorOccurred";

    public static final int EECLIPPERPLUGIN_CONFIGURATIONS_REFRESH_ID = -143259786; // random number
    public static final String EECLIPPERPLUGIN_CONFIGURATIONS_REFRESH = "EEClipperPlugin.Configurations.Refresh";

    public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE = "EEClipperPlugin.ActionDelegate.AddFileToEvernote.Message";
    public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_SUBTASK_MESSAGE = "EEClipperPlugin.ActionDelegate.AddFileToEvernote.Subtask.Message";
    public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE = "EEClipperPlugin.ActionDelegate.AddFileToEvernote.OutOfDateMessage";

    public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE = "EEClipperPlugin.ActionDelegate.AddSelectionToEvernote.Message";
    public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_SUBTASK_MESSAGE = "EEClipperPlugin.ActionDelegate.AddSelectionToEvernote.Subtask.Message";
    public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_OUTOFDATEMESSAGE = "EEClipperPlugin.ActionDelegate.AddSelectionToEvernote.OutOfDateMessage";

    public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_CLIPSCREENSHOTTOEVERNOTE_MESSAGE = "EEClipperPlugin.ActionDelegate.ClipScreenshotToEvernote.Message";
    public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_CLIPSCREENSHOTTOEVERNOTE_SUBTASK_MESSAGE = "EEClipperPlugin.ActionDelegate.ClipScreenshotToEvernote.Subtask.Message";
    public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_CLIPSCREENSHOTTOEVERNOTE_OUTOFDATEMESSAGE = "EEClipperPlugin.ActionDelegate.ClipScreenshotToEvernote.OutOfDateMessage";
    public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_CLIPSCREENSHOTTOEVERNOTE_HINT = "EEClipperPlugin.ActionDelegate.ClipScreenshotToEvernote.Hint";

    // DOM
    public static final String DOM_INDEX_SIZE_ERR_MSG1 = "offset is negative or larger than the length of this String object, or count is negative.";
    public static final String DOM_INDEX_SIZE_ERR_MSG0 = "offset is negative or larger than the length of this String object";

}
