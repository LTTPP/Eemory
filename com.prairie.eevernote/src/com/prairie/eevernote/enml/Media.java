package com.prairie.eevernote.enml;

import com.prairie.eevernote.util.StringUtil;

public class Media {

	private String align = StringUtil.EMPTY;
	private String type = StringUtil.EMPTY;
	private String hash = StringUtil.EMPTY;

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		String media = "<en-media";
		if (!StringUtil.nullOrEmptyOrBlankString(align)) {
			media += " align=\"" + align + "\"";
		}
		if (!StringUtil.nullOrEmptyOrBlankString(type)) {
			media += " type=\"" + type + "\"";
		}
		if (!StringUtil.nullOrEmptyOrBlankString(hash)) {
			media += " hash=\"" + hash + "\"";
		}
		media += "/>";
		return media;
	}

}
