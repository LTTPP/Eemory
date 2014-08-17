package com.prairie.eevernote;

public interface Constants {

	// -=-=-=-=-=-=-=-=-=-=-= Meta data definition =-=-=-=-=-=-=-=-=-=-=-
	public static final String PLUGIN_ID = "com.prairie.eevernote"; //$NON-NLS-1$

	public static final String TAGS_SEPARATOR = ",";
	public static final String COMMA = ",";
	public static final String STRING_EMPTY = "";
	public static final String STRING_NON_BREAKING_SPACE = " ";
	public static final String TAB = "\t";
	public static final String HTML_NBSP = "&nbsp;";
	public static final String COLON = ":";
	public static final String FILENAME_DELIMITER = ".";
	public static final String POUND = "#";
	public static final String MD5 = "MD5";
	public static final String SimpleDateFormat = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String SimpleDateFormat2 = "yyyy-MM-dd'T'HH-mm-ss-";
	public static final String MimeDetector = "eu.medsea.mimeutil.detector.MagicMimeMimeDetector";
	public static final String PropertiesFile = "EEProperties.properties";
	public static final String ErrorMessageFile = "ErrorMessage.properties";
	public static final String LEFT_PARENTHESIS = "(";
	public static final String RIGHT_PARENTHESIS = ")";
	public static final String LEFT_BRACE = "{";
	public static final String RIGHT_BRACE = "}";
	public static final String LESS_THAN = "<";
	public static final String GREATER_THAN = ">";
	public static final String IMG_PNG = "png";
	public static final int ZERO = 0;
	public static final int ONE = 1;
	public static final int TWO = 2;
	public static final int THREE = 3;
	public static final int FOUR = 4;
	public static final int FIVE = 5;
	public static final int SIX = 6;
	public static final int SEVEN = 7;
	public static final int EIGHT = 8;
	public static final int NINE = 9;
	public static final int TEN = 10;
	public static final int HUNDRED = 100;
	public static final int NEGATIVE = -1;
	// -=-=-=-=-=-=-=-=--=-=-=-=--=-=-=-=--=-=-=-=--=-=-=-=--=-=-=-=--=-

	public static final String PLUGIN_ORG_ECLIPSE_JDT_CORE_NAME = "org.eclipse.jdt.core";
	public static final String PLUGIN_ORG_ECLIPSE_JDT_CORE_PREF_FORMATTER_TABULATION_SIZE = "org.eclipse.jdt.core.formatter.tabulation.size";

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Settings Keys =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

	public static final String SETTINGS_KEY_TOKEN = "token";

	public static final String SETTINGS_SECTION_NOTEBOOK = "notebook";
	public static final String SETTINGS_SECTION_NOTE = "note";
	public static final String SETTINGS_SECTION_TAGS = "tags";
	public static final String SETTINGS_SECTION_COMMENTS = "comments";

	public static final String SETTINGS_KEY_NAME = "name";
	public static final String SETTINGS_KEY_GUID = "guid";
	public static final String SETTINGS_KEY_CHECKED = "isChecked";

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

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Command IDs =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
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

	// ENML

	public static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public static final String DOCTYPE_DECLARATION = "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">";

	public static final String NOTE_START = "<en-note>";

	public static final String ENML_DTD = "enml2.dtd";
	public static final String XHTML_1_0_LATIN_1_ENT = "xhtml-lat1.ent";
	public static final String XHTML_1_0_SYMBOL_ENT = "xhtml-symbol.ent";
	public static final String XHTML_1_0_SPECIAL_ENT = "xhtml-special.ent";

	public static final String ENML_DTD_LOCATION = "dtd/enml2.dtd";
	public static final String XHTML_1_0_LATIN_1_ENT_LOCATION = "dtd/xhtml-lat1.ent";
	public static final String XHTML_1_0_SYMBOL_ENT_LOCATION = "dtd/xhtml-symbol.ent";
	public static final String XHTML_1_0_SPECIAL_ENT_LOCATION = "dtd/xhtml-special.ent";

}
