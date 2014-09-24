package com.prairie.eevernote.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.Messages;
import com.prairie.eevernote.client.EDAMLimits;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.EEClipperFactory;
import com.prairie.eevernote.client.ENNote;
import com.prairie.eevernote.client.impl.ENNoteImpl;
import com.prairie.eevernote.exception.ThrowableHandler;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.EclipseUtil;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.MapUtil;
import com.prairie.eevernote.util.StringUtil;

public class HotTextDialog extends Dialog implements ConstantsUtil, Constants {

    public static final int SHOULD_NOT_SHOW = EECLIPPERPLUGIN_HOTINPUTDIALOG_SHOULD_NOT_SHOW_ID;

    private final Shell shell;
    private static HotTextDialog thisDialog;

    private EEClipper clipper;

    private Map<String, String> notebooks; // <Name, Guid>
    private Map<String, ENNote> notes; // <Name, Guid>
    private List<String> tags;

    private SimpleContentProposalProvider noteProposalProvider;

    private Map<String, Text> fields;
    private ENNote quickSettings;
    // <Field Property, <Field Property, Field Value>>
    private Map<String, Map<String, String>> matrix;

    private boolean fatal = false;// TODO may remove

    private boolean canceled = false;

    public HotTextDialog(final Shell parentShell) {
        super(parentShell);
        shell = parentShell;
        notebooks = MapUtil.map();
        notes = MapUtil.map();
        tags = ListUtil.list();
        clipper = EEClipperFactory.getInstance().getEEClipper();
    }

