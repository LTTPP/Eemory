package com.prairie.eevernote;

import com.prairie.eevernote.util.ConstantsUtil;

public interface Constants {

    public static final String PLUGIN_ID = "com.prairie.eevernote";
    public static final String PLUGIN_COMMAND_PARAM_ID = "com.prairie.eevernote.command.parameter";

    public static final String CONSUMER_KEY = "eevernote";
    public static final String CONSUMER_SECRET = "4a4879fd4c671f5b";

    // Settings Keys
    public static final String PLUGIN_SETTINGS_KEY_TOKEN = "token";
    public static final String PLUGIN_SETTINGS_SECTION_NOTEBOOK = "notebook";
    public static final String PLUGIN_SETTINGS_SECTION_NOTE = "note";
    public static final String PLUGIN_SETTINGS_SECTION_TAGS = "tags";
    public static final String PLUGIN_SETTINGS_SECTION_COMMENTS = "comments";
    public static final String PLUGIN_SETTINGS_KEY_NAME = "name";
    public static final String PLUGIN_SETTINGS_KEY_GUID = "guid";
    public static final String PLUGIN_SETTINGS_KEY_CHECKED = "isChecked";
    public static final String PLUGIN_SETTINGS_KEY_UUID = "uuid";

    // Command IDs
    public static final String PLUGIN_COMMAND_ID_CLIP_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipToEvernote";
    public static final String PLUGIN_COMMAND_ID_CLIP_SELECTION_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipSelectionToEvernote";
    public static final String PLUGIN_COMMAND_ID_CLIP_FILE_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipFileToEvernote";
    public static final String PLUGIN_COMMAND_ID_CLIP_SCREENSHOT_TO_EVERNOTE = "com.prairie.eevernote.commands.ClipScreenshotToEvernote";
    public static final String PLUGIN_COMMAND_ID_CONFIGURATIONS = "com.prairie.eevernote.commands.Configurations";

    // Screen shot
    public static final int PLUGIN_SCREENSHOT_HINT_HEIGHT = 18;
    public static final int PLUGIN_SCREENSHOT_HINT_WIDTH = 260;
    public static final float PLUGIN_SCREENSHOT_HINT_SCALEFACTOR = 0.3F;
    public static final int PLUGIN_SCREENSHOT_HINT_TEXT_START_X = 5;
    public static final int PLUGIN_SCREENSHOT_HINT_TEXT_START_Y = -6;
    public static final float PLUGIN_SCREENSHOT_MASK_FULLSCREEN_SCALEFACTOR = 0.7F;

    // ENML
    public static final String XHTML_1_0_LATIN_1_ENT_LOCATION = "dtd/xhtml-lat1.ent";
    public static final String XHTML_1_0_SYMBOL_ENT_LOCATION = "dtd/xhtml-symbol.ent";
    public static final String XHTML_1_0_SPECIAL_ENT_LOCATION = "dtd/xhtml-special.ent";

    public static final String ENML_DOCTYPE_DECLARATION_SYSTEM_ID = "http://xml.evernote.com/pub/enml2.dtd";
    public static final String ENML_DTD = "enml2.dtd";
    public static final String ENML_DTD_LOCATION = "dtd/enml2.dtd";

    public static final String ENML_VALUE_PT = "pt";
    public static final String ENML_VALUE_FONT_SIZE = "font-size:";
    public static final String ENML_ATTR_STYLE = "style";
    public static final String ENML_TAG_BR = "br";
    public static final String ENML_TAG_SPAN = "span";
    public static final String ENML_ATTR_HASH = "hash";
    public static final String ENML_ATTR_TYPE = "type";
    public static final String ENML_TAG_EN_MEDIA = "en-media";
    public static final String ENML_TAG_EN_NOTE = "en-note";
    public static final String ENML_TAG_DIV = "div";
    public static final String ENML_ATTR_SIZE = "size";
    public static final String ENML_ATTR_COLOR = "color";
    public static final String ENML_ATTR_FACE = "face";
    public static final String ENML_ATTR_FONT = "font";
    public static final String ENML_TAG_ITALIC = "i";
    public static final String ENML_TAG_BOLD = "b";
    public static final String DOM_NODE_NAME_TEXT = "#text";
    public static final String ENML_DOCTYPE_PUBLIC = "PUBLIC";
    public static final String ENML_DOCTYPE_SYSTEM = "SYSTEM";
    public static final String ENML_DOCTYPE = "DOCTYPE";
    /*
     * Regex: assume > does not appear in the value for the attributes, and the
     * XML is valid.
     */
    public static final String ENML_TAG_EN_NOTE_SELF_CLOSING_REGEX = "(<"+ENML_TAG_EN_NOTE+"[^>]*)/>";
    public static final String ENML_TAG_EN_NOTE_SELF_CLOSING_REPLACEMENT = "$1></" + ENML_TAG_EN_NOTE + ">";
    public static final String ENML_TAG_EN_NOTE_START_REGEX = "(<" + ENML_TAG_EN_NOTE + "[^>]*>)";
    public static final String ENML_TAG_EN_NOTE_START_REPLACEMENT_P1 = "$1";
    /* end */

