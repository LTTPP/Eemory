package com.prairie.eevernote.ui;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import com.prairie.eevernote.client.EEClipperFactory;
import com.prairie.eevernote.client.impl.ClipperArgsImpl;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.MapUtil;
import com.prairie.eevernote.util.StringUtil;

public class HotTextDialog extends Dialog implements ConstantsUtil, Constants {

    public static final int SHOULD_NOT_SHOW = EECLIPPERPLUGIN_HOTINPUTDIALOG_SHOULD_NOT_SHOW_ID;

    private final Shell shell;
    private static HotTextDialog thisDialog;

    private Map<String, String> notebooks; // <Name, Guid>
    private Map<String, String> notes; // <Name, Guid>

    private SimpleContentProposalProvider noteProposalProvider;

    private Map<String, Text> fields;
    private Map<String, String> quickSettings; // <Field Property, Field Value>
    private Map<String, Map<String, String>> matrix; // <Field Property, <Field Property, Field Value>>

    public HotTextDialog(final Shell parentShell) {
        super(parentShell);
        shell = parentShell;
    }

    @Override
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_HOTINPUTDIALOG_SHELL_TITLE));
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
        container.setLayout(new GridLayout(2, false));
        container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        // ------------

        if (shouldShow(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID)) {

            Text notebookField = createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
            addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, notebookField);
            try {
                notebooks = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN), false).listNotebooks();
                this.enableFilteringContentAssist(notebookField, notebooks.keySet().toArray(new String[notebooks.size()]));
            } catch (Throwable e) {
                MessageDialog.openError(shell, EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e.getLocalizedMessage());
            }
        }

        // ------------

        if (shouldShow(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID)) {
            Text noteField = createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);
            addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, noteField);
            try {
                notes = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN), false).listNotesWithinNotebook(ClipperArgsImpl.forNotebookGuid(IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_CHECKED) ? IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID) : notebooks.get(getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK))));
                noteProposalProvider = this.enableFilteringContentAssist(noteField, notes.keySet().toArray(new String[notes.size()]));
            } catch (Throwable e) {
                MessageDialog.openError(shell, EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e.getLocalizedMessage());
            }
            if (IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_CHECKED)) {
                noteField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(final FocusEvent e) {
                        try {
                            if (HotTextDialog.this.shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)) {
                                notes = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN), false).listNotesWithinNotebook(ClipperArgsImpl.forNotebookGuid(notebooks.get(HotTextDialog.this.getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK))));
                                noteProposalProvider.setProposals(notes.keySet().toArray(new String[notes.size()]));
                            }
                        } catch (Throwable e1) {
                            MessageDialog.openError(shell, EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e1.getLocalizedMessage());
                        }
                    }
                });
            }
        }

        // ------------

        if (shouldShow(SETTINGS_SECTION_TAGS, SETTINGS_KEY_NAME)) {
            Text tagsField = createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS);
            addField(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, tagsField);
            try {
                this.enableFilteringContentAssist(tagsField, EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN), false).listTags(), TAGS_SEPARATOR);
            } catch (Throwable e) {
                MessageDialog.openError(HotTextDialog.this.shell, EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_ERROROCCURRED), e.getLocalizedMessage());
            }
        }

        // ------------

        if (shouldShow(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_NAME)) {
            addField(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS));
        }

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
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
        if (quickSettings == null) {
            quickSettings = MapUtil.map();
        }
        quickSettings.put(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, notebooks == null ? null : notebooks.get(getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)));
        quickSettings.put(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, notes == null ? null : notes.get(getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE)));
        quickSettings.put(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS));
        quickSettings.put(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS));
    }

    public Map<String, String> getQuickSettings() {
        return quickSettings;
    }

    private boolean shouldRefresh(final String uniqueKey, final String property) {
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

    public static int show(final Shell shell) {
        if (shouldShow()) {
            thisDialog = new HotTextDialog(shell);
            return thisDialog.open();
        }
        return HotTextDialog.SHOULD_NOT_SHOW;
    }

    protected static boolean shouldShow() {
        return shouldShow(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID) || shouldShow(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID) || shouldShow(SETTINGS_SECTION_TAGS, SETTINGS_KEY_NAME) || shouldShow(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_NAME);
    }

    private static boolean shouldShow(final String property, final String key) {
        boolean checked = IDialogSettingsUtil.getBoolean(property, SETTINGS_KEY_CHECKED);
        String value = IDialogSettingsUtil.get(property, key);
        return checked && StringUtils.isBlank(value);
    }

    protected Text createLabelTextField(final Composite container, final String labelText) {
        Label label = new Label(container, SWT.NONE);
        label.setText(EEProperties.getProperties().getProperty(labelText) + COLON);

        Text text = new Text(container, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        return text;
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

    protected String getFieldValue(final String property) {
        Text text = (Text) getField(property);
        if (text == null) {
            return null;
        }
        return text.getText().trim();
    }

    protected Control getField(final String property) {
        if (fields == null) {
            return null;
        }
        return fields.get(property);
    }

    protected void addField(final String key, final Text value) {
        if (fields == null) {
            fields = MapUtil.map();
        }
        fields.put(key, value);
    }

    public static HotTextDialog getThis() {
        return thisDialog;
    }

}