package com.prairie.eevernote.enml;

import com.prairie.eevernote.util.StringUtil;

public class ENML {

	public static final String VERSION_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public static final String DOCTYPE_DEFINITION = "<!DOCTYPE en-note SYSTEM \"http://xml.evernote.com/pub/enml2.dtd\">";

	public static final String NOTE_START = "<en-note>";
	public static final String NOTE_CLOSE = "</en-note>";

	public static final String NOTE_DIV_START = "<div>";
	public static final String NOTE_DIV_CLOSE = "</div>";

	public static String resource(String hexhash, String mimeType) {
		return NOTE_DIV_START + "<en-media align=\"left\" type=\"" + mimeType + "\" hash=\"" + hexhash + "\"/>" + NOTE_DIV_CLOSE;
	}

	public static String content(String content, String mimeType, String comments) {
		if (!StringUtil.nullOrEmptyOrBlankString(mimeType)) {
			content = resource(content, mimeType);
		}
		return ENML.VERSION_DECLARATION + ENML.DOCTYPE_DEFINITION + ENML.NOTE_START + comments(comments) + content + ENML.NOTE_CLOSE;
	}

	public static String comments(String comments) {
		if (StringUtil.nullOrEmptyOrBlankString(comments)) {
			return StringUtil.STRING_EMPTY;
		}
		return NOTE_DIV_START + comments + ":" + NOTE_DIV_START;
	}

	public static String newline() {
		return NOTE_DIV_START + "<br />" + NOTE_DIV_CLOSE;
	}
}
