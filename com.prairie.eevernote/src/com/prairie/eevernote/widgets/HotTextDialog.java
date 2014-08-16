package com.prairie.eevernote.widgets;

import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEProperties;
import com.prairie.eevernote.client.EEClipperManager;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.MapUtil;
import com.prairie.eevernote.util.StringUtil;

public class HotTextDialog extends Dialog implements Constants {

	public static final int SHOULD_NOT_SHOW = Constants.EECLIPPERPLUGIN_HOTINPUTDIALOG_SHOULD_NOT_SHOW_ID;

	private Shell shell;
	private static HotTextDialog thisDialog;

	private Map<String, String> notebooks; // <Name, Guid>
	private Map<String, String> notes; // <Name, Guid>

	private SimpleContentProposalProvider noteProposalProvider;

	private Map<String, Text> fields;
	private Map<String, String> quickSettings; // <Field Property, Field Value>
	private Map<String, Map<String, String>> matrix; // <Field Property, <Field Property, Field Value>>

	public HotTextDialog(Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_HOTINPUTDIALOG_SHELL_TITLE));
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
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		// ------------

		if (!IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_CHECKED)) {

			Text notebookField = this.createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
			this.addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, notebookField);
			try {
				this.notebooks = EEClipperManager.getInstance().getEEClipper(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN), false).listNotebooks();
				this.enableFilteringContentAssist(notebookField, this.notebooks.keySet().toArray(new String[this.notebooks.size()]));
			} catch (Throwable e) {
				MessageDialog.openError(this.shell, EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e.getLocalizedMessage());
			}
		}

		// ------------

		if (!IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTE, SETTINGS_KEY_CHECKED)) {
			Text noteField = this.createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);
			this.addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, noteField);
			try {
				this.notes = EEClipperManager.getInstance().getEEClipper(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN), false).listNotesWithinNotebook(IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_CHECKED) ? IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID) : this.notebooks.get(this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)));
				this.noteProposalProvider = this.enableFilteringContentAssist(noteField, this.notes.keySet().toArray(new String[this.notes.size()]));
			} catch (Throwable e) {
				MessageDialog.openError(this.shell, EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e.getLocalizedMessage());
			}
			if (IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_CHECKED)) {
				noteField.addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(FocusEvent e) {
						try {
							if (HotTextDialog.this.shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)) {
								HotTextDialog.this.notes = EEClipperManager.getInstance().getEEClipper(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN), false).listNotesWithinNotebook(HotTextDialog.this.notebooks.get(HotTextDialog.this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)));
								HotTextDialog.this.noteProposalProvider.setProposals(HotTextDialog.this.notes.keySet().toArray(new String[HotTextDialog.this.notes.size()]));
							}
						} catch (Throwable e1) {
							MessageDialog.openError(HotTextDialog.this.shell, EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e1.getLocalizedMessage());
						}
					}
				});
			}
		}

		// ------------

		if (!IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_TAGS, SETTINGS_KEY_CHECKED)) {
			Text tagsField = this.createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS);
			this.addField(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, tagsField);
			try {
				this.enableFilteringContentAssist(tagsField, EEClipperManager.getInstance().getEEClipper(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN), false).listTags(), TAGS_SEPARATOR);
			} catch (Throwable e) {
				MessageDialog.openError(HotTextDialog.this.shell, EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e.getLocalizedMessage());
			}
		}

		// ------------

		if (!IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_CHECKED)) {
			this.addField(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, this.createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS));
		}

		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 200);
	}

	@Override
	protected void okPressed() {
		saveQuickSettings();
		super.okPressed();
	}

	private void saveQuickSettings() {
		if (this.quickSettings == null) {
			this.quickSettings = MapUtil.map();
		}
		this.quickSettings.put(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, this.notebooks == null ? null : this.notebooks.get(this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)));
		this.quickSettings.put(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, this.notes == null ? null : this.notes.get(this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE)));
		this.quickSettings.put(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS));
		this.quickSettings.put(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS));
	}

	public Map<String, String> getQuickSettings() {
		return this.quickSettings;
	}

	private boolean shouldRefresh(String uniqueKey, String property) {
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

	public static int show(Shell shell) {
		if (shouldShow()) {
			thisDialog = new HotTextDialog(shell);
			return thisDialog.open();
		}
		return HotTextDialog.SHOULD_NOT_SHOW;
	}

	protected static boolean shouldShow() {
		return !IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_CHECKED) || !IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTE, SETTINGS_KEY_CHECKED) || !IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_TAGS, SETTINGS_KEY_CHECKED) || !IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_CHECKED);
	}

	protected Text createLabelTextField(Composite container, String labelText) {
		Label label = new Label(container, SWT.NONE);
		label.setText(EEProperties.getProperties().getProperty(labelText) + COLON);

		Text text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		return text;
	}

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

	protected String getFieldValue(String property) {
		Text text = (Text) this.getField(property);
		if (text == null) {
			return null;
		}
		return text.getText().trim();
	}

	protected Control getField(String property) {
		if (this.fields == null) {
			return null;
		}
		return this.fields.get(property);
	}

	protected void addField(String key, Text value) {
		if (this.fields == null) {
			this.fields = MapUtil.map();
		}
		this.fields.put(key, value);
	}

	public static HotTextDialog getThis() {
		return thisDialog;
	}

}