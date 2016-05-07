package org.lttpp.eemory;

import org.lttpp.eemory.util.ConstantsUtil;

public final class Constants {

    private Constants() {

    }

    public static final String EVERNOTE_INTERNATIONAL = "Evernote International";
    public static final String EVERNOTE_YINXIANG = "印象笔记";
    public static final String EVERNOTE_SANDBOX = "Evernote Sandbox";

    // Command IDs
    public static final String PLUGIN_COMMAND_PARAM_ID = "org.lttpp.eemory.command.parameter";
    public static final String PLUGIN_COMMAND_ID_CLIP_TO_EVERNOTE = "org.lttpp.eemory.commands.ClipToEvernote";
    public static final String PLUGIN_COMMAND_ID_CLIP_SELECTION_TO_EVERNOTE = "org.lttpp.eemory.commands.ClipSelectionToEvernote";
    public static final String PLUGIN_COMMAND_ID_CLIP_FILE_TO_EVERNOTE = "org.lttpp.eemory.commands.ClipFileToEvernote";
    public static final String PLUGIN_COMMAND_ID_CLIP_SCREENSHOT_TO_EVERNOTE = "org.lttpp.eemory.commands.ClipScreenshotToEvernote";
    public static final String PLUGIN_COMMAND_ID_CONFIGURATIONS = "org.lttpp.eemory.commands.Configurations";
    public static final String PLUGIN_TESTERS_ISFILE = "isFile";

    // field properties
    public static final String PLUGIN_CONFIGS_NOTEBOOK = "Plugin.Configurations.Notebook";
    public static final String PLUGIN_CONFIGS_NOTE = "Plugin.Configurations.Note";
    public static final String PLUGIN_CONFIGS_TAGS = "Plugin.Configurations.Tags";
    public static final String PLUGIN_CONFIGS_COMMENTS = "Plugin.Configurations.Comments";

    // Settings Keys
    public static final String PLUGIN_SETTINGS_KEY_TOKEN = "token";
    public static final String PLUGIN_SETTINGS_SECTION_NOTEBOOK = "notebook";
    public static final String PLUGIN_SETTINGS_SECTION_NOTE = "note";
    public static final String PLUGIN_SETTINGS_SECTION_TAGS = "tags";
    public static final String PLUGIN_SETTINGS_SECTION_COMMENTS = "comments";
    public static final String PLUGIN_SETTINGS_KEY_NAME = "name";
    public static final String PLUGIN_SETTINGS_KEY_GUID = "guid";
    public static final String PLUGIN_SETTINGS_KEY_TYPE = "type";
    public static final String PLUGIN_SETTINGS_KEY_OBJECT = "object";
    public static final String PLUGIN_SETTINGS_KEY_CHECKED = "isChecked";
    public static final String PLUGIN_SETTINGS_KEY_UUID = "uuid";
    public static final String PLUGIN_SETTINGS_KEY_BRAND = "brand";

    // Data Model
    public static final String ENML_MODEL_NOTE_NOTEGUID = "Note.guid";
    public static final String ENML_MODEL_NOTE_NOTEBOOKGUID = "Note.notebookGuid";
    public static final String ENML_MODEL_NOTEBOOKGUID = "Notebook.guid";
    public static final int PLUGIN_CONFIGS_REFRESH_ID = -143259786; // random number

    // DOM
    public static String XML = "xml";
    public static String VERSION = "version";
    public static String ENCODING = "encoding";
    public static String STANDALONE = "standalone";
    public static final String DOM_FRAGMENT_NAME = "#document-fragment";
    public static String NO = "no";

    // ENML
    public static final String XHTML_1_0_LATIN_1_ENT = "xhtml-lat1.ent";
    public static final String XHTML_1_0_SYMBOL_ENT = "xhtml-symbol.ent";
    public static final String XHTML_1_0_SPECIAL_ENT = "xhtml-special.ent";
    public static final String XML_VERSION_1_0 = "1.0";

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

    public static final String FONT_STYLE_NORMAL = "normal";
    public static final String FONT_STYLE_BOLD = "bold";
    public static final String FONT_STYLE_ITALIC = "italic";
    public static final String FONT_STYLE_BOLD_ITALIC = "bold-italic";
    public static final String HTML_NBSP = "&nbsp;";

    public static final String EDAM_SYNTAX_INTITLE = "intitle:";
    /* end */

    // OAuth
    public static final String JETTY_LOG_IMPL_CLASS = "org.eclipse.jetty.util.log.class";
    public static final String CALLBACK_URL = "/oauth/callback";
    public static final String OAUTH_VERIFIER = "oauth_verifier";
    public static final String CALLBACK_HTML_META = "text/html;charset=utf-8";
    public static final String OAUTH_EVERNOTE_TRADEMARK = "icons/evernote_32x32.png";
    public static final String OAUTH_EVERNOTE_TRADEMARK_DISCONNECTED = "icons/evernote_32x32.png";
    public static final String OAUTH_CALLBACK_HTML = "html/callback.html";
    public static final String OAUTH_CALLBACK_ERR_HTML = "html/callback_err.html";
    public static final String OAUTH_NOTTARGET_HTML = "html/images/default.html";
    public static final String OAUTH_RESOURCE_BASE = "html/images";
    public static final String OAUTH_DEFAULT_HTML = "default.html";

    // Store type
    public static final String ENOBJECT_TYPE_NORMAL = "normal";
    public static final String ENOBJECT_TYPE_LINKED = "linked";
    public static final String ENOBJECT_TYPE_BUSINESS = "business";

    // mime-util
    public static final String MimeDetector = "eu.medsea.mimeutil.detector.MagicMimeMimeDetector";

    // Plug-in
    public static final String PLUGIN_DEBUG_MODE = "org.lttpp.eemory.debug";
    public static final String PLUGIN_RUN_ON_SANDBOX = "com.evernote.sandbox";

    // Button properties
    public static final String Plugin_OAuth_AuthExpired_ReAuth = "Plugin_OAuth_AuthExpired_ReAuth";
    public static final String Plugin_OAuth_NotNow = "Plugin_OAuth_NotNow";
    public static final String Plugin_OAuth_Copy = "Plugin_OAuth_Copy";
    public static final String Plugin_OAuth_Cancel = "Plugin_OAuth_Cancel";

    // Others
    public static final String FileNamePartSimpleDateFormat = "yyyy-MM-dd'T'HH-mm-ss-";
    public static final String TAGS_SEPARATOR = ConstantsUtil.COMMA;
    public static final int TAB_WIDTH = 4;
    public static final String returnCode = "returnCode";
    public static final String toggleState = "toggleState";

}
