package com.prairie.eevernote.enml;

import com.prairie.eevernote.util.StringUtil;

public class Bold {

	private String text = StringUtil.EMPTY;

	public Bold(String text) {
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
		String b = "<b>";
		if (!StringUtil.nullOrEmptyString(text)) {
			b += text;
		}
		return b + "</b>";
	}

}
