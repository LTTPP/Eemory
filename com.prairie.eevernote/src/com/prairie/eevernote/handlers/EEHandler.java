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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
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
import com.prairie.eevernote.ui.CaptureView;
import com.prairie.eevernote.ui.ConfigurationsDialog;
import com.prairie.eevernote.ui.HotTextDialog;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.DateTimeUtil;
import com.prairie.eevernote.util.EclipseUtil;
import com.prairie.eevernote.util.FileUtil;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.LogUtil;
import com.prairie.eevernote.util.NumberUtil;

public class EEHandler extends AbstractHandler implements ConstantsUtil, Constants {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CONFIGURATIONS)) {
            configurationsClicked(event);
        } else {
            // check token
            if (StringUtils.isBlank(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN))) {
                int opt = EclipseUtil.openWarningWithMultipleButtons(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), "Token is not configured, please first configure token in Configurations dialog.", ArrayUtils.toArray("Configure", OK_CAPS));
                if (opt == ZERO) {
                    configurationsClicked(event);
                }
                return null;
            }
            // clip
            if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CLIP_TO_EVERNOTE)) {
                // TODO
            } else if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CLIP_SELECTION_TO_EVERNOTE)) {
                clipSelectionClicked(event);
            } else if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CLIP_FILE_TO_EVERNOTE)) {
                clipFileClicked(event);
            } else if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CLIP_SCREENSHOT_TO_EVERNOTE)) {
                clipScreenshotClicked(event);
            }
        }
        return null;
    }

    public void clipFileClicked(final ExecutionEvent event) throws ExecutionException {
        try {
            final ENNote args = createENNote();

            int option = HotTextDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
            if (option == HotTextDialog.OK) {
                args.adopt(HotTextDialog.getThis().getQuickSettings());
            } else if (option == HotTextDialog.CANCEL) {
                return;
            }

            args.setAttachments(EclipseUtil.getSelectedFiles(event));
            if (StringUtils.isBlank(args.getName())) {
                args.setName(FileUtil.concatNameOfFiles(args.getAttachments()));
            }

            Job job = new Job(Messages.getString(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.getString(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE), TWO);
                    EEClipper clipper = null;
                    try {
                        clipper = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false);
                        monitor.worked(ONE);

                        if (monitor.isCanceled()) {
                            return LogUtil.cancel();
                        }
                        clipper.clipFile(args);
                        monitor.worked(TWO);
                    } catch (EDAMNotFoundException e) {
                        // try to auto fix EDAMNotFoundException
                        boolean fixed = new EDAMNotFoundHandler(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN)).fixNotFoundException(e, args);
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

    public void clipSelectionClicked(final ExecutionEvent event) throws ExecutionException {
        try {
            final ENNote args = createENNote();

            int option = HotTextDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
            if (option == HotTextDialog.OK) {
                args.adopt(HotTextDialog.getThis().getQuickSettings());
            } else if (option == HotTextDialog.CANCEL) {
                return;
            }

            IEditorPart editor = HandlerUtil.getActiveEditor(event);
            StyledText styledText = (StyledText) editor.getAdapter(Control.class);
            if (StringUtils.isBlank(args.getName())) {
                args.setName(editor.getTitle() + MINUS + DateTimeUtil.timestamp());
            }
            args.setContent(EclipseUtil.getSelectedStyleText(styledText));

            Job job = new Job(Messages.getString(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.getString(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE), TWO);
                    EEClipper clipper = null;
                    try {
                        clipper = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false);
                        monitor.worked(ONE);

                        if (monitor.isCanceled()) {
                            return LogUtil.cancel();
                        }
                        clipper.clipSelection(args);
                        monitor.worked(TWO);
                    } catch (EDAMNotFoundException e) {
                        boolean fixed = new EDAMNotFoundHandler(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN)).fixNotFoundException(e, args);
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

    public void clipScreenshotClicked(final ExecutionEvent event) throws ExecutionException {
        try {
            final ENNote args = createENNote();

            int option = HotTextDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
            if (option == HotTextDialog.OK) {
                args.adopt(HotTextDialog.getThis().getQuickSettings());
            } else if (option == HotTextDialog.CANCEL) {
                return;
            }

            Thread.sleep(NumberUtil.number(FOUR, ZERO, ZERO)); // wait for right click menu to hide

            final BufferedImage screenshot = CaptureView.showView();
            if (screenshot == null) {
                return;
            }
            final File file = File.createTempFile(DateTimeUtil.formatCurrentTime(FileNamePartSimpleDateFormat), FILENAME_DELIMITER + IMG_PNG);
            if (StringUtils.isBlank(args.getName())) {
                args.setName(DateTimeUtil.timestamp() + FILENAME_DELIMITER + IMG_PNG);
            }
            args.setAttachments(ListUtil.list(file));

            Job job = new Job(Messages.getString(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(Messages.getString(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE), THREE);
                    EEClipper clipper = null;
                    try {
                        ImageIO.write(screenshot, IMG_PNG, file);
                        monitor.worked(ONE);

                        clipper = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false);
                        monitor.worked(TWO);

                        if (monitor.isCanceled()) {
                            return LogUtil.cancel();
                        }
                        clipper.clipFile(args);
                        monitor.worked(THREE);
                    } catch (EDAMNotFoundException e) {
                        boolean fixed = new EDAMNotFoundHandler(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN)).fixNotFoundException(e, args);
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

    public void configurationsClicked(final ExecutionEvent event) throws ExecutionException {
        ConfigurationsDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
    }

    private ENNote createENNote() {
        ENNote args = new ENNoteImpl();

        String value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID);
        if (!StringUtils.isBlank(value)) {
            args.getNotebook().setGuid(value);
        }
        value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_NAME);
        if (!StringUtils.isBlank(value)) {
            args.getNotebook().setName(value);
        }

        value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID);
        if (!StringUtils.isBlank(value)) {
            args.setGuid(value);
        }
        value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_NAME);
        if (!StringUtils.isBlank(value)) {
            args.setName(value);
        }

        value = IDialogSettingsUtil.get(SETTINGS_SECTION_TAGS, SETTINGS_KEY_NAME);
        if (!StringUtils.isBlank(value)) {
            args.setTags(ListUtil.toList(value.split(ConstantsUtil.TAGS_SEPARATOR)));
        }
        value = IDialogSettingsUtil.get(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_NAME);
        if (!StringUtils.isBlank(value)) {
            args.setComments(value);
        }

        return args;
    }

    private void saveIfNeeded(final ENNote args) {
        if (args.getNotebook().isGuidReset() && !args.getNotebook().isGuidAdopt()) {
            IDialogSettingsUtil.set(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID, args.getNotebook().getGuid());
        }
        if (args.isGuidReset() && !args.isGuidAdopt()) {
            IDialogSettingsUtil.set(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID, args.getGuid());
        }
    }

}
