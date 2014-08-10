package com.prairie.eevernote.enml;

import com.prairie.eevernote.util.StringUtil;

public class Italic {

	private String text = StringUtil.EMPTY;

	public Italic(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if (!StringUtil.nullOrEmptyString(text)) {
			this.text = text;
		}
	}

	public void addText(String text) {
		if (!StringUtil.nullOrEmptyString(text)) {
			this.text += text;
		}
	}

	@Override
	public String toString() {
		String b = "<i>";
		if (!StringUtil.nullOrEmptyString(text)) {
			b += text;
		}
		return b + "</i>";
	}

}
