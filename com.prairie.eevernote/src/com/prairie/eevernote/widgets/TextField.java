package com.prairie.eevernote.widgets;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

public abstract class TextField {

	public abstract String getValue();

	public abstract void setValue(String text);

	public abstract boolean isEnabled();

	public abstract void setEnabled(boolean enable);

	public abstract Control getControl();

	public abstract void setForeground(Color foreground);

	public abstract Color getForeground();

	public abstract void setBackground(Color background);

	public abstract Color getBackground();

}
