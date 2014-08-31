package com.prairie.eevernote.ui;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEProperties;
import com.prairie.eevernote.client.EEClipperFactory;
import com.prairie.eevernote.client.impl.ClipperArgsImpl;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.MapUtil;
import com.prairie.eevernote.util.StringUtil;

public class ConfigurationsDialog extends TitleAreaDialog implements ConstantsUtil, Constants {

    private final Shell shell;

    private Map<String, String> notebooks; // <Name, Guid>
    private Map<String, String> notes; // <Name, Guid>
    private String[] tags;

    private SimpleContentProposalProvider notebookProposalProvider;
    private SimpleContentProposalProvider noteProposalProvider;
    private SimpleContentProposalProvider tagsProposalProvider;

    private Map<String, TextField> fields;
    // <Field Property, <Field Property, Field Value>>
    private Map<String, Map<String, String>> matrix;

    private boolean shouldRefresh = false;

    public ConfigurationsDialog(final Shell parentShell) {
        super(parentShell);
        shell = parentShell;
        notebooks = MapUtil.map();
        notes = MapUtil.map();
        tags = new String[ZERO];
    }

    @Override
    public void create() {
        super.create();
        setTitle(getProperty(EECLIPPERPLUGIN_CONFIGURATIONSDIALOG_TITLE));
        setMessage(getProperty(EECLIPPERPLUGIN_CONFIGURATIONSDIALOG_MESSAGE), IMessageProvider.NONE);
    }

