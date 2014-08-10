package com.prairie.eevernote.enml;

import com.prairie.eevernote.util.StringUtil;

public class Span {

	private String style = StringUtil.EMPTY;
	private String body = StringUtil.EMPTY;

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setFontSize(String size) {
		this.style = "font-size:" + size + "pt";
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void addBold(Bold bold) {
		if (!StringUtil.nullOrEmptyString(bold.toString())) {
			body += bold;
		}
	}

	public void addItalic(Italic italic) {
		if (!StringUtil.nullOrEmptyString(italic.toString())) {
			body += italic;
		}
	}

	public void addText(String text) {
		if (!StringUtil.nullOrEmptyString(text.toString())) {
			body += text;
		}
	}

	public void addBr(Br br) {
		if (!StringUtil.nullOrEmptyString(br.toString())) {
			body += br;
		}
	}

	@Override
	public String toString() {
		String span = "<span";
		if (!StringUtil.nullOrEmptyOrBlankString(style)) {
			span += " style=\"" + style + "\"";
		}
		span += ">";
		if (!StringUtil.nullOrEmptyString(body)) {
			span += body;
		}
		span += "</span>";
		return span;
	}
}
