package com.prairie.eevernote.ui;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
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
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEProperties;
import com.prairie.eevernote.client.EDAMLimits;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.EEClipperFactory;
import com.prairie.eevernote.client.ENNote;
import com.prairie.eevernote.client.impl.ENNoteImpl;
import com.prairie.eevernote.exception.EDAMNotFoundHandler;
import com.prairie.eevernote.exception.ThrowableHandler;
import com.prairie.eevernote.util.ColorUtil;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.EclipseUtil;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.MapUtil;
import com.prairie.eevernote.util.NumberUtil;
import com.prairie.eevernote.util.StringUtil;

public class ConfigurationsDialog extends TitleAreaDialog implements ConstantsUtil, Constants {

    private final Shell shell;

    private EEClipper globalClipper;

    private Map<String, String> notebooks; // <Name, Guid>
    private Map<String, ENNote> notes; // <Name, Guid>
    private List<String> tags;

    private SimpleContentProposalProvider notebookProposalProvider;
    private SimpleContentProposalProvider noteProposalProvider;
    private SimpleContentProposalProvider tagsProposalProvider;

    private Map<String, TextField> fields;
    // <Field Property, <Field Property, Field Value>>
    private Map<String, Map<String, String>> matrix;
    // <Field Property, User Input>
    private Map<String, Boolean> inputMatrix;

    // <Field Property, Hint Message Property>
    private Map<String, String> hintPropMap;

    private boolean canceled = false;

