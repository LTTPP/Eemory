package com.prairie.eevernote.ui;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class LabelCheckTextField extends LabelTextField {

	private Button button;

	public LabelCheckTextField(Button button, Text text) {
		super(text);
		this.button = button;
	}

	private boolean isChecked() {
		return this.button == null || this.button.getSelection();
	}

	private void setChecked(boolean checked) {
		if (this.button != null) {
			this.button.setSelection(checked);
		}
	}

	@Override
	public boolean isEnabled() {
		return this.isChecked() && super.isEnabled();
	}

	@Override
	public void setEnabled(boolean enable) {
		this.setChecked(enable);
		super.setEnabled(enable);
	}

}