    @Override
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.getString(EECLIPPERPLUGIN_HOTINPUTDIALOG_SHELL_TITLE));
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
            notebookField.setTextLimit(EDAMLimits.EDAM_NOTEBOOK_NAME_LEN_MAX);
            addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK, notebookField);
            fetchNotebooksInProgres();
            EclipseUtil.enableFilteringContentAssist(notebookField, notebooks.keySet().toArray(new String[notebooks.size()]));
        }

        // ------------

        if (shouldShow(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID)) {
            Text noteField = createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_NOTE);
            noteField.setTextLimit(EDAMLimits.EDAM_NOTE_TITLE_LEN_MAX);
            addField(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, noteField);
            fetchNotesInProgres();
            noteProposalProvider = EclipseUtil.enableFilteringContentAssist(noteField, notes.keySet().toArray(new String[notes.size()]));
            if (shouldShow(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID)) {
                noteField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(final FocusEvent e) {
                        if (shouldRefresh(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE, EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)) {
                            final String hotebook = getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK);
                            BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        notes = clipper.listNotesWithinNotebook(ENNoteImpl.forNotebookGuid(notebooks.get(hotebook)));
                                    } catch (Throwable e) {
                                        ThrowableHandler.handleDesignTimeErr(shell, e, clipper);
                                    }
                                }
                            });
                            String[] ns = notes.keySet().toArray(new String[notes.size()]);
                            Arrays.sort(ns);
                            noteProposalProvider.setProposals(ns);
                        }
                    }
                });
            }
        }

        // ------------

        if (shouldShow(SETTINGS_SECTION_TAGS, SETTINGS_KEY_NAME)) {
            Text tagsField = createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_TAGS);
            tagsField.setTextLimit(EDAMLimits.EDAM_TAG_NAME_LEN_MAX);
            addField(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS, tagsField);
            fetchTagsInProgress();
            EclipseUtil.enableFilteringContentAssist(tagsField, tags.toArray(new String[tags.size()]), TAGS_SEPARATOR);
        }

        // ------------

        if (shouldShow(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_NAME)) {
            addField(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS, createLabelTextField(container, EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS));
        }

        return container;
    }

    private void authInProgress() {
        if (isCanceled()) {
            return;
        }
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Authenticating...", ONE);
                    try {
                        clipper = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(SETTINGS_KEY_TOKEN), false);
                    } catch (Throwable e) {
                        fatal = true;
                        ThrowableHandler.handleDesignTimeErr(shell, e, true, clipper);
                    }
                    setCanceled(monitor.isCanceled());
                    monitor.done();
                }
            });
        } catch (Throwable e) {
            fatal = true;
            ThrowableHandler.handleDesignTimeErr(shell, e, true);
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
                        notebooks = clipper.listNotebooks();
                    } catch (Throwable e) {
                        ThrowableHandler.handleDesignTimeErr(shell, e, clipper);
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
        try {
            new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor.beginTask("Fetching notes...", ONE);
                    try {
                        notes = clipper.listNotesWithinNotebook(ENNoteImpl.forNotebookGuid(IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID)));
                    } catch (Throwable e) {
                        ThrowableHandler.handleDesignTimeErr(shell, e, clipper);
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
                        tags = clipper.listTags();
                    } catch (Throwable e) {
                        ThrowableHandler.handleDesignTimeErr(shell, e, clipper);
                    }
                    setCanceled(monitor.isCanceled());
                    monitor.done();
                }
            });
        } catch (Throwable e) {
            ThrowableHandler.handleDesignTimeErr(shell, e);
        }
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
        if (!confirmDefault()) {
            return;
        }
        super.okPressed();
    }

    private boolean confirmDefault() {
        boolean confirm = false;
        String msg = StringUtils.EMPTY;
        if (shouldShow(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID) && StringUtils.isBlank(quickSettings.getGuid())) {
            msg = "No existing note found, will create a new one" + (StringUtils.isBlank(quickSettings.getName()) ? StringUtils.EMPTY : " with given name " + quickSettings.getName()) + ".";
            confirm = true;
        } else if (shouldShow(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID) && StringUtils.isBlank(quickSettings.getNotebook().getGuid())) {
            msg = "No existing notebook found, will clip to default.";
            confirm = true;
        }
        return confirm ? MessageDialog.openQuestion(shell, Messages.getString(EECLIPPERPLUGIN_HOTINPUTDIALOG_SHELL_TITLE), msg) : true;
    }

    private void saveQuickSettings() {
        quickSettings = new ENNoteImpl();

        quickSettings.getNotebook().setName(getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK));
        quickSettings.getNotebook().setGuid(notebooks.get(getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK)));

        ENNote note = notes.get(getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE));
        quickSettings.setName(note != null ? note.getName() : getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE));
        quickSettings.setGuid(note != null ? note.getGuid() : null);

        String tags = getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS);
        if (!StringUtils.isBlank(tags)) {
            quickSettings.setTags(ListUtil.toList(tags.split(ConstantsUtil.TAGS_SEPARATOR)));
        }

        quickSettings.setComments(getFieldValue(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS));
    }

    public ENNote getQuickSettings() {
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
            thisDialog.authInProgress();
            return thisDialog.fatal ? CANCEL : thisDialog.open();
        }
        return HotTextDialog.SHOULD_NOT_SHOW;
    }

    protected static boolean shouldShow() {
        return shouldShow(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID) || shouldShow(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID) || shouldShow(SETTINGS_SECTION_TAGS, SETTINGS_KEY_NAME) || shouldShow(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_NAME);
    }

    private static boolean shouldShow(final String property, final String key) {
        if (property.equals(SETTINGS_SECTION_NOTEBOOK)) {
            if (IDialogSettingsUtil.getBoolean(SETTINGS_SECTION_NOTE, SETTINGS_KEY_CHECKED) && !shouldShow(SETTINGS_SECTION_NOTE, key)) {
                return false;
            }
        }
        boolean checked = IDialogSettingsUtil.getBoolean(property, SETTINGS_KEY_CHECKED);
        String value = IDialogSettingsUtil.get(property, key);
        return checked && StringUtils.isBlank(value);
    }

    protected Text createLabelTextField(final Composite container, final String labelText) {
        Label label = new Label(container, SWT.NONE);
        label.setText(Messages.getString(labelText) + COLON);

        Text text = new Text(container, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        return text;
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

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(final boolean canceled) {
        this.canceled = canceled;
    }

}