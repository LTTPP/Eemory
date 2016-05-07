package org.lttpp.eemory.ui;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

public class LabelCheckTextField extends LabelTextField {

    private final Button button;

    public LabelCheckTextField(final Button button, final Text text) {
        super(text);
        this.button = button;
    }

    public Button getCheckControl() {
        return button;
    }

    private boolean isChecked() {
        return button == null || button.getSelection();
    }

    private void setChecked(final boolean checked) {
        if (button != null) {
            button.setSelection(checked);
        }
    }

    @Override
    public boolean isEditable() {
        return isChecked() && super.isEditable();
    }

    @Override
    public void setEditable(final boolean enable) {
        setChecked(enable);
        super.setEditable(enable);
    }

}
