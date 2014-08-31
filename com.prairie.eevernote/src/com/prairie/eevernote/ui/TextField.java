package com.prairie.eevernote.ui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

public abstract class TextField {

    public abstract String getValue();

    public abstract void setValue(String text);

    public abstract boolean isEditable();

    public abstract void setEditable(boolean enable);

    public abstract Control getTextControl();

    public abstract void setForeground(Color foreground);

    public abstract Color getForeground();

    public abstract void setBackground(Color background);

    public abstract Color getBackground();

}
