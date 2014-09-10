package com.prairie.eevernote.handlers;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEPlugin;
import com.prairie.eevernote.EEProperties;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.EEClipperFactory;
import com.prairie.eevernote.client.ENNote;
import com.prairie.eevernote.client.impl.ENNoteImpl;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.ui.CaptureView;
import com.prairie.eevernote.ui.ConfigurationsDialog;
import com.prairie.eevernote.ui.HotTextDialog;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.DateTimeUtil;
import com.prairie.eevernote.util.EclipseUtil;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.LogUtil;

public class EEHandler extends AbstractHandler implements ConstantsUtil, Constants {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CONFIGURATIONS)) {
            configurationsClicked(event);
        } else {
            // check token
            if (StringUtils.isBlank(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN))) {
                MessageDialog.openWarning(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), "Token is not configured, please first configure token in Configurations dialog.");
                return null;
            }
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

            Job job = new Job(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE), IProgressMonitor.UNKNOWN);
                    try {
                        EEClipper clipper = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false);
                        clipper.clipFile(args);
                    } catch (EDAMNotFoundException e) {
                        // try to auto fix EDAMNotFoundException
                        boolean fixed = new EDAMNotFoundHandler(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN)).fixNotFoundException(e, args);
                        if (fixed) {
                            try {
                                EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false).clipFile(args);
                            } catch (Throwable e1) {
                                return LogUtil.error(e1);
                            }
                            saveIfNeeded(args);
                            return LogUtil.ok();
                        }
                        return LogUtil.error(e);
                    } catch (Throwable e) {
                        return LogUtil.error(e);
                    }
                    monitor.done();
                    return LogUtil.ok();
                }
            };
            job.setUser(true);
            job.schedule();

        } catch (OutOfDateException e) {
            LogUtil.logError(e);
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtils.EMPTY + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE));
        } catch (Throwable e) {
            LogUtil.logError(e);
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), ExceptionUtils.getRootCauseMessage(e));
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
            args.setName(editor.getTitle() + MINUS + DateTimeUtil.timestamp());
            args.setContent(EclipseUtil.getSelectedStyleText(styledText));

            Job job = new Job(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE), IProgressMonitor.UNKNOWN);
                    try {
                        EEClipper clipper = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false);
                        clipper.clipSelection(args);
                    } catch (EDAMNotFoundException e) {
                        boolean fixed = new EDAMNotFoundHandler(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN)).fixNotFoundException(e, args);
                        if (fixed) {
                            try {
                                EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false).clipFile(args);
                            } catch (Throwable e1) {
                                return LogUtil.error(e1);
                            }
                            saveIfNeeded(args);
                            return LogUtil.ok();
                        }
                        return LogUtil.error(e);
                    } catch (final Throwable e) {
                        return LogUtil.error(e);
                    }
                    monitor.done();
                    return LogUtil.ok();
                }
            };
            job.setUser(true);
            job.schedule();

        } catch (OutOfDateException e) {
            LogUtil.logError(e);
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtils.EMPTY + EEPlugin.getVersion() + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_OUTOFDATEMESSAGE));
        } catch (Throwable e) {
            LogUtil.logError(e);
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), ExceptionUtils.getRootCauseMessage(e));
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

            BufferedImage screenshot = CaptureView.showView();
            if (screenshot == null) {
                return;
            }
            // 2014-02-21T18-35-32
            final File file = File.createTempFile(DateTimeUtil.formatCurrentTime(FileNamePartSimpleDateFormat), FILENAME_DELIMITER + IMG_PNG);
            ImageIO.write(screenshot, IMG_PNG, file);
            args.setAttachments(ListUtil.list(file));
            args.setName(DateTimeUtil.timestamp() + FILENAME_DELIMITER + IMG_PNG);

            Job job = new Job(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE), IProgressMonitor.UNKNOWN);
                    try {
                        EEClipper clipper = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false);
                        clipper.clipFile(args);
                    } catch (EDAMNotFoundException e) {
                        boolean fixed = new EDAMNotFoundHandler(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN)).fixNotFoundException(e, args);
                        if (fixed) {
                            try {
                                EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false).clipFile(args);
                            } catch (Throwable e1) {
                                return LogUtil.error(e1);
                            }
                            saveIfNeeded(args);
                            return LogUtil.ok();
                        }
                        return LogUtil.error(e);
                    } catch (Throwable e) {
                        return LogUtil.error(e);
                    }
                    monitor.done();
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                    return LogUtil.ok();
                }
            };
            job.setUser(true);
            job.schedule();

        } catch (OutOfDateException e) {
            LogUtil.logError(e);
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtils.EMPTY + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE));
        } catch (Throwable e) {
            LogUtil.logError(e);
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public void configurationsClicked(final ExecutionEvent event) throws ExecutionException {
        ConfigurationsDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
    }

    private ENNote createENNote() throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
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
