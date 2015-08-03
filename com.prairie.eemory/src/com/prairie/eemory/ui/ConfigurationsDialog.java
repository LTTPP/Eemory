package com.prairie.eemory.ui;

import static com.prairie.eemory.Constants.PLUGIN_CONFIGS_COMMENTS;
import static com.prairie.eemory.Constants.PLUGIN_CONFIGS_NOTE;
import static com.prairie.eemory.Constants.PLUGIN_CONFIGS_NOTEBOOK;
import static com.prairie.eemory.Constants.PLUGIN_CONFIGS_REFRESH_ID;
import static com.prairie.eemory.Constants.PLUGIN_CONFIGS_TAGS;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_KEY_CHECKED;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_KEY_GUID;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_KEY_NAME;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_KEY_OBJECT;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_KEY_TOKEN;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_KEY_TYPE;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_KEY_UUID;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_SECTION_COMMENTS;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_SECTION_NOTE;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_SECTION_NOTEBOOK;
import static com.prairie.eemory.Constants.PLUGIN_SETTINGS_SECTION_TAGS;
import static com.prairie.eemory.Constants.TAGS_SEPARATOR;

import java.io.IOException;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.prairie.eemory.Messages;
import com.prairie.eemory.client.EeClipper;
import com.prairie.eemory.client.EeClipperFactory;
import com.prairie.eemory.client.impl.model.ENNoteImpl;
import com.prairie.eemory.client.impl.model.ENObjectImpl;
import com.prairie.eemory.client.metadata.EDAMLimits;
import com.prairie.eemory.client.metadata.ENObjectType;
import com.prairie.eemory.client.model.ENNote;
import com.prairie.eemory.client.model.ENObject;
import com.prairie.eemory.exception.EDAMNotFoundHandler;
import com.prairie.eemory.exception.ThrowableHandler;
import com.prairie.eemory.util.ColorUtil;
import com.prairie.eemory.util.ConstantsUtil;
import com.prairie.eemory.util.EclipseUtil;
import com.prairie.eemory.util.EncryptionUtil;
import com.prairie.eemory.util.IDialogSettingsUtil;
import com.prairie.eemory.util.ListUtil;
import com.prairie.eemory.util.MapUtil;
import com.prairie.eemory.util.ObjectUtil;
import com.prairie.eemory.util.StringUtil;

public class ConfigurationsDialog extends TitleAreaDialog {

    private final Shell shell;

    private EeClipper globalClipper;

    private Map<String, ENObject> notebooks; // <Name, Guid|Type|LinkedObject>
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

    // <Field Property, Hint Message>
    private Map<String, String> hintPropMap;

    private boolean canceled = false;

    public ConfigurationsDialog(final Shell parentShell) {
        super(parentShell);
        shell = parentShell;
        notebooks = MapUtil.map();
        notes = MapUtil.map();
        tags = ListUtil.list();
        globalClipper = EeClipperFactory.getInstance().getEeClipper();
        buildHintPropMap();
    }

    @Override
    public void create() {
        super.create();
        setTitle(Messages.Plugin_Configs_Title);
        setMessage(Messages.Plugin_Configs_Message, IMessageProvider.NONE);
    }

