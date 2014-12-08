package com.prairie.eemory.handlers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

import com.prairie.eemory.Constants;
import com.prairie.eemory.Messages;
import com.prairie.eemory.client.ENNote;
import com.prairie.eemory.client.ENObjectType;
import com.prairie.eemory.client.EeClipper;
import com.prairie.eemory.client.EeClipperFactory;
import com.prairie.eemory.client.impl.ENNoteImpl;
import com.prairie.eemory.exception.ThrowableHandler;
import com.prairie.eemory.oauth.OAuth;
import com.prairie.eemory.ui.CaptureView;
import com.prairie.eemory.ui.ConfigurationsDialog;
import com.prairie.eemory.ui.QuickOrganizeDialog;
import com.prairie.eemory.util.ConstantsUtil;
import com.prairie.eemory.util.DateTimeUtil;
import com.prairie.eemory.util.EclipseUtil;
import com.prairie.eemory.util.EncryptionUtil;
import com.prairie.eemory.util.FileUtil;
import com.prairie.eemory.util.IDialogSettingsUtil;
import com.prairie.eemory.util.ListUtil;
import com.prairie.eemory.util.LogUtil;
import com.prairie.eemory.util.ObjectUtil;

public class EeHandler extends AbstractHandler implements Constants {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        // check token
        if (StringUtils.isBlank(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN))) {
            oauth(event);
            if (StringUtils.isBlank(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN))) {
                return null;
            } else {
                boolean confirmed = MessageDialog.openConfirm(HandlerUtil.getActiveShellChecked(event), event.getParameter(PLUGIN_COMMAND_PARAM_ID), Messages.Plugin_OAuth_Confirm);
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
        int opt = EclipseUtil.openCustomImageTypeWithCustomButtons(shell, Messages.Plugin_OAuth_Title, Messages.Plugin_OAuth_TokenNotConfigured, new Image(Display.getDefault(), getClass().getClassLoader().getResourceAsStream(OAUTH_EVERNOTE_TRADEMARK)), ArrayUtils.toArray(Messages.Plugin_OAuth_Configure, Messages.Plugin_OAuth_NotNow));
        if (opt == 0) {
            try {
                new ProgressMonitorDialog(shell).run(true, true, new IRunnableWithProgress() {
                    @Override
                    public void run(final IProgressMonitor monitor) {
                        monitor.beginTask(Messages.Plugin_OAuth_Waiting, IProgressMonitor.UNKNOWN);
                        try {
                            String token = new OAuth().auth();
                            if (StringUtils.isNotBlank(token)) {
                                IDialogSettingsUtil.set(PLUGIN_SETTINGS_KEY_TOKEN, EncryptionUtil.encrypt(token));
                            }
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

            Job job = new Job(Messages.Plugin_Runtime_AddFileToEvernote) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.Plugin_Runtime_AddFileToEvernote, 2);
                    EeClipper clipper = null;
                    try {
                        clipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                        monitor.worked(1);

                        if (monitor.isCanceled()) {
                            return LogUtil.cancel();
                        }
                        clipper.clipFile(args);
                        monitor.worked(2);
                    } catch (Throwable e) {
                        IStatus status = ThrowableHandler.handleJobErr(e, clipper, args, event);
                        if (status == LogUtil.ok()) {
                            try {
                                clipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                                clipper.clipFile(args);
                            } catch (Throwable t) {
                                return ThrowableHandler.handleJobErr(t, clipper);
                            }
                            try {
                                saveIfNeeded(args);
                            } catch (Throwable ignored) {
                            }
                        }
                        return status;
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

            Job job = new Job(Messages.Plugin_Runtime_AddSelectionToEvernote) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.Plugin_Runtime_AddSelectionToEvernote, 2);
                    EeClipper clipper = null;
                    try {
                        clipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                        monitor.worked(1);

                        if (monitor.isCanceled()) {
                            return LogUtil.cancel();
                        }
                        clipper.clipSelection(args);
                        monitor.worked(2);
                    } catch (Throwable e) {
                        IStatus status = ThrowableHandler.handleJobErr(e, clipper, args, event);
                        if (status == LogUtil.ok()) {
                            try {
                                clipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                                clipper.clipSelection(args);
                            } catch (Throwable t) {
                                return ThrowableHandler.handleJobErr(t, clipper);
                            }
                            try {
                                saveIfNeeded(args);
                            } catch (Throwable ignored) {
                            }
                        }
                        return status;
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

            Job job = new Job(Messages.Plugin_Runtime_AddFileToEvernote) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.Plugin_Runtime_AddFileToEvernote, 3);
                    EeClipper clipper = null;
                    try {
                        ImageIO.write(screenshot, ConstantsUtil.IMG_PNG, file);
                        monitor.worked(1);

                        clipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                        monitor.worked(2);

                        if (monitor.isCanceled()) {
                            return LogUtil.cancel();
                        }
                        clipper.clipFile(args);
                        monitor.worked(3);
                    } catch (Throwable e) {
                        IStatus status = ThrowableHandler.handleJobErr(e, clipper, args, event);
                        if (status == LogUtil.ok()) {
                            try {
                                clipper = EeClipperFactory.getInstance().getEeClipper(EncryptionUtil.decrypt(IDialogSettingsUtil.get(PLUGIN_SETTINGS_KEY_TOKEN)), false);
                                clipper.clipFile(args);
                            } catch (Throwable t) {
                                return ThrowableHandler.handleJobErr(t, clipper);
                            }
                            try {
                                saveIfNeeded(args);
                            } catch (Throwable ignored) {
                            }
                        }
                        return status;
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

    private ENNote createENNote() throws ClassNotFoundException, IOException {
        ENNote args = new ENNoteImpl();

        String value = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_GUID);
        if (StringUtils.isNotBlank(value)) {
            args.getNotebook().setGuid(value);
        }
        String type = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_TYPE);
        if (StringUtils.isNotBlank(type)) {
            args.getNotebook().setType(ENObjectType.forName(type));
        }
        String object = IDialogSettingsUtil.get(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_OBJECT);
        if (args.getNotebook().getType() == ENObjectType.LINKED && StringUtils.isNotBlank(object)) {
            // Should meet the two conditions(linked & non-blank) at the same time
            args.getNotebook().setLinkedObject(ObjectUtil.deserialize(object));
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

    private void saveIfNeeded(final ENNote args) throws IOException {
        if (args.getNotebook().isArgsReset() && !args.getNotebook().isArgsAdopt()) {
            IDialogSettingsUtil.set(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_NAME, args.getNotebook().getName());
            IDialogSettingsUtil.set(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_GUID, args.getNotebook().getGuid());
            IDialogSettingsUtil.set(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_TYPE, args.getNotebook().getType().toString());
            IDialogSettingsUtil.set(PLUGIN_SETTINGS_SECTION_NOTEBOOK, PLUGIN_SETTINGS_KEY_OBJECT, ObjectUtil.serialize(args.getNotebook().getLinkedObject()));
            // uuid maybe not correct here, if will be updated when Configurations opens and Apply clicked
        }
        if (args.isArgsReset() && !args.isArgsAdopt()) {
            IDialogSettingsUtil.set(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_NAME, args.getName());
            IDialogSettingsUtil.set(PLUGIN_SETTINGS_SECTION_NOTE, PLUGIN_SETTINGS_KEY_GUID, args.getGuid());
            // uuid maybe not correct here, if will be updated when Configurations opens and Apply clicked
        }
    }

}
