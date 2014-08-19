package com.prairie.eevernote.enml;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.util.ColorUtil;
import com.prairie.eevernote.util.StringUtil;

public class StyleText implements Constants {

	private String text;
	private String face;
	private String colorHexCode;
	private String size;
	private FontStyle fontStyle;

	public StyleText(String text) {
		this.text = text;
		this.face = StringUtil.EMPTY;
		this.colorHexCode = ColorUtil.toHexCode(ZERO, ZERO, ZERO);
		this.fontStyle = FontStyle.NORMAL;
		this.size = String.valueOf(TEN);
	}

	public StyleText(String text, String face, String colorHexCode, String size, FontStyle fontStyle) {
		this.text = text;
		this.face = face;
		this.colorHexCode = colorHexCode;
		this.fontStyle = fontStyle;
		this.size = size;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getColorHexCode() {
		return colorHexCode;
	}

	public void setColorHexCode(String colorHexCode) {
		this.colorHexCode = colorHexCode;
	}

	public FontStyle getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(FontStyle fontStyle) {
		this.fontStyle = fontStyle;
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

}