    @Override
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getProperty(EECLIPPERPLUGIN_CONFIGURATIONSDIALOG_SHELL_TITLE));
    }

    @Override
    protected void setShellStyle(final int newShellStyle) {
        super.setShellStyle(newShellStyle | SWT.RESIZE);
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
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
        addField(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN, tokenField);
        restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN);

        // ----------------------

        Group groupPref = new Group(container, SWT.NONE);
        groupPref.setText(getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_EVERNOTEPREFERENCES));
        groupPref.setLayout(new GridLayout(2, false));
        groupPref.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        // ----------------------

        TextField notebookField = createLabelCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
        addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, notebookField);
        final String token = getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN);
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Fetching notebooks...", IProgressMonitor.UNKNOWN);
                    try {
                        notebooks = EEClipperFactory.getInstance().getEEClipper(token, false).listNotebooks();
                    } catch (Throwable e) {
                        // ignore, not fatal
                    }
                    monitor.done();
                }
            });
        } catch (Throwable e) {
            // ignore, not fatal
        }
        notebookProposalProvider = enableFilteringContentAssist(notebookField.getTextControl(), notebooks.keySet().toArray(new String[notebooks.size()]));
        notebookField.getTextControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                try {
                    if (ConfigurationsDialog.this.shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN)) {
                        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    notebooks = EEClipperFactory.getInstance().getEEClipper(token, false).listNotebooks();
                                } catch (Throwable e) {
                                    // ignore, not fatal
                                }
                            }
                        });
                    }
                } catch (Throwable e1) {
                    // ignore, not fatal
                }
                notebookProposalProvider.setProposals(notebooks.keySet().toArray(new String[notebooks.size()]));
            }
        });
        restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);

        // ----------------------

        TextField noteField = createLabelCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);
        addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, noteField);
        final String notebook = getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Fetching notes..", IProgressMonitor.UNKNOWN);
                    try {
                        notes = EEClipperFactory.getInstance().getEEClipper(token, false).listNotesWithinNotebook(ClipperArgsImpl.forNotebookGuid(notebooks.get(notebook)));
                    } catch (Throwable e) {
                        // ignore, not fatal
                    }
                    monitor.done();
                }
            });
        } catch (Throwable e) {
            // ignore, not fatal
        }
        noteProposalProvider = enableFilteringContentAssist(noteField.getTextControl(), notes.keySet().toArray(new String[notes.size()]));
        noteField.getTextControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                try {
                    clearHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE);
                    if (ConfigurationsDialog.this.shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)) {
                        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    notes = EEClipperFactory.getInstance().getEEClipper(token, false).listNotesWithinNotebook(ClipperArgsImpl.forNotebookGuid(notebooks.get(notebook)));
                                } catch (Throwable e) {
                                    // ignore, not fatal
                                }
                            }
                        });
                    }
                } catch (Throwable e1) {
                    // ignore, not fatal
                }
                noteProposalProvider.setProposals(notes.keySet().toArray(new String[notes.size()]));
            }

            @Override
            public void focusLost(final FocusEvent e) {
                showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE);
            }
        });
        restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);

        // ----------------------

        TextField tagsField = createLabelCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS);
        addField(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, tagsField);
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Fetch tags...", IProgressMonitor.UNKNOWN);
                    try {
                        tags = EEClipperFactory.getInstance().getEEClipper(token, false).listTags();
                    } catch (Throwable e) {
                        // ignore, not fatal
                    }
                    monitor.done();
                }
            });
        } catch (Throwable e) {
            // ignore, not fatal
        }
        tagsProposalProvider = enableFilteringContentAssist(tagsField.getTextControl(), tags, TAGS_SEPARATOR);
        tagsField.getTextControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent event) {
                clearHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE);
                try {
                    if (ConfigurationsDialog.this.shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN)) {
                        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    tags = EEClipperFactory.getInstance().getEEClipper(token, false).listTags();
                                } catch (Throwable e) {
                                    // ignore, not fatal
                                }
                            }
                        });
                    }
                } catch (Throwable e) {
                    // ignore, not fatal
                }
                tagsProposalProvider.setProposals(tags);
            }

            @Override
            public void focusLost(final FocusEvent e) {
                showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE);
            }
        });
        restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS);

        TextField commentsField = createLabelCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS);
        addField(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, commentsField);
        restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS);

        // ----------------------

        postCreateDialogArea();

        // ----------------------

        return area;
    }

    protected void postCreateDialogArea() {
        showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE);
        showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE);
    }

    private void showHintText(final String property, final String hintMsg) {
        if (ConfigurationsDialog.this.getField(property).isEditable() && StringUtils.isEmpty(getFieldValue(property))) {
            getField(property).setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_GRAY));
            setFieldValue(property, getProperty(hintMsg));
        }
    }

    private void clearHintText(final String property, final String hintMsg) {
        if (ConfigurationsDialog.this.getFieldValue(property).equals(getProperty(hintMsg))) {
            ConfigurationsDialog.this.setFieldValue(property, StringUtils.EMPTY);
            // Sets foreground color to the default system color for this
            // control.
            ConfigurationsDialog.this.getField(property).setForeground(null);
        }
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, EECLIPPERPLUGIN_CONFIGURATIONS_REFRESH_ID, getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_REFRESH), false);
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void buttonPressed(final int buttonId) {
        if (buttonId == EECLIPPERPLUGIN_CONFIGURATIONS_REFRESH_ID) {
            shouldRefresh = true;
        } else {
            super.buttonPressed(buttonId);
        }
    }

    @Override
    protected Point getInitialSize() {
        return new Point(550, 400);
    }

    public static int show(final Shell shell) {
        ConfigurationsDialog dialog = new ConfigurationsDialog(shell);
        return dialog.open();
    }

    @Override
    protected void okPressed() {
        saveSettings();
        super.okPressed();
    }

    private boolean shouldRefresh(final String uniqueKey, final String property) {
        if (shouldRefresh) {
            shouldRefresh = false;
            return true;
        }
        return fieldValueChanged(uniqueKey, property);
    }

    private boolean fieldValueChanged(final String uniqueKey, final String property) {
        if (matrix == null) {
            matrix = MapUtil.map();
        }
        Map<String, String> map = matrix.get(uniqueKey);
        if (map == null) {
            map = MapUtil.map();
            matrix.put(uniqueKey, map);
        }
        if (!StringUtil.equalsInLogic(getFieldValue(property), map.get(property))) {
            map.put(property, getFieldValue(property));
            return true;
        }
        return false;
    }

    private void saveSettings() {
        IDialogSettingsUtil.set(SETTINGS_KEY_TOKEN, getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN));
        setSection(SETTINGS_SECTION_NOTEBOOK, getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK), isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK), notebooks.get(getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)));
        setSection(SETTINGS_SECTION_NOTE, getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE), isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE), notes.get(getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE)));
        setSection(SETTINGS_SECTION_TAGS, getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS).equals(getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE)) ? StringUtils.EMPTY : getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS), isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS), null);
        setSection(SETTINGS_SECTION_COMMENTS, getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS), isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS), null);
    }

    private void restoreSettings(final String label) {
        if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN)) {
            if (!StringUtil.isNullOrEmptyOrBlank(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN))) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN, IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN));
            }
        } else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)) {
            editableField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_NAME);
            if (isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK) && !StringUtil.isNullOrEmptyOrBlank(value)) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, value);
            }
        } else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE)) {
            editableField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTE, SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_NAME);
            if (isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE) && !StringUtil.isNullOrEmptyOrBlank(value)) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, value);
            }
        } else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS)) {
            editableField(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_TAGS, SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(SETTINGS_SECTION_TAGS, SETTINGS_KEY_NAME);
            if (isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS) && !StringUtil.isNullOrEmptyOrBlank(value)) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, value);
            }
        } else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS)) {
            editableField(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_NAME);
            if (isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS) && !StringUtil.isNullOrEmptyOrBlank(value)) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, value);
            }
        }
    }

    private void setSection(final String sectionName, final String name, final boolean isChecked, final String guid) {
        IDialogSettingsUtil.set(sectionName, SETTINGS_KEY_NAME, name);
        IDialogSettingsUtil.set(sectionName, SETTINGS_KEY_CHECKED, isChecked);
        IDialogSettingsUtil.set(sectionName, SETTINGS_KEY_GUID, guid);
    }

    protected SimpleContentProposalProvider enableFilteringContentAssist(final Control control, final String[] proposals, final String byOperator) {
        ConfigContentProposalProvider contentProposalProvider = new ConfigContentProposalProvider(proposals);
        contentProposalProvider.setFiltering(true);
        contentProposalProvider.setByOperator(byOperator);

        ConfigTextContentAdapter textContentAdapter = new ConfigTextContentAdapter();
        textContentAdapter.setByOperator(byOperator);

        new ContentProposalAdapter(control, textContentAdapter, contentProposalProvider, null, null);

        return contentProposalProvider;
    }

    protected SimpleContentProposalProvider enableFilteringContentAssist(final Control control, final String[] proposals) {
        SimpleContentProposalProvider contentProposalProvider = new SimpleContentProposalProvider(proposals);
        contentProposalProvider.setFiltering(true);

        TextContentAdapter textContentAdapter = new TextContentAdapter();

        ContentProposalAdapter contentProposalAdapter = new ContentProposalAdapter(control, textContentAdapter, contentProposalProvider, null, null);
        contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

        return contentProposalProvider;
    }

    protected LabelCheckTextField createLabelCheckTextField(final Composite container, final String labelText) {
        final Button button = new Button(container, SWT.CHECK);
        button.setText(getProperty(labelText) + COLON);
        button.setSelection(true);

        final Text text = new Text(container, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        text.setEnabled(button.getSelection());

        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                if (!button.getSelection()) {
                    text.setText(StringUtils.EMPTY);
                }
                text.setEnabled(button.getSelection());
                // Fix Eclipse Bug 193933 – Text is not grayed out when disabled
                // if custom foreground color is set.
                text.setBackground(button.getSelection() ? null : shell.getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
            }
        });

        return new LabelCheckTextField(button, text);
    }

    protected TextField createLabelTextField(final Composite container, final String labelText) {
        Label label = new Label(container, SWT.NONE);
        label.setText(getProperty(labelText) + COLON);

        Text text = new Text(container, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        return new LabelTextField(text);
    }

    protected boolean isFieldEditable(final String property) {
        TextField f = getField(property);
        return f != null && f.isEditable();
    }

    protected boolean isFieldEnabled(final String property) {
        TextField f = getField(property);
        return f instanceof LabelCheckTextField ? ((LabelCheckTextField) f).isEnabled() : f != null;
    }

    protected void editableField(final String property, final boolean check) {
        TextField f = getField(property);
        if (f != null) {
            f.setEditable(check);
        }
    }

    protected void enableField(final String property, final boolean enable) {
        TextField f = getField(property);
        if (f instanceof LabelCheckTextField) {
            ((LabelCheckTextField) f).setEnabled(enable);
        }
    }

    protected String getFieldValue(final String property) {
        return getField(property).getValue().trim();
    }

    protected void setFieldValue(final String property, final String value) {
        TextField f = getField(property);
        if (f != null) {
            f.setValue(value);
        }
    }

    protected TextField getField(final String property) {
        if (fields == null) {
            return null;
        }
        return fields.get(property);
    }

    protected void addField(final String property, final TextField field) {
        if (fields == null) {
            fields = MapUtil.map();
        }
        fields.put(property, field);
    }

    protected String getProperty(final String key) {
        return EEProperties.getProperties().getProperty(key);
    }

}
