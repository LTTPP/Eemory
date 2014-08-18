package com.prairie.eevernote.ui;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEProperties;
import com.prairie.eevernote.client.EEClipperManager;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.MapUtil;
import com.prairie.eevernote.util.StringUtil;

public class ConfigurationsDialog extends TitleAreaDialog implements Constants {

	private Shell shell;

	private Map<String, String> notebooks; // <Name, Guid>
	private Map<String, String> notes; // <Name, Guid>

	private SimpleContentProposalProvider notebookProposalProvider;
	private SimpleContentProposalProvider noteProposalProvider;
	private SimpleContentProposalProvider tagsProposalProvider;

	private Map<String, TextField> fields;
	private Map<String, Map<String, String>> matrix; // <Field Property, <Field Property, Field Value>>

	private boolean shouldRefresh = false;

	public ConfigurationsDialog(Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
	}

	@Override
	public void create() {
		super.create();
		setTitle(getProperty(EECLIPPERPLUGIN_CONFIGURATIONSDIALOG_TITLE));
		setMessage(getProperty(EECLIPPERPLUGIN_CONFIGURATIONSDIALOG_MESSAGE), IMessageProvider.NONE);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(getProperty(EECLIPPERPLUGIN_CONFIGURATIONSDIALOG_SHELL_TITLE));
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		// container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		// ----------------------

		Group groupAuth = new Group(container, SWT.NONE);
		groupAuth.setText(getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_OAUTH));
		groupAuth.setLayout(new GridLayout(2, false));
		groupAuth.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		TextField tokenField = createLabelTextField(groupAuth, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN);
		this.addField(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN, tokenField);
		restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN);

		// ----------------------

		Group groupPref = new Group(container, SWT.NONE);
		groupPref.setText(getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_EVERNOTEPREFERENCES));
		groupPref.setLayout(new GridLayout(2, false));
		groupPref.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// ----------------------

		TextField notebookField = createLablCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
		this.addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, notebookField);
		try {
			this.notebooks = EEClipperManager.getInstance().getEEClipper(this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN), false).listNotebooks();
			this.notebookProposalProvider = enableFilteringContentAssist(notebookField.getControl(), this.notebooks.keySet().toArray(new String[this.notebooks.size()]));
		} catch (Throwable e) {
			MessageDialog.openError(this.shell, getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e.getLocalizedMessage());
		}
		notebookField.getControl().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				try {
					if (ConfigurationsDialog.this.shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN)) {
						ConfigurationsDialog.this.notebooks = EEClipperManager.getInstance().getEEClipper(ConfigurationsDialog.this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN), false).listNotebooks();
						ConfigurationsDialog.this.notebookProposalProvider.setProposals(ConfigurationsDialog.this.notebooks.keySet().toArray(new String[ConfigurationsDialog.this.notebooks.size()]));
					}
				} catch (Throwable e1) {
					MessageDialog.openError(ConfigurationsDialog.this.shell, getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e1.getLocalizedMessage());
				}
			}
		});
		restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);

		// ----------------------

		TextField noteField = createLablCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);
		this.addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, noteField);
		try {
			this.notes = EEClipperManager.getInstance().getEEClipper(this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN), false).listNotesWithinNotebook(this.notebooks.get(this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)));
			this.noteProposalProvider = enableFilteringContentAssist(noteField.getControl(), this.notes.keySet().toArray(new String[this.notes.size()]));
		} catch (Throwable e) {
			MessageDialog.openError(this.shell, getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e.getLocalizedMessage());
		}
		noteField.getControl().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				try {
					clearHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE);
					if (ConfigurationsDialog.this.shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)) {
						ConfigurationsDialog.this.notes = EEClipperManager.getInstance().getEEClipper(ConfigurationsDialog.this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN), false).listNotesWithinNotebook(ConfigurationsDialog.this.notebooks.get(ConfigurationsDialog.this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)));
						ConfigurationsDialog.this.noteProposalProvider.setProposals(ConfigurationsDialog.this.notes.keySet().toArray(new String[ConfigurationsDialog.this.notes.size()]));
					}
				} catch (Throwable e1) {
					MessageDialog.openError(ConfigurationsDialog.this.shell, getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e1.getLocalizedMessage());
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE);
			}
		});
		restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);

		// ----------------------

		TextField tagsField = createLablCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS);
		this.addField(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, tagsField);
		try {
			this.tagsProposalProvider = enableFilteringContentAssist(tagsField.getControl(), EEClipperManager.getInstance().getEEClipper(this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN), false).listTags(), TAGS_SEPARATOR);
		} catch (Throwable e) {
			MessageDialog.openError(ConfigurationsDialog.this.shell, getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e.getLocalizedMessage());
		}
		tagsField.getControl().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent event) {
				clearHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE);
				try {
					if (ConfigurationsDialog.this.shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN)) {
						ConfigurationsDialog.this.tagsProposalProvider.setProposals(EEClipperManager.getInstance().getEEClipper(ConfigurationsDialog.this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN), false).listTags());
					}
				} catch (Throwable e) {
					MessageDialog.openError(ConfigurationsDialog.this.shell, getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e.getLocalizedMessage());
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE);
			}
		});
		restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS);

		TextField commentsField = createLablCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS);
		this.addField(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, commentsField);
		restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS);

		// ----------------------

		postCreateDialogArea();

		// ----------------------

		return area;
	}

	protected void postCreateDialogArea() {
		showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE);
		showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE);
	}

	private void showHintText(String property, String hintMsg) {
		if (ConfigurationsDialog.this.getField(property).isEnabled() && StringUtil.nullOrEmptyString(this.getFieldValue(property))) {
			this.getField(property).setForeground(this.shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));
			this.setFieldValue(property, getProperty(hintMsg));
		}
	}

	private void clearHintText(String property, String hintMsg) {
		if (ConfigurationsDialog.this.getFieldValue(property).equals(getProperty(hintMsg))) {
			ConfigurationsDialog.this.setFieldValue(property, StringUtil.EMPTY);
			// Sets foreground color to the default system color for this
			// control.
			ConfigurationsDialog.this.getField(property).setForeground(null);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, EECLIPPERPLUGIN_CONFIGURATIONS_REFRESH_ID, getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_REFRESH), false);
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == EECLIPPERPLUGIN_CONFIGURATIONS_REFRESH_ID) {
			this.shouldRefresh = true;
		} else {
			super.buttonPressed(buttonId);
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(550, 400);
	}

	public static int show(Shell shell) {
		ConfigurationsDialog dialog = new ConfigurationsDialog(shell);
		return dialog.open();
	}

	@Override
	protected void okPressed() {
		saveSettings();
		super.okPressed();
	}

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

	private boolean shouldRefresh(String uniqueKey, String property) {
		if (this.shouldRefresh) {
			this.shouldRefresh = false;
			return true;
		}
		return fieldValueChanged(uniqueKey, property);
	}

	private boolean fieldValueChanged(String uniqueKey, String property) {
		if (this.matrix == null) {
			this.matrix = MapUtil.map();
		}
		Map<String, String> map = this.matrix.get(uniqueKey);
		if (map == null) {
			map = MapUtil.map();
			this.matrix.put(uniqueKey, map);
		}
		if (!StringUtil.equalsInLogic(this.getFieldValue(property), map.get(property))) {
			map.put(property, this.getFieldValue(property));
			return true;
		}
		return false;
	}

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

	private void saveSettings() {
		IDialogSettingsUtil.set(SETTINGS_KEY_TOKEN, this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN));
		addNewSection(SETTINGS_SECTION_NOTEBOOK, this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK), this.fieldEnabled(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK), this.notebooks.get(this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)));
		addNewSection(SETTINGS_SECTION_NOTE, this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE), this.fieldEnabled(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE), this.notes.get(this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE)));
		addNewSection(SETTINGS_SECTION_TAGS, this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS).equals(getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE)) ? StringUtil.EMPTY : this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS), this.fieldEnabled(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS), null);
		addNewSection(SETTINGS_SECTION_COMMENTS, this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS), this.fieldEnabled(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS), null);
	}

	private void restoreSettings(String label) {
		if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN)) {
			if (!StringUtil.nullOrEmptyOrBlankString(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN))) {
				this.setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN, IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN));
			}
		} else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)) {
			this.enableField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_CHECKED));
			String value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_NAME);
			if (this.fieldEnabled(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK) && !StringUtil.nullOrEmptyOrBlankString(value)) {
				this.setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, value);
			}
		} else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE)) {
			this.enableField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTE, SETTINGS_KEY_CHECKED));
			String value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_NAME);
			if (this.fieldEnabled(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE) && !StringUtil.nullOrEmptyOrBlankString(value)) {
				this.setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, value);
			}
		} else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS)) {
			this.enableField(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_TAGS, SETTINGS_KEY_CHECKED));
			String value = IDialogSettingsUtil.get(SETTINGS_SECTION_TAGS, SETTINGS_KEY_NAME);
			if (this.fieldEnabled(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS) && !StringUtil.nullOrEmptyOrBlankString(value)) {
				this.setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, value);
			}
		} else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS)) {
			this.enableField(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_CHECKED));
			String value = IDialogSettingsUtil.get(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_NAME);
			if (this.fieldEnabled(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS) && !StringUtil.nullOrEmptyOrBlankString(value)) {
				this.setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, value);
			}
		}
	}

	private void addNewSection(String sectionName, String name, boolean isChecked, String guid) {
		IDialogSettingsUtil.set(sectionName, SETTINGS_KEY_NAME, name);
		IDialogSettingsUtil.set(sectionName, SETTINGS_KEY_CHECKED, isChecked);
		IDialogSettingsUtil.set(sectionName, SETTINGS_KEY_GUID, guid);
	}

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

	protected SimpleContentProposalProvider enableFilteringContentAssist(Control control, String[] proposals, String byOperator) {
		ConfigContentProposalProvider contentProposalProvider = new ConfigContentProposalProvider(proposals);
		contentProposalProvider.setFiltering(true);
		contentProposalProvider.setByOperator(byOperator);

		ConfigTextContentAdapter textContentAdapter = new ConfigTextContentAdapter();
		textContentAdapter.setByOperator(byOperator);

		new ContentProposalAdapter(control, textContentAdapter, contentProposalProvider, null, null);

		return contentProposalProvider;
	}

	protected SimpleContentProposalProvider enableFilteringContentAssist(Control control, String[] proposals) {
		SimpleContentProposalProvider contentProposalProvider = new SimpleContentProposalProvider(proposals);
		contentProposalProvider.setFiltering(true);

		TextContentAdapter textContentAdapter = new TextContentAdapter();

		ContentProposalAdapter contentProposalAdapter = new ContentProposalAdapter(control, textContentAdapter, contentProposalProvider, null, null);
		contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		return contentProposalProvider;
	}

	protected TextField createLablCheckTextField(Composite container, String labelText) {
		final Button button = new Button(container, SWT.CHECK);
		button.setText(getProperty(labelText) + COLON);
		button.setSelection(true);

		final Text text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		text.setEnabled(button.getSelection());

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!button.getSelection()) {
					text.setText(StringUtil.EMPTY);
				}
				text.setEnabled(button.getSelection());
				// Fix Eclipse Bug 193933 – Text is not grayed out when disabled
				// if custom foreground color is set.
				text.setBackground(button.getSelection() ? null : ConfigurationsDialog.this.shell.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
			}
		});

		return new LabelCheckTextField(button, text);
	}

	protected TextField createLabelTextField(Composite container, String labelText) {
		Label label = new Label(container, SWT.NONE);
		label.setText(getProperty(labelText) + COLON);

		Text text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		return new LabelTextField(text);
	}

	// -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

	protected boolean fieldEnabled(String property) {
		TextField f = this.getField(property);
		return f != null && f.isEnabled();
	}

	protected void enableField(String property, boolean check) {
		TextField f = this.getField(property);
		if (f != null) {
			f.setEnabled(check);
		}
	}

	protected String getFieldValue(String property) {
		return this.getField(property).getValue().trim();
	}

	protected void setFieldValue(String property, String value) {
		TextField f = this.getField(property);
		if (f != null) {
			f.setValue(value);
		}
	}

	protected TextField getField(String property) {
		if (this.fields == null) {
			return null;
		}
		return this.fields.get(property);
	}

	protected void addField(String property, TextField field) {
		if (this.fields == null) {
			this.fields = MapUtil.map();
		}
		this.fields.put(property, field);
	}

	protected String getProperty(String key) {
		return EEProperties.getProperties().getProperty(key);
	}
}
