package com.prairie.eevernote.enml;

import org.eclipse.swt.custom.StyleRange;

import com.prairie.eevernote.util.ColorUtil;

public class StyleTextRange {

	private String text;
	private StyleRange styleRange;

	public StyleTextRange(String text) {
		this.text = text;
		this.styleRange = new StyleRange();
		this.styleRange.fontStyle = FontStyle.NORMAL.toNumber();
		this.styleRange.foreground = ColorUtil.SWT_DEFAULT_COLOR;
	}

	public StyleTextRange(String text, StyleRange styleRange) {
		this.text = text;
		this.styleRange = styleRange;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public StyleRange getStyleRange() {
		return styleRange;
	}

	public void setStyleRange(StyleRange styleRange) {
		this.styleRange = styleRange;
	}

}
