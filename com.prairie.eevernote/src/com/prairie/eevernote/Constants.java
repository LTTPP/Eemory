package com.prairie.eevernote;

public interface Constants {

	// -=-=-=-=-=-=-=-=-=-=-= Meta data definition =-=-=-=-=-=-=-=-=-=-=-
	public static final String PLUGIN_ID = "com.prairie.eevernote"; //$NON-NLS-1$

	public static final String TAGS_SEPARATOR = ",";
	public static final String STRING_EMPTY = "";
	public static final String COLON = ":";
	public static final String FILENAME_DELIMITER = ".";
	public static final String MD5 = "MD5";
	public static final char ZERO_CHAR = '0';
	public static final String SimpleDateFormat = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String SimpleDateFormat2 = "yyyy-MM-dd'T'HH-mm-ss-";
	public static final String MimeDetector = "eu.medsea.mimeutil.detector.MagicMimeMimeDetector";
	public static final String PropertiesFile = "EEProperties.properties";
	public static final String LEFT_PARENTHESIS = "(";
	public static final String RIGHT_PARENTHESIS = ")";
	public static final String IMG_PNG = "png";
	// -=-=-=-=-=-=-=-=--=-=-=-=--=-=-=-=--=-=-=-=--=-=-=-=--=-=-=-=--=-

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Settings Keys =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

	public static final String SETTINGS_KEY_TOKEN = "Token";

	public static final String SETTINGS_KEY_NOTEBOOK = "Notebook";
	public static final String SETTINGS_KEY_NOTEBOOK_GUID = "Notebook Guid";
	public static final String SETTINGS_KEY_NOTEBOOK_CHECKED = "Notebook Checked";

	public static final String SETTINGS_KEY_NOTE = "Note";
	public static final String SETTINGS_KEY_NOTE_GUID = "Note Guid";
	public static final String SETTINGS_KEY_NOTE_CHECKED = "Note Checked";

	public static final String SETTINGS_KEY_TAGS = "Tags";
	public static final String SETTINGS_KEY_TAGS_CHECKED = "Tags Checked";

	public static final String SETTINGS_KEY_COMMENTS = "Comments";
	public static final String SETTINGS_KEY_COMMENTS_CHECKED = "Comments Checked";

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Strings =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

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
	public static final String EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS = "EEClipperPlugin.Configurations.Comments";
	public static final String EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED = "EEClipperPlugin.Configurations.ErrorOccurred";

	public static final int EECLIPPERPLUGIN_CONFIGURATIONS_REFRESH_ID = -143259786;
	public static final String EECLIPPERPLUGIN_CONFIGURATIONS_REFRESH = "EEClipperPlugin.Configurations.Refresh";

	public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE = "EEClipperPlugin.ActionDelegate.AddFileToEvernote.Message";
	public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_SUBTASK_MESSAGE = "EEClipperPlugin.ActionDelegate.AddFileToEvernote.Subtask.Message";
	public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE = "EEClipperPlugin.ActionDelegate.AddFileToEvernote.OutOfDateMessage";

	public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE = "EEClipperPlugin.ActionDelegate.AddSelectionToEvernote.Message";
	public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_SUBTASK_MESSAGE = "EEClipperPlugin.ActionDelegate.AddSelectionToEvernote.Subtask.Message";
	public static final String EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_OUTOFDATEMESSAGE = "EEClipperPlugin.ActionDelegate.AddSelectionToEvernote.OutOfDateMessage";

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Command IDs =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
	public static final String EEPLUGIN_COMMAND_ID_CLIP_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipToEvernote";
	public static final String EEPLUGIN_COMMAND_ID_CLIP_SELECTION_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipSelectionToEvernote";
	public static final String EEPLUGIN_COMMAND_ID_CLIP_FILE_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipFileToEvernote";
	public static final String EEPLUGIN_COMMAND_ID_CLIP_SCREENSHOT_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipScreenshotToEvernote";
	public static final String EEPLUGIN_COMMAND_ID_CONFIGURATIONS = "com.prairie.eevernote.commands.Configurations";

}
