package com.prairie.eevernote.enml;

import com.prairie.eevernote.util.StringUtil;

public class Font {

	private String color = StringUtil.EMPTY;
	private String face = StringUtil.EMPTY;
	private String size = StringUtil.EMPTY;
	private String body = StringUtil.EMPTY;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void addSpan(Span span) {
		if (!StringUtil.nullOrEmptyString(span.toString())) {
			body += span;
		}
	}

	@Override
	public String toString() {
		String font = "<font";
		if (!StringUtil.nullOrEmptyOrBlankString(color)) {
			font += " color=\"" + color + "\"";
		}
		if (!StringUtil.nullOrEmptyOrBlankString(face)) {
			font += " face=\"" + face + "\"";
		}
		if (!StringUtil.nullOrEmptyOrBlankString(size)) {
			font += " size=\"" + size + "\"";
		}
		font += ">";
		if (!StringUtil.nullOrEmptyString(body.toString())) {
			font += body;
		}
		return font + "</font>";
	}

}
