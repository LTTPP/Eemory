package com.prairie.eevernote.enml;

import com.prairie.eevernote.util.StringUtil;

public class ENNote {

	private String body = StringUtil.EMPTY;

	public void addDiv(Div div) {
		if (!StringUtil.nullOrEmptyString(div.toString())) {
			body += div;
		}
	}

	public void addSnippet(String snippet) {
		if (!StringUtil.nullOrEmptyString(snippet)) {
			body += snippet;
		}
	}

	@Override
	public String toString() {
		String note = "<en-note>";
		if (!StringUtil.nullOrEmptyString(body)) {
			note += body;
		}
		note += "</en-note>";
		return note;
	}

}