    public static final String ENML_MODEL_NOTE_NOTEGUID = "Note.guid";
    public static final String ENML_MODEL_NOTE_NOTEGUID_READABLE = "Note";
    public static final String ENML_MODEL_NOTE_NOTEBOOKGUID = "Note.notebookGuid";
    public static final String ENML_MODEL_NOTE_NOTEBOOKGUID_READABLE = "Notebook";

    public static final String EDAM_SYNTAX_INTITLE = "intitle:";

    public static final String EDAM_OAUTH_ADDRESS = "https://evernote.com";

    // Message IDs
    public static final String PLUGIN_CONFIGS_SHELL_TITLE = "Plugin.Configurations.Shell.Title";
    public static final String PLUGIN_CONFIGS_TITLE = "Plugin.Configurations.Title";
    public static final String PLUGIN_CONFIGS_MESSAGE = "Plugin.Configurations.Message";
    public static final String PLUGIN_CONFIGS_HOTSET_SHELL_TITLE = "Plugin.Configurations.Hotset.Shell.Title";
    public static final int PLUGIN_CONFIGS_HOTSET_SHOULD_NOT_SHOW_ID = -1;

    public static final String PLUGIN_CONFIGS_ORGANIZE = "Plugin.Configurations.Organize";
    public static final String PLUGIN_CONFIGS_NOTEBOOK = "Plugin.Configurations.Notebook";
    public static final String PLUGIN_CONFIGS_NOTE = "Plugin.Configurations.Note";
    public static final String PLUGIN_CONFIGS_TAGS = "Plugin.Configurations.Tags";
    public static final String PLUGIN_CONFIGS_TAGS_HINT = "Plugin.Configurations.Tags.HintMessage";
    public static final String PLUGIN_CONFIGS_NOTE_HINT = "Plugin.Configurations.Note.HintMessage";
    public static final String PLUGIN_CONFIGS_NOTEBOOK_HINT = "Plugin.Configurations.Notebook.HintMessage";
    public static final String PLUGIN_CONFIGS_COMMENTS = "Plugin.Configurations.Comments";

    public static final int PLUGIN_CONFIGS_REFRESH_ID = -143259786; // random number
    public static final String PLUGIN_CONFIGS_REFRESH = "Plugin.Configurations.Refresh";

    public static final String PLUGIN_CONFIGS_TOKENNOTCONFIGURED = "Plugin.Configurations.TokenNotConfigured";
    public static final String PLUGIN_CONFIGS_OAUTH_TITLE = "Plugin.Configurations.OAuth.Title";
    public static final String PLUGIN_CONFIGS_OAUTH_CONFIGURE = "Plugin.Configurations.OAuth.Configure";
    public static final String PLUGIN_CONFIGS_OAUTH_NOTNOW = "Plugin.Configurations.OAuth.NotNow";
    public static final String PLUGIN_CONFIGS_OAUTH_CONFIRM = "Plugin.Configurations.OAuth.Confirm";
    public static final String PLUGIN_CONFIGS_OAUTH_WAITING = "Plugin.Configurations.OAuth.Waiting";
    public static final String PLUGIN_CONFIGS_AUTHENTICATING = "Plugin.Configurations.Authenticating";
    public static final String PLUGIN_CONFIGS_FETCHINGNOTEBOOKS = "Plugin.Configurations.FetchingNotebooks";
    public static final String PLUGIN_CONFIGS_FETCHINGNOTES = "Plugin.Configurations.FetchingNotes";
    public static final String PLUGIN_CONFIGS_FETCHINGTAGS = "Plugin.Configurations.FetchingTags";

    public static final String PLUGIN_RUNTIME_ADDFILETOEVERNOTE_MESSAGE = "Plugin.Runtime.AddFileToEvernote.Message";
    public static final String PLUGIN_RUNTIME_ADDFILETOEVERNOTE_SUBTASK_MESSAGE = "Plugin.Runtime.AddFileToEvernote.Subtask.Message";
    public static final String PLUGIN_RUNTIME_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE = "Plugin.Runtime.AddFileToEvernote.OutOfDateMessage";

