package com.prairie.eevernote.widgets;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

public class LabelTextField extends TextField {

	private Text text;

	public LabelTextField(Text text) {
		this.text = text;
	}

	@Override
	public Text getControl() {
		return text;
	}

	@Override
	public String getValue() {
		return this.text.getText().trim();
	}

	@Override
	public void setValue(String text) {
		this.text.setText(text);
	}

	@Override
	public boolean isEnabled() {
		return this.text.isEnabled();
	}

	@Override
	public void setEnabled(boolean enable) {
		this.text.setEnabled(enable);
	}

	@Override
	public void setForeground(Color foreground) {
		this.text.setForeground(foreground);
	}

	@Override
	public Color getForeground() {
		return this.text.getForeground();
	}

	@Override
	public void setBackground(Color background) {
		this.text.setBackground(background);
	}

	@Override
	public Color getBackground() {
		return this.text.getBackground();
	}

}