    public ConfigurationsDialog(final Shell parentShell) {
        super(parentShell);
        shell = parentShell;
        notebooks = MapUtil.map();
        notes = MapUtil.map();
        tags = ListUtil.list();
        globalClipper = EEClipperFactory.getInstance().getEEClipper();
        buildHintPropMap();
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
        container.setLayout(new GridLayout(ONE, false));
        container.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        // ----------------------

        Group groupAuth = new Group(container, SWT.NONE);
        groupAuth.setText(getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_OAUTH));
        groupAuth.setLayout(new GridLayout(TWO, false));
        groupAuth.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        TextField tokenField = createLabelHyperlinkTextField(groupAuth, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN, "https://evernote.com", "click to authenticate to Evernote account");
        addField(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN, tokenField);
        tokenField.getTextControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                clearHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN_HINTMESSAGE);
            }
            @Override
            public void focusLost(final FocusEvent e) {
                showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN_HINTMESSAGE);
            }
        });
        restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN);

        // ----------------------

        Group groupPref = new Group(container, SWT.NONE);
        groupPref.setText(getProperty(EECLIPPERPLUGIN_CONFIGURATIONS_EVERNOTEPREFERENCES));
        groupPref.setLayout(new GridLayout(TWO, false));
        groupPref.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        // ----------------------

        // Auth
        authInProgress();

        final LabelCheckTextField notebookField = createLabelCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
        notebookField.setTextLimit(EDAMLimits.EDAM_NOTEBOOK_NAME_LEN_MAX);
        addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, notebookField);
        fetchNotebooksInProgres();
        notebookProposalProvider = EclipseUtil.enableFilteringContentAssist(notebookField.getTextControl(), notebooks.keySet().toArray(new String[notebooks.size()]));
        notebookField.getTextControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent event) {
                clearHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK_HINTMESSAGE);
                try {
                    if (shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN)) {
                        final String hotoken = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN);
                        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                            @Override
                            public void run() {
                                EEClipper clipper = null;
                                try {
                                    clipper = EEClipperFactory.getInstance().getEEClipper(hotoken, false);
                                    notebooks = clipper.listNotebooks();
                                } catch (Throwable e) {
                                    ThrowableHandler.handleDesignTimeErr(shell, e, clipper);
                                }
                            }
                        });
                    }
                } catch (Throwable e) {
                    ThrowableHandler.handleDesignTimeErr(shell, e);
                }
                String[] nbs = notebooks.keySet().toArray(new String[notebooks.size()]);
                Arrays.sort(nbs);
                notebookProposalProvider.setProposals(nbs);
            }

            @Override
            public void focusLost(final FocusEvent e) {
                showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK_HINTMESSAGE);
            }
        });
        notebookField.getCheckControl().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (notebookField.isEditable()) {
                    showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK_HINTMESSAGE);
                }
            }
        });
        restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);

        // ----------------------

        final LabelCheckTextField noteField = createLabelCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);
        noteField.setTextLimit(EDAMLimits.EDAM_NOTE_TITLE_LEN_MAX);
        addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, noteField);
        fetchNotesInProgres();
        noteProposalProvider = EclipseUtil.enableFilteringContentAssist(noteField.getTextControl(), notes.keySet().toArray(new String[notes.size()]));
        noteField.getTextControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                clearHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE);
                if (shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)) {
                    final String hotoken = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN);
                    final String hotebook = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
                    BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                        @Override
                        public void run() {
                            EEClipper clipper = null;
                            try {
                                clipper = EEClipperFactory.getInstance().getEEClipper(hotoken, false);
                                notes = clipper.listNotesWithinNotebook(ENNoteImpl.forNotebookGuid(notebooks.get(hotebook)));
                            } catch (Throwable e) {
                                ThrowableHandler.handleDesignTimeErr(shell, e, clipper);
                            }
                        }
                    });
                }
                String[] ns = notes.keySet().toArray(new String[notes.size()]);
                Arrays.sort(ns);
                noteProposalProvider.setProposals(ns);
            }

            @Override
            public void focusLost(final FocusEvent e) {
                showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE);
            }
        });
        noteField.getCheckControl().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (noteField.isEditable()) {
                    showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE);
                }
            }
        });
        restoreSettings(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);

        // ----------------------

        final LabelCheckTextField tagsField = createLabelCheckTextField(groupPref, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS);
        tagsField.setTextLimit(EDAMLimits.EDAM_TAG_NAME_LEN_MAX);
        addField(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, tagsField);
        fetchTagsInProgress();
        tagsProposalProvider = EclipseUtil.enableFilteringContentAssist(tagsField.getTextControl(), tags.toArray(new String[tags.size()]), TAGS_SEPARATOR);
        tagsField.getTextControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent event) {
                clearHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE);
                try {
                    if (shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN)) {
                        final String hotoken = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN);
                        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                            @Override
                            public void run() {
                                EEClipper clipper = null;
                                try {
                                    clipper = EEClipperFactory.getInstance().getEEClipper(hotoken, false);
                                    tags = clipper.listTags();
                                } catch (Throwable e) {
                                    ThrowableHandler.handleDesignTimeErr(shell, e, clipper);
                                }
                            }
                        });
                    }
                } catch (Throwable e) {
                    ThrowableHandler.handleDesignTimeErr(shell, e);
                }
                String[] tagArray = tags.toArray(new String[tags.size()]);
                Arrays.sort(tagArray);
                tagsProposalProvider.setProposals(tagArray);
            }

            @Override
            public void focusLost(final FocusEvent e) {
                showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE);
            }
        });
        tagsField.getCheckControl().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (tagsField.isEditable()) {
                    showHintText(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE);
                }
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

    private void buildHintPropMap() {
        if (hintPropMap == null) {
            hintPropMap = MapUtil.map();
        }
        hintPropMap.put(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK_HINTMESSAGE);
        hintPropMap.put(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE_HINTMESSAGE);
        hintPropMap.put(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS_HINTMESSAGE);
        hintPropMap.put(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN, EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN_HINTMESSAGE);
    }

    private void authInProgress() {
        if (isCanceled()) {
            return;
        }
        final String token = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN);
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Authenticating...", ONE);
                    try {
                        globalClipper = EEClipperFactory.getInstance().getEEClipper(token, false);
                    } catch (Throwable e) {
                        ThrowableHandler.handleDesignTimeErr(shell, e, globalClipper);
                    }
                    setCanceled(monitor.isCanceled());
                    monitor.done();
                }
            });
        } catch (Throwable e) {
            ThrowableHandler.handleDesignTimeErr(shell, e);
        }
    }

    private void fetchNotebooksInProgres() {
        if (isCanceled()) {
            return;
        }
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Fetching notebooks...", ONE);
                    try {
                        notebooks = globalClipper.listNotebooks();
                    } catch (Throwable e) {
                        ThrowableHandler.handleDesignTimeErr(shell, e, globalClipper);
                    }
                    setCanceled(monitor.isCanceled());
                    monitor.done();
                }
            });
        } catch (Throwable e) {
            ThrowableHandler.handleDesignTimeErr(shell, e);
        }
    }

    private void fetchNotesInProgres() {
        if (isCanceled()) {
            return;
        }
        final String notebook = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Fetching notes...", ONE);
                    try {
                        notes = globalClipper.listNotesWithinNotebook(ENNoteImpl.forNotebookGuid(notebooks.get(notebook)));
                    } catch (Throwable e) {
                        ThrowableHandler.handleDesignTimeErr(shell, e, globalClipper);
                    }
                    setCanceled(monitor.isCanceled());
                    monitor.done();
                }
            });
        } catch (Throwable e) {
            ThrowableHandler.handleDesignTimeErr(shell, e);
        }
    }

    private void fetchTagsInProgress() {
        if (isCanceled()) {
            return;
        }
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Fetching tags...", ONE);
                    try {
                        tags = globalClipper.listTags();
                    } catch (Throwable e) {
                        ThrowableHandler.handleDesignTimeErr(shell, e, globalClipper);
                    }
                    setCanceled(monitor.isCanceled());
                    monitor.done();
                }
            });
        } catch (Throwable e) {
            ThrowableHandler.handleDesignTimeErr(shell, e);
        }
    }

    protected void postCreateDialogArea() {
        for (Entry<String, String> e : hintPropMap.entrySet()) {
            showHintText(e.getKey(), e.getValue());
        }
    }

    private void showHintText(final String property, final String hintMsg) {
        if (getField(property).isEditable() && StringUtils.isBlank(getFieldValue(property))) {
            getField(property).setForeground(shell.getDisplay().getSystemColor(ColorUtil.SWT_COLOR_GRAY));
            setFieldValue(property, getProperty(hintMsg));
            setHasInput(property, false);
        } else {
            setHasInput(property, true);
        }
    }

    private void clearHintText(final String property, final String hintMsg) {
        if (!isHasInput(property)) {
            setFieldValue(property, StringUtils.EMPTY);
            // Sets foreground color to the default system color for this control.
            getField(property).setForeground(null);
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
            refreshPressed();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    protected void refreshPressed() {
        authInProgress();

        // refresh notebook
        fetchNotebooksInProgres();
        String nbName = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
        diagnoseNotebook(nbName);

        // refresh note
        nbName = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
        fetchNotesInProgres();
        String nName = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);
        diagnoseNote(nName);

        // refresh tags
        fetchTagsInProgress();
    }

    private void diagnoseNotebook(final String nbName) {
        if (!StringUtils.isBlank(nbName)) {
            if (!notebooks.containsKey(nbName) && notebooks.containsValue(IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID))) {
                // rename case
                String key = MapUtil.getKey(notebooks, IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID));
                if (!StringUtils.isBlank(nbName) && isHasInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK) && !nbName.equals(key)) {
                    setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, key);
                }
            }
        }
    }

    private void diagnoseNote(final String nName) {
        if (!isOk(nName)) {
            if (!StringUtils.equals(getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE), IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_UUID))) {
                // user changed input
                refreshGuidByName(nName);
            } else {
                // user make nothing change, but maybe something changed in Evernote, needs to be synced
                if (notes.containsValue(ENNoteImpl.forGuid(IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID)))) { // override equals() of ENNote, assume ENNote equals if guid equals
                    refreshNameByGuid();
                } else {
                    refreshGuidByName(nName);
                }
            }
        }
    }

    // diagnose if everything is fine, nothing needs to change
    private boolean isOk(final String nName) {
        return StringUtils.isBlank(nName) || notes.containsKey(nName) && (StringUtils.equals(notes.get(nName).getName(), IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_NAME)) || StringUtils.isBlank(IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_NAME))) && (StringUtils.equals(notes.get(nName).getGuid(), IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID)) || StringUtils.isBlank(IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID)));
    }

    private void refreshGuidByName(final String nName) {
        // recreate, delete cases
        ENNote noteFound = EDAMNotFoundHandler.findNote(notes, nName); // pass in uuid here, so should not work for duplicate name case
        if (noteFound != null && !StringUtils.isBlank(noteFound.getGuid())) {
            notes.put(nName, noteFound);
            saveNoteSettings(nName);
        }
    }

    private void refreshNameByGuid() {
        // rename case
        if (notes.containsValue(ENNoteImpl.forGuid(IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID)))) { // override equals() of ENNote, assume ENNote equals if guid equals
            String key = MapUtil.getKey(notes, ENNoteImpl.forGuid(IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID))); // override equals() of ENNote, assume ENNote equals if guid equals
            if (isHasInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE)) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, key);
                saveNoteSettings(key);
            }
        }
    }

    @Override
    protected void okPressed() {
        saveSettings();
        super.okPressed();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(NumberUtil.number(FIVE, FIVE, ZERO), NumberUtil.number(FOUR, ZERO, ZERO));
    }

    public static int show(final Shell shell) {
        ConfigurationsDialog dialog = new ConfigurationsDialog(shell);
        return dialog.open();
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

    private void saveSettings() {
        IDialogSettingsUtil.set(SETTINGS_KEY_TOKEN, getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN));

        String notebookValue = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
        setSection(SETTINGS_SECTION_NOTEBOOK, notebookValue, isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK), notebooks.get(notebookValue));

        String noteValue = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);
        diagnoseNote(noteValue);
        noteValue = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);
        saveNoteSettings(noteValue);

        String tagsValue = getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS);
        setSection(SETTINGS_SECTION_TAGS, tagsValue, isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS), null);

        setSection(SETTINGS_SECTION_COMMENTS, getFieldInput(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS), isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS), null);
    }

    private void saveNoteSettings(final String noteValue) {
        ENNote note = notes.get(noteValue);
        setSection(SETTINGS_SECTION_NOTE, note != null ? note.getName() : null, isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE), note != null ? note.getGuid() : null);
        IDialogSettingsUtil.set(SETTINGS_SECTION_NOTE, SETTINGS_KEY_UUID, noteValue);
    }

    private void restoreSettings(final String label) {
        if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN)) {
            if (!StringUtils.isBlank(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN))) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TOKEN, IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN));
            }
        } else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)) {
            editableField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_NAME);
            if (isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK) && !StringUtils.isBlank(value)) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, value);
            }
        } else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE)) {
            editableField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTE, SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_UUID);
            if (isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE) && !StringUtils.isBlank(value)) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, value);
            }
        } else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS)) {
            editableField(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_TAGS, SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(SETTINGS_SECTION_TAGS, SETTINGS_KEY_NAME);
            if (isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS) && !StringUtils.isBlank(value)) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, value);
            }
        } else if (label.equals(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS)) {
            editableField(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_NAME);
            if (isFieldEditable(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS) && !StringUtils.isBlank(value)) {
                setFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, value);
            }
        }
    }

    private void setSection(final String sectionName, final String name, final boolean isChecked, final String guid) {
        IDialogSettingsUtil.set(sectionName, SETTINGS_KEY_NAME, name);
        IDialogSettingsUtil.set(sectionName, SETTINGS_KEY_CHECKED, isChecked);
        IDialogSettingsUtil.set(sectionName, SETTINGS_KEY_GUID, guid);
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
                /*
                 * Workaround for Eclipse Bug 193933: Text is not grayed out
                 * when disabled if custom foreground color is set.
                 */
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

    protected TextField createLabelHyperlinkTextField(final Composite container, final String labelText, final String hyperlink, final String tip) {
        Link link = new Link(container, SWT.NONE);
        link.setText("<a href=\"" + hyperlink + "\">" + getProperty(labelText) + COLON + "</a>");
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    //  Open default external browser
                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(event.text));
                } catch (Throwable e) {
                    ThrowableHandler.openError(shell, "Problem occurred when open hyperlink, please copy the link: " + hyperlink + "to browser to continue.");
                }
            }
        });
        link.setToolTipText(tip);

        Text text = new Text(container, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        return new LabelTextField(text);
    }

    protected boolean isFieldEditable(final String property) {
        TextField f = getField(property);
        return f != null && f.isEditable();
    }

    protected void editableField(final String property, final boolean check) {
        TextField f = getField(property);
        if (f != null) {
            f.setEditable(check);
        }
    }

    protected String getFieldValue(final String property) {
        return getField(property).getValue().trim();
    }

    protected String getFieldInput(final String property) {
        if (hintPropMap.containsKey(property)) {
            return isHasInput(property) ? getFieldValue(property) : StringUtils.EMPTY;
        } else {
            return getFieldValue(property);
        }
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

    protected boolean isHasInput(final String property) {
        if (inputMatrix == null) {
            return false;
        }
        Boolean has = inputMatrix.get(property);
        return has == null ? false : has;
    }

    protected void setHasInput(final String property, final boolean inputed) {
        if (inputMatrix == null) {
            inputMatrix = MapUtil.map();
        }
        inputMatrix.put(property, inputed);
    }

    protected String getProperty(final String key) {
        return EEProperties.getProperties().getProperty(key);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(final boolean canceled) {
        this.canceled = canceled;
    }

}