    public static final String PLUGIN_RUNTIME_ADDSELECTIONTOEVERNOTE_MESSAGE = "Plugin.Runtime.AddSelectionToEvernote.Message";
    public static final String PLUGIN_RUNTIME_ADDSELECTIONTOEVERNOTE_SUBTASK_MESSAGE = "Plugin.Runtime.AddSelectionToEvernote.Subtask.Message";
    public static final String PLUGIN_RUNTIME_ADDSELECTIONTOEVERNOTE_OUTOFDATEMESSAGE = "Plugin.Runtime.AddSelectionToEvernote.OutOfDateMessage";

    public static final String PLUGIN_RUNTIME_CLIPSCREENSHOTTOEVERNOTE_MESSAGE = "Plugin.Runtime.ClipScreenshotToEvernote.Message";
    public static final String PLUGIN_RUNTIME_CLIPSCREENSHOTTOEVERNOTE_SUBTASK_MESSAGE = "Plugin.Runtime.ClipScreenshotToEvernote.Subtask.Message";
    public static final String PLUGIN_RUNTIME_CLIPSCREENSHOTTOEVERNOTE_OUTOFDATEMESSAGE = "Plugin.Runtime.ClipScreenshotToEvernote.OutOfDateMessage";
    public static final String PLUGIN_RUNTIME_CLIPSCREENSHOTTOEVERNOTE_HINT = "Plugin.Runtime.ClipScreenshotToEvernote.Hint";

    public static final String PLUGIN_RUNTIME_AUTHEXPIRED_MESSAGE = "Plugin.Runtime.AuthExpired.Message";
    public static final String PLUGIN_RUNTIME_AUTHEXPIRED_TITLE = "Plugin.Runtime.AuthExpired.Title";
    public static final String PLUGIN_RUNTIME_CREATENEWNOTEWITHGIVENNAME = "Plugin.Runtime.CreateNewNoteWithGivenName";
    public static final String PLUGIN_RUNTIME_CREATENEWNOTE = "Plugin.Runtime.CreateNewNote";
    public static final String PLUGIN_RUNTIME_CLIPTODEFAULT = "Plugin.Runtime.ClipToDefault";

    public static final String PLUGIN_ERROR_OCCURRED = "Plugin.Error.Occurred";
    public static final String PLUGIN_ERROR_OOD = "Plugin.Error.OutOfDate";
    public static final String PLUGIN_THROWABLE_NOTFATAL_MESSAGE = "Plugin.Throwable.NotFatal.Message";

    // DOM
    public static String XML = "xml";
    public static String VERSION = "version";
    public static String ENCODING = "encoding";
    public static String STANDALONE = "standalone";
    public static final String DOM_FRAGMENT_NAME = "#document-fragment";
    public static final String DOM_ERROR0 = "DOM.Error0";
    public static final String DOM_ERROR1 = "DOM.Error1";
    public static final String DOM_ERROR2 = "DOM.Error2";
    public static final String DOM_ERROR3 = "DOM.Error3";
    public static final String DOM_ERROR4 = "DOM.Error4";
    public static final String DOM_ERROR5 = "DOM.Error5";
    public static final String DOM_ERROR6 = "DOM.Error6";
    public static final String DOM_ERROR7 = "DOM.Error7";
    public static final String DOM_ERROR8 = "DOM.Error8";
    public static final String DOM_ERROR9 = "DOM.Error9";

    // OAuth
    public static final String JETTY_LOG_IMPL_CLASS = "org.eclipse.jetty.util.log.class";
    public static final String CALLBACK_URL = "/oauth/callback";
    public static final String OAUTH_VERIFIER = "oauth_verifier";
    public static final String CALLBACK_HTML_META = "text/html;charset=utf-8";
    public static final String OAUTH_EVERNOTE_TRADEMARK = "icons/evernote_32x32.png";

    // others
    public static final String BUNDLE_NAME = "messages";
    public static final String ILLEGAL_ARGUMENT_EXCEPTION_MSG = "Throwable.IllegalArgumentException.Message";
    public static final String FileNamePartSimpleDateFormat = "yyyy-MM-dd'T'HH-mm-ss-";
    public static final String TAGS_SEPARATOR = ConstantsUtil.COMMA;
    public static String NO = "no";
    public static String OK_CAPS = "OK";

    public static final String PLUGIN_DEBUG_MODE = "com.prairie.eevernote.debug";
    public static final String PLUGIN_RUN_ON_SANDBOX = "com.evernote.sandbox";

}