    @Override
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.Plugin_Configs_Shell_Title);
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

        Group groupPref = new Group(container, SWT.NONE);
        groupPref.setText(Messages.Plugin_Configs_Organize);
        groupPref.setLayout(new GridLayout(2, false));
        groupPref.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        // ----------------------

        // Auth
        authInProgress();

        final LabelCheckTextField notebookField = createLabelCheckTextField(groupPref, Messages.Plugin_Configs_Notebook);
        notebookField.setTextLimit(EDAMLimits.EDAM_NOTEBOOK_NAME_LEN_MAX);
        addField(PLUGIN_CONFIGS_NOTEBOOK, notebookField);
        fetchNotebooksInProgres();
        notebookProposalProvider = EclipseUtil.enableFilteringContentAssist(notebookField.getTextControl(), notebooks.keySet().toArray(new String[notebooks.size()]));
        notebookField.getTextControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent event) {
                clearHintText(PLUGIN_CONFIGS_NOTEBOOK);
                String[] nbs = notebooks.keySet().toArray(new String[notebooks.size()]);
                Arrays.sort(nbs);
                notebookProposalProvider.setProposals(nbs);
            }

            @Override
            public void focusLost(final FocusEvent e) {
                showHintText(PLUGIN_CONFIGS_NOTEBOOK, Messages.Plugin_Configs_Notebook_Hint);
            }
        });
        notebookField.getCheckControl().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (notebookField.isEditable()) {
                    showHintText(PLUGIN_CONFIGS_NOTEBOOK, Messages.Plugin_Configs_Notebook_Hint);
                }
            }
        });
        restoreSettings(PLUGIN_CONFIGS_NOTEBOOK);

        // ----------------------

        final LabelCheckTextField noteField = createLabelCheckTextField(groupPref, Messages.Plugin_Configs_Note);
        noteField.setTextLimit(EDAMLimits.EDAM_NOTE_TITLE_LEN_MAX);
        addField(PLUGIN_CONFIGS_NOTE, noteField);
        fetchNotesInProgres();
        noteProposalProvider = EclipseUtil.enableFilteringContentAssist(noteField.getTextControl(), notes.keySet().toArray(new String[notes.size()]));
        noteField.getTextControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent e) {
                clearHintText(PLUGIN_CONFIGS_NOTE);
                if (shouldRefresh(PLUGIN_CONFIGS_NOTE, PLUGIN_CONFIGS_NOTEBOOK)) {
                    final String hotebook = getFieldInput(PLUGIN_CONFIGS_NOTEBOOK);
                    BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                        @Override
                        public void run() {
                            try {
                                notes = globalClipper.listNotesWithinNotebook(ENNoteImpl.forNotebook(notebooks.get(hotebook)));
                            } catch (Throwable e) {
                                boolean fixed = ThrowableHandler.handleDesignTimeErr(shell, e, globalClipper);
                                if (fixed) {
                                    try {
                                        notes = globalClipper.listNotesWithinNotebook(ENNoteImpl.forNotebook(notebooks.get(hotebook)));
                                    } catch (Exception ignored) {
                                    }
                                }
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
                showHintText(PLUGIN_CONFIGS_NOTE, Messages.Plugin_Configs_Note_Hint);
            }
        });
        noteField.getCheckControl().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (noteField.isEditable()) {
                    showHintText(PLUGIN_CONFIGS_NOTE, Messages.Plugin_Configs_Note_Hint);
                }
            }
        });
        restoreSettings(PLUGIN_CONFIGS_NOTE);

        // ----------------------

        final LabelCheckTextField tagsField = createLabelCheckTextField(groupPref, Messages.Plugin_Configs_Tags);
        tagsField.setTextLimit(EDAMLimits.EDAM_TAG_NAME_LEN_MAX);
        addField(PLUGIN_CONFIGS_TAGS, tagsField);
        fetchTagsInProgress();
        tagsProposalProvider = EclipseUtil.enableFilteringContentAssist(tagsField.getTextControl(), tags.toArray(new String[tags.size()]), TAGS_SEPARATOR);
        tagsField.getTextControl().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent event) {
                clearHintText(PLUGIN_CONFIGS_TAGS);
                String[] tagArray = tags.toArray(new String[tags.size()]);
                Arrays.sort(tagArray);
                tagsProposalProvider.setProposals(tagArray);
            }

            @Override
            public void focusLost(final FocusEvent e) {
                showHintText(PLUGIN_CONFIGS_TAGS, Messages.Plugin_Configs_Tags_Hint);
            }
        });
        tagsField.getCheckControl().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                if (tagsField.isEditable()) {
                    showHintText(PLUGIN_CONFIGS_TAGS, Messages.Plugin_Configs_Tags_Hint);
                }
            }
        });
        restoreSettings(PLUGIN_CONFIGS_TAGS);

        TextField commentsField = createLabelCheckTextField(groupPref, Messages.Plugin_Configs_Comments);
        addField(PLUGIN_CONFIGS_COMMENTS, commentsField);
        restoreSettings(PLUGIN_CONFIGS_COMMENTS);

        // ----------------------

        postCreateDialogArea();

        // ----------------------

        return area;
    }

    private void buildHintPropMap() {
        if (hintPropMap == null) {
            hintPropMap = MapUtil.map();
        }
        hintPropMap.put(PLUGIN_CONFIGS_NOTEBOOK, Messages.Plugin_Configs_Notebook_Hint);
        hintPropMap.put(PLUGIN_CONFIGS_NOTE, Messages.Plugin_Configs_Note_Hint);
        hintPropMap.put(PLUGIN_CONFIGS_TAGS, Messages.Plugin_Configs_Tags_Hint);
    }

    private void authInProgress() {
        if (isCanceled()) {
            return;
        }
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.Plugin_Configs_Authenticating, 1);
                    try {
                        globalClipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                    } catch (Throwable e) {
                        boolean fixed = ThrowableHandler.handleDesignTimeErr(shell, e, globalClipper);
                        if (fixed) {
                            try {
                                globalClipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                            } catch (Exception ignored) {
                            }
                        }
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
                    monitor.beginTask(Messages.Plugin_Configs_FetchingNotebooks, 1);
                    try {
                        notebooks = globalClipper.listNotebooks();
                    } catch (Throwable e) {
                        boolean fixed = ThrowableHandler.handleDesignTimeErr(shell, e, globalClipper);
                        if (fixed) {
                            try {
                                globalClipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                                notebooks = globalClipper.listNotebooks();
                            } catch (Exception ignored) {
                            }
                        }
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
        final String notebook = getFieldInput(PLUGIN_CONFIGS_NOTEBOOK);
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.Plugin_Configs_FetchingNotes, 1);
                    try {
                        notes = globalClipper.listNotesWithinNotebook(ENNoteImpl.forNotebook(notebooks.get(notebook)));
                    } catch (Throwable e) {
                        boolean fixed = ThrowableHandler.handleDesignTimeErr(shell, e, globalClipper);
                        if (fixed) {
                            try {
                                globalClipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                                notes = globalClipper.listNotesWithinNotebook(ENNoteImpl.forNotebook(notebooks.get(notebook)));
                            } catch (Exception ignored) {
                            }
                        }
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
                    monitor.beginTask(Messages.Plugin_Configs_FetchingTags, 1);
                    try {
                        tags = globalClipper.listTags();
                    } catch (Throwable e) {
                        boolean fixed = ThrowableHandler.handleDesignTimeErr(shell, e, globalClipper);
                        if (fixed) {
                            try {
                                globalClipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                                tags = globalClipper.listTags();
                            } catch (Exception ignored) {
                            }
                        }
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

        fieldValueChanged(PLUGIN_CONFIGS_NOTE, PLUGIN_CONFIGS_NOTEBOOK);
    }

    private void showHintText(final String property, final String hintMsg) {
        if (getField(property).isEditable() && StringUtils.isBlank(getFieldValue(property))) {
            getField(property).setForeground(shell.getDisplay().getSystemColor(ColorUtil.SWT_COLOR_GRAY));
            setFieldValue(property, hintMsg);
            setHasInput(property, false);
        } else {
            setHasInput(property, true);
        }
    }

    private void clearHintText(final String property) {
        if (!isHasInput(property)) {
            setFieldValue(property, StringUtils.EMPTY);
            // Sets foreground color to the default system color for this control.
            getField(property).setForeground(null);
        }
    }

    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, PLUGIN_CONFIGS_REFRESH_ID, Messages.Plugin_Configs_Refresh, false);
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    @Override
    protected void buttonPressed(final int buttonId) {
        if (buttonId == PLUGIN_CONFIGS_REFRESH_ID) {
            refreshPressed();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    protected void refreshPressed() {
        authInProgress();

        // refresh notebook
        fetchNotebooksInProgres();
        diagnoseNotebook();

        // refresh note
        fetchNotesInProgres();
        diagnoseNote();

        // refresh tags
        fetchTagsInProgress();
    }

    private void diagnoseNotebook() {
        String nbName = getFieldInput(PLUGIN_CONFIGS_NOTEBOOK);
        if (StringUtils.isNotBlank(nbName)) {
            if (StringUtils.equals(nbName, IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_UUID))) {
                // user does not change input, so subject to guid
                if (notebooks.containsValue(ENObjectImpl.forGuid(IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_GUID)))) {
                    // rename case on Evernote
                    String key = MapUtil.getKeyByValue(notebooks, ENObjectImpl.forGuid(IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_GUID)));
                    if (isHasInput(PLUGIN_CONFIGS_NOTEBOOK) && !nbName.equals(key)) {
                        setFieldValue(PLUGIN_CONFIGS_NOTEBOOK, key);
                    }
                } else {
                    // re-creation(delete and create a new one with same name) case, and deletion(the configured notebook has been deleted) case on Evernote
                    // but nothing need to do here, Apply will handle all
                }
            } else {
                // user changed input
                // but nothing need to do here, Apply will handle all
            }
        }
    }

    private void diagnoseNote() {
        String nName = getFieldInput(PLUGIN_CONFIGS_NOTE);
        if (!isOk(nName)) {
            if (!StringUtils.equals(nName, IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_UUID))) {
                // user changed input
                refreshGuidByName(nName);
            } else {
                // user does not change input, so subject to guid
                if (notes.containsValue(ENNoteImpl.forGuid(IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_GUID)))) { // override equals() of ENNote, assume ENNote equals if guid equals
                    // rename case on Evernote
                    String key = MapUtil.getKeyByValue(notes, ENNoteImpl.forGuid(IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_GUID)));
                    if (isHasInput(PLUGIN_CONFIGS_NOTE) && !nName.equals(key)) {
                        setFieldValue(PLUGIN_CONFIGS_NOTE, key);
                    }
                } else {
                    // re-creation(delete and create a new one with same name) case, and deletion(the configured notebook has been deleted) case on Evernote
                    refreshGuidByName(nName);
                }
            }
        }
    }

    // diagnose if everything is fine, nothing need to be changed
    private boolean isOk(final String nName) {
        return StringUtils.isBlank(nName) || notes.containsKey(nName) && (StringUtils.equals(notes.get(nName).getName(), IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_NAME)) || StringUtils.isBlank(IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_NAME))) && (StringUtils.equals(notes.get(nName).getGuid(), IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_GUID)) || StringUtils.isBlank(IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_GUID)));
    }

    /*
     * Refresh note related information including UI display by NOTE NAME not
     * uuid. Actually this can be done when Apply button is clicked.
     * 
     * Keeping for 1.0.0 release as already tested, considering deletion in next
     * release.
     * 
     * @param nName uuid of note
     */
    @Deprecated
    private void refreshGuidByName(final String nName) {
        // recreate, delete cases
        ENNote noteFound = EDAMNotFoundHandler.findNote(notes, nName); // NOTICE: pass in uuid here, so should not work for name repetition case
        if (noteFound != null && StringUtils.isNotBlank(noteFound.getGuid())) {
            notes.put(nName, noteFound);
            saveNoteSettings(nName);
        }
    }

    @Deprecated
    private void saveNoteSettings(final String noteValue) {
        ENNote note = notes.get(noteValue);
        setSection(PLUGIN_SETTINGS_SECTION_NOTE, note != null ? note.getName() : null, isFieldEditable(PLUGIN_CONFIGS_NOTE), note != null ? note.getGuid() : null, noteValue);
    }

    @Override
    protected void okPressed() {
        try {
            saveSettings();
        } catch (IOException e) {
            ThrowableHandler.handleDesignTimeErr(shell, e);
        }
        super.okPressed();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(550, 400);
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
        String fieldValue = isHasInput(property) ? getFieldValue(property) : StringUtils.EMPTY;
        if (!StringUtil.equalsInLogic(fieldValue, map.get(property))) {
            map.put(property, getFieldValue(property));
            return true;
        }
        return false;
    }

    private void saveSettings() throws IOException {
        diagnoseNotebook();
        String notebookValue = getFieldInput(PLUGIN_CONFIGS_NOTEBOOK);
        ENObject notebook = notebooks.get(notebookValue);
        setSection(PLUGIN_SETTINGS_SECTION_NOTEBOOK, notebook != null ? notebook.getName() : null, isFieldEditable(PLUGIN_CONFIGS_NOTEBOOK), notebook != null ? notebook.getGuid() : null, notebookValue, notebook != null ? notebook.getType().toString() : null);
        if (notebook != null && notebook.getType() == ENObjectType.LINKED) {
            // notebook.getLinkedObject() should NOT be null
            IDialogSettingsUtil.set(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_OBJECT, ObjectUtil.serialize(notebook.getLinkedObject()));
        } else {
            IDialogSettingsUtil.set(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_OBJECT, null);
        }

        diagnoseNote();
        String noteValue = getFieldInput(PLUGIN_CONFIGS_NOTE);
        ENNote note = notes.get(noteValue);
        setSection(PLUGIN_SETTINGS_SECTION_NOTE, note != null ? note.getName() : null, isFieldEditable(PLUGIN_CONFIGS_NOTE), note != null ? note.getGuid() : null, noteValue);

        String tagsValue = getFieldInput(PLUGIN_CONFIGS_TAGS);
        setSection(PLUGIN_SETTINGS_SECTION_TAGS, tagsValue, isFieldEditable(PLUGIN_CONFIGS_TAGS));

        setSection(PLUGIN_SETTINGS_SECTION_COMMENTS, getFieldInput(PLUGIN_CONFIGS_COMMENTS), isFieldEditable(PLUGIN_CONFIGS_COMMENTS));
    }

    private void restoreSettings(final String label) {
        if (label.equals(PLUGIN_CONFIGS_NOTEBOOK)) {
            editableField(PLUGIN_CONFIGS_NOTEBOOK, IDialogSettingsUtil.getBoolean(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_UUID);
            if (isFieldEditable(PLUGIN_CONFIGS_NOTEBOOK) && StringUtils.isNotBlank(value)) {
                setFieldValue(PLUGIN_CONFIGS_NOTEBOOK, value);
                setHasInput(PLUGIN_CONFIGS_NOTEBOOK, true);
            }
        } else if (label.equals(PLUGIN_CONFIGS_NOTE)) {
            editableField(PLUGIN_CONFIGS_NOTE, IDialogSettingsUtil.getBoolean(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_UUID);
            if (isFieldEditable(PLUGIN_CONFIGS_NOTE) && StringUtils.isNotBlank(value)) {
                setFieldValue(PLUGIN_CONFIGS_NOTE, value);
                setHasInput(PLUGIN_CONFIGS_NOTE, true);
            }
        } else if (label.equals(PLUGIN_CONFIGS_TAGS)) {
            editableField(PLUGIN_CONFIGS_TAGS, IDialogSettingsUtil.getBoolean(PLUGIN_SETTINGS_SECTION_TAGS, PLUGIN_SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_TAGS, PLUGIN_SETTINGS_KEY_NAME);
            if (isFieldEditable(PLUGIN_CONFIGS_TAGS) && StringUtils.isNotBlank(value)) {
                setFieldValue(PLUGIN_CONFIGS_TAGS, value);
                setHasInput(PLUGIN_CONFIGS_TAGS, true);
            }
        } else if (label.equals(PLUGIN_CONFIGS_COMMENTS)) {
            editableField(PLUGIN_CONFIGS_COMMENTS, IDialogSettingsUtil.getBoolean(PLUGIN_SETTINGS_SECTION_COMMENTS, PLUGIN_SETTINGS_KEY_CHECKED));
            String value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_COMMENTS, PLUGIN_SETTINGS_KEY_NAME);
            if (isFieldEditable(PLUGIN_CONFIGS_COMMENTS) && StringUtils.isNotBlank(value)) {
                setFieldValue(PLUGIN_CONFIGS_COMMENTS, value);
            }
        }
    }

    private void setSection(final String sectionName, final String name, final boolean isChecked, final String guid, final String uuid, final String type) {
        setSection(sectionName, name, isChecked, guid, uuid);
        IDialogSettingsUtil.set(sectionName, PLUGIN_SETTINGS_KEY_TYPE, type);
    }

    private void setSection(final String sectionName, final String name, final boolean isChecked, final String guid, final String uuid) {
        setSection(sectionName, name, isChecked, guid);
        IDialogSettingsUtil.set(sectionName, PLUGIN_SETTINGS_KEY_UUID, uuid);
    }

    private void setSection(final String sectionName, final String name, final boolean isChecked, final String guid) {
        setSection(sectionName, name, isChecked);
        IDialogSettingsUtil.set(sectionName, PLUGIN_SETTINGS_KEY_GUID, guid);
    }

    private void setSection(final String sectionName, final String name, final boolean isChecked) {
        IDialogSettingsUtil.set(sectionName, PLUGIN_SETTINGS_KEY_NAME, name);
        IDialogSettingsUtil.set(sectionName, PLUGIN_SETTINGS_KEY_CHECKED, isChecked);
    }

    protected LabelCheckTextField createLabelCheckTextField(final Composite container, final String labelText) {
        final Button button = new Button(container, SWT.CHECK);
        button.setText(labelText + ConstantsUtil.COLON);
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

    protected void setHasInput(final String property, final boolean hasInput) {
        if (inputMatrix == null) {
            inputMatrix = MapUtil.map();
        }
        inputMatrix.put(property, hasInput);
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(final boolean canceled) {
        this.canceled = canceled;
    }

}
