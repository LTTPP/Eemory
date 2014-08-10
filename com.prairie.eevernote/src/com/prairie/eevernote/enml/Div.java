package com.prairie.eevernote.enml;

import com.prairie.eevernote.util.StringUtil;

public class Div {

	private String align = StringUtil.EMPTY;
	private String body = StringUtil.EMPTY;

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void addFont(Font font) {
		if (!StringUtil.nullOrEmptyString(font.toString())) {
			body += font;
		}
	}

	public void addMedia(Media media) {
		if (!StringUtil.nullOrEmptyString(media.toString())) {
			body += media;
		}
	}

	public void addText(String text) {
		if (!StringUtil.nullOrEmptyString(text)) {
			body += text;
		}
	}

	@Override
	public String toString() {
		String div = "<div";
		if (!StringUtil.nullOrEmptyOrBlankString(align)) {
			div += " align=\"" + align + "\"";
		}
		div += ">";
		if (!StringUtil.nullOrEmptyString(body)) {
			div += body;
		}
		div += "</div>";
		return div;
	}

}
