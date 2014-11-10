package com.prairie.eemory.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;

public class LabelTextField extends TextField {

    private final Text text;

    public LabelTextField(final Text text) {
        this.text = text;
    }

    @Override
    public Text getTextControl() {
        return text;
    }

    @Override
    public String getValue() {
        return text.getText().trim();
    }

    @Override
    public void setValue(final String text) {
        this.text.setText(text);
    }

    @Override
    public boolean isEditable() {
        return text.isEnabled();
    }

    @Override
    public void setEditable(final boolean enable) {
        text.setEnabled(enable);
    }

    @Override
    public void setForeground(final Color foreground) {
        text.setForeground(foreground);
    }

    @Override
    public Color getForeground() {
        return text.getForeground();
    }

    @Override
    public void setBackground(final Color background) {
        text.setBackground(background);
    }

    @Override
    public Color getBackground() {
        return text.getBackground();
    }

    @Override
    public void setTextLimit(final int limit) {
        text.setTextLimit(limit);
    }

}
