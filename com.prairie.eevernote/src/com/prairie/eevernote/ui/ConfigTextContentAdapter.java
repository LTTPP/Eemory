package com.prairie.eevernote.ui;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class ConfigTextContentAdapter extends TextContentAdapter {

	private String byOperator;

	@Override
	public void insertControlContents(Control control, String text, int cursorPosition) {
		if (!StringUtils.isEmpty(byOperator)) {
			String existingText = ((Text) control).getText();
			if (existingText.contains(byOperator)) {
				((Text) control).setText(existingText.substring(0, existingText.lastIndexOf(byOperator) + 1));
			} else {
				((Text) control).setText(StringUtils.EMPTY);
			}
			int newCursorPosition = ((Text) control).getText().length();
			((Text) control).setSelection(newCursorPosition, newCursorPosition);
		}

		super.insertControlContents(control, text, cursorPosition);
	}

	public String getByOperator() {
		return byOperator;
	}

	public void setByOperator(String byOperator) {
		this.byOperator = byOperator;
	}

}
