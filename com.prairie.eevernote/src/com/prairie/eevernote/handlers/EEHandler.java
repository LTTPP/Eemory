package com.prairie.eevernote.handlers;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.evernote.edam.error.EDAMNotFoundException;
import com.prairie.eevernote.Constants;
import com.prairie.eevernote.Messages;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.EEClipperFactory;
import com.prairie.eevernote.client.ENNote;
import com.prairie.eevernote.client.impl.ENNoteImpl;
import com.prairie.eevernote.exception.EDAMNotFoundHandler;
import com.prairie.eevernote.exception.ThrowableHandler;
import com.prairie.eevernote.oauth.OAuth;
import com.prairie.eevernote.ui.CaptureView;
import com.prairie.eevernote.ui.ConfigurationsDialog;
import com.prairie.eevernote.ui.QuickOrganizeDialog;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.DateTimeUtil;
import com.prairie.eevernote.util.EclipseUtil;
import com.prairie.eevernote.util.EncryptionUtil;
import com.prairie.eevernote.util.FileUtil;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.LogUtil;

public class EEHandler extends AbstractHandler implements Constants {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        // check token
        if (StringUtils.isBlank(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN))) {
            oauth(event);
            if (StringUtils.isBlank(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN))) {
                return null;
            } else {
                boolean confirmed = MessageDialog.openConfirm(HandlerUtil.getActiveShellChecked(event), event.getParameter(PLUGIN_COMMAND_PARAM_ID), Messages.getString(PLUGIN_CONFIGS_OAUTH_CONFIRM));
                if (!confirmed) {
                    return null;
                }
            }
        }
        // clip
        if (event.getCommand().getId().equals(PLUGIN_COMMAND_ID_CONFIGURATIONS)) {
            configurationsClicked(event);
        } else if (event.getCommand().getId().equals(PLUGIN_COMMAND_ID_CLIP_TO_EVERNOTE)) {
            clipSelectionClicked(event);
        } else if (event.getCommand().getId().equals(PLUGIN_COMMAND_ID_CLIP_SELECTION_TO_EVERNOTE)) {
            clipSelectionClicked(event);
        } else if (event.getCommand().getId().equals(PLUGIN_COMMAND_ID_CLIP_FILE_TO_EVERNOTE)) {
            clipFileClicked(event);
        } else if (event.getCommand().getId().equals(PLUGIN_COMMAND_ID_CLIP_SCREENSHOT_TO_EVERNOTE)) {
            clipScreenshotClicked(event);
        }
        return null;
    }

    protected void oauth(final ExecutionEvent event) throws ExecutionException {
        final Shell shell = HandlerUtil.getActiveShellChecked(event);
        int opt = EclipseUtil.openCustomImageTypeWithCustomButtons(shell, Messages.getString(PLUGIN_CONFIGS_OAUTH_TITLE), Messages.getString(PLUGIN_CONFIGS_TOKENNOTCONFIGURED), new Image(Display.getDefault(), getClass().getClassLoader().getResourceAsStream(OAUTH_EVERNOTE_TRADEMARK)), ArrayUtils.toArray(Messages.getString(PLUGIN_CONFIGS_OAUTH_CONFIGURE), Messages.getString(PLUGIN_CONFIGS_OAUTH_NOTNOW)));
        if (opt == 0) {
            try {
                new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                    @Override
                    public void run(final IProgressMonitor monitor) {
                        monitor.beginTask(Messages.getString(PLUGIN_CONFIGS_OAUTH_WAITING), IProgressMonitor.UNKNOWN);
                        try {
                            String token = new OAuth().auth();
                            IDialogSettingsUtil.set(PLUGIN_SETTINGS_KEY_TOKEN, EncryptionUtil.encrypt(token));
                        } catch (Throwable e) {
                            ThrowableHandler.handleDesignTimeErr(shell, e);
                        }
                        monitor.done();
                    }
                });
            } catch (Throwable e) {
                throw ThrowableHandler.handleExecErr(e);
            }
        }
    }

    protected void clipFileClicked(final ExecutionEvent event) throws ExecutionException {
        try {
            final ENNote args = createENNote();

            int option = QuickOrganizeDialog.show(HandlerUtil.getActiveShellChecked(event));
            if (option == QuickOrganizeDialog.OK) {
                args.adopt(QuickOrganizeDialog.getThis().getQuickSettings());
            } else if (option == QuickOrganizeDialog.CANCEL) {
                return;
            }

            args.setAttachments(EclipseUtil.getSelectedFiles(event));
            if (StringUtils.isBlank(args.getName())) {
                args.setName(FileUtil.concatNameOfFiles(args.getAttachments()));
            }

            Job job = new Job(Messages.getString(PLUGIN_RUNTIME_ADDFILETOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.getString(PLUGIN_RUNTIME_ADDFILETOEVERNOTE_MESSAGE), 2);
                    EEClipper clipper = null;
                    try {
                        clipper = EEClipperFactory.getInstance().getEEClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                        monitor.worked(1);

                        if (monitor.isCanceled()) {
                            return LogUtil.cancel();
                        }
                        clipper.clipFile(args);
                        monitor.worked(2);
                    } catch (EDAMNotFoundException e) {
                        // try to auto fix EDAMNotFoundException
                        boolean fixed = new EDAMNotFoundHandler(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN))).fixNotFoundException(e, args);
                        if (fixed) {
                            try {
                                clipper.clipFile(args);
                            } catch (Throwable e1) {
                                return ThrowableHandler.handleJobErr(e1, clipper);
                            }
                            saveIfNeeded(args);
                            return LogUtil.ok();
                        }
                        return ThrowableHandler.handleJobErr(e);
                    } catch (Throwable e) {
                        return ThrowableHandler.handleJobErr(e, clipper);
                    }
                    monitor.done();
                    return LogUtil.ok();
                }
            };
            job.setUser(true);
            job.schedule();

        } catch (Throwable e) {
            throw ThrowableHandler.handleExecErr(e);
        }
    }

    protected void clipSelectionClicked(final ExecutionEvent event) throws ExecutionException {
        try {
            final ENNote args = createENNote();

            int option = QuickOrganizeDialog.show(HandlerUtil.getActiveShellChecked(event));
            if (option == QuickOrganizeDialog.OK) {
                args.adopt(QuickOrganizeDialog.getThis().getQuickSettings());
            } else if (option == QuickOrganizeDialog.CANCEL) {
                return;
            }

            IEditorPart editor = HandlerUtil.getActiveEditor(event);
            StyledText styledText = (StyledText) editor.getAdapter(Control.class);
            if (StringUtils.isBlank(args.getName())) {
                args.setName(editor.getTitle() + ConstantsUtil.MINUS + DateTimeUtil.timestamp());
            }
            args.setContent(EclipseUtil.getSelectedStyleText(styledText));

            Job job = new Job(Messages.getString(PLUGIN_RUNTIME_ADDSELECTIONTOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.getString(PLUGIN_RUNTIME_ADDSELECTIONTOEVERNOTE_MESSAGE), 2);
                    EEClipper clipper = null;
                    try {
                        clipper = EEClipperFactory.getInstance().getEEClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                        monitor.worked(1);

                        if (monitor.isCanceled()) {
                            return LogUtil.cancel();
                        }
                        clipper.clipSelection(args);
                        monitor.worked(2);
                    } catch (EDAMNotFoundException e) {
                        boolean fixed = new EDAMNotFoundHandler(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN))).fixNotFoundException(e, args);
                        if (fixed) {
                            try {
                                clipper.clipFile(args);
                            } catch (Throwable e1) {
                                return ThrowableHandler.handleJobErr(e1, clipper);
                            }
                            saveIfNeeded(args);
                            return LogUtil.ok();
                        }
                        return ThrowableHandler.handleJobErr(e);
                    } catch (final Throwable e) {
                        return ThrowableHandler.handleJobErr(e, clipper);
                    }
                    monitor.done();
                    return LogUtil.ok();
                }
            };
            job.setUser(true);
            job.schedule();

        } catch (Throwable e) {
            throw ThrowableHandler.handleExecErr(e);
        }
    }

    protected void clipScreenshotClicked(final ExecutionEvent event) throws ExecutionException {
        try {
            Thread.sleep(800); // wait for right click menu to hide

            final BufferedImage screenshot = CaptureView.showView();
            if (screenshot == null) {
                return;
            }

            final ENNote args = createENNote();

            int option = QuickOrganizeDialog.show(HandlerUtil.getActiveShellChecked(event));
            if (option == QuickOrganizeDialog.OK) {
                args.adopt(QuickOrganizeDialog.getThis().getQuickSettings());
            } else if (option == QuickOrganizeDialog.CANCEL) {
                return;
            }

            final File file = File.createTempFile(DateTimeUtil.formatCurrentTime(FileNamePartSimpleDateFormat), ConstantsUtil.DOT + ConstantsUtil.IMG_PNG);
            if (StringUtils.isBlank(args.getName())) {
                args.setName(DateTimeUtil.timestamp() + ConstantsUtil.DOT + ConstantsUtil.IMG_PNG);
            }
            args.setAttachments(ListUtil.list(file));

            Job job = new Job(Messages.getString(PLUGIN_RUNTIME_ADDFILETOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.getString(PLUGIN_RUNTIME_ADDFILETOEVERNOTE_MESSAGE), 3);
                    EEClipper clipper = null;
                    try {
                        ImageIO.write(screenshot, ConstantsUtil.IMG_PNG, file);
                        monitor.worked(1);

                        clipper = EEClipperFactory.getInstance().getEEClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                        monitor.worked(2);

                        if (monitor.isCanceled()) {
                            return LogUtil.cancel();
                        }
                        clipper.clipFile(args);
                        monitor.worked(3);
                    } catch (EDAMNotFoundException e) {
                        boolean fixed = new EDAMNotFoundHandler(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN))).fixNotFoundException(e, args);
                        if (fixed) {
                            try {
                                clipper.clipFile(args);
                            } catch (Throwable e1) {
                                return ThrowableHandler.handleJobErr(e1, clipper);
                            }
                            saveIfNeeded(args);
                            monitor.done();
                            return LogUtil.ok();
                        }
                        return ThrowableHandler.handleJobErr(e);
                    } catch (Throwable e) {
                        return ThrowableHandler.handleJobErr(e, clipper);
                    } finally {
                        if (file != null && file.exists()) {
                            file.delete();
                        }
                    }
                    monitor.done();
                    return LogUtil.ok();
                }
            };
            job.setUser(true);
            job.schedule();

        } catch (Throwable e) {
            throw ThrowableHandler.handleExecErr(e);
        }
    }

    protected void configurationsClicked(final ExecutionEvent event) throws ExecutionException {
        ConfigurationsDialog.show(HandlerUtil.getActiveShellChecked(event));
    }

    private ENNote createENNote() {
        ENNote args = new ENNoteImpl();

        String value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_GUID);
        if (StringUtils.isNotBlank(value)) {
            args.getNotebook().setGuid(value);
        }
        value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_NAME);
        if (StringUtils.isNotBlank(value)) {
            args.getNotebook().setName(value);
        }

        value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_GUID);
        if (StringUtils.isNotBlank(value)) {
            args.setGuid(value);
        }
        value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_NAME);
        if (StringUtils.isNotBlank(value)) {
            args.setName(value);
        }

        value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_TAGS, PLUGIN_SETTINGS_KEY_NAME);
        if (StringUtils.isNotBlank(value)) {
            args.setTags(ListUtil.toList(value.split(TAGS_SEPARATOR)));
        }
        value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_COMMENTS, PLUGIN_SETTINGS_KEY_NAME);
        if (StringUtils.isNotBlank(value)) {
            args.setComments(value);
        }

        return args;
    }

    private void saveIfNeeded(final ENNote args) {
        if (args.getNotebook().isGuidReset() && !args.getNotebook().isGuidAdopt()) {
            IDialogSettingsUtil.set(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_GUID, args.getNotebook().getGuid());
        }
        if (args.isGuidReset() && !args.isGuidAdopt()) {
            IDialogSettingsUtil.set(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_GUID, args.getGuid());
        }
    }

}
