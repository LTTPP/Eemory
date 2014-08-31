package com.prairie.eevernote.handlers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEPlugin;
import com.prairie.eevernote.EEProperties;
import com.prairie.eevernote.ErrorMessage;
import com.prairie.eevernote.client.ClipperArgs;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.EEClipperFactory;
import com.prairie.eevernote.client.impl.ClipperArgsImpl;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.ui.CaptureView;
import com.prairie.eevernote.ui.ConfigurationsDialog;
import com.prairie.eevernote.ui.HotTextDialog;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.DateTimeUtil;
import com.prairie.eevernote.util.EclipseUtil;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.MapUtil;
import com.prairie.eevernote.util.StringUtil;

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
            final ClipperArgs args = createClipperArgs();

            int option = HotTextDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
            if (option == HotTextDialog.OK) {
                hotsetClipperArgs(args, HotTextDialog.getThis().getQuickSettings());
            } else if (option == HotTextDialog.CANCEL) {
                return;
            }

            args.setFiles(EclipseUtil.getSelectedFiles(event));

            final EEClipper clipper = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false);

            Job job = new Job(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE), IProgressMonitor.UNKNOWN);
                    try {
                        clipper.clipFile(args);
                        monitor.subTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_SUBTASK_MESSAGE));
                    } catch (Throwable e) {
                        return new Status(Status.ERROR, EEPlugin.PLUGIN_ID, e.getLocalizedMessage());
                    }
                    monitor.done();
                    return Status.OK_STATUS;
                }
            };
            job.setUser(true);
            job.schedule();

        } catch (OutOfDateException e) {
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtils.EMPTY + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE));
        } catch (Throwable e) {
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), e.getLocalizedMessage());
        }
    }

    public void clipSelectionClicked(final ExecutionEvent event) throws ExecutionException {
        try {
            final ClipperArgs args = createClipperArgs();

            int option = HotTextDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
            if (option == HotTextDialog.OK) {
                hotsetClipperArgs(args, HotTextDialog.getThis().getQuickSettings());
            } else if (option == HotTextDialog.CANCEL) {
                return;
            }

            IEditorPart editor = HandlerUtil.getActiveEditor(event);
            StyledText styledText = (StyledText) editor.getAdapter(Control.class);
            args.setTitle(editor.getTitle());
            args.setStyleText(EclipseUtil.getSelectedStyleText(styledText));

            final EEClipper clipper = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false);

            Job job = new Job(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE)) {

                @Override
                protected IStatus run(final IProgressMonitor monitor) {

                    monitor.beginTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE), IProgressMonitor.UNKNOWN);
                    try {
                        clipper.clipSelection(args);
                        monitor.subTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_SUBTASK_MESSAGE));
                    } catch (final Throwable e) {
                        return new Status(Status.ERROR, EEPlugin.PLUGIN_ID, e.getLocalizedMessage());
                    }
                    monitor.done();

                    return Status.OK_STATUS;
                }

            };
            job.setUser(true);
            job.schedule();

        } catch (OutOfDateException e) {
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtils.EMPTY + EEPlugin.getVersion() + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_OUTOFDATEMESSAGE));
        } catch (Throwable e) {
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), e.getLocalizedMessage());
        }
    }

    public void clipScreenshotClicked(final ExecutionEvent event) throws ExecutionException {
        try {
            final ClipperArgs args = createClipperArgs();

            int option = HotTextDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
            if (option == HotTextDialog.OK) {
                hotsetClipperArgs(args, HotTextDialog.getThis().getQuickSettings());
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
            args.setFiles(ListUtil.list(file));

            final EEClipper clipper = EEClipperFactory.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN), false);

            Job job = new Job(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE)) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    monitor.beginTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE), IProgressMonitor.UNKNOWN);
                    try {
                        monitor.subTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_SUBTASK_MESSAGE));
                        clipper.clipFile(args);
                    } catch (Throwable e) {
                        return new Status(Status.ERROR, EEPlugin.PLUGIN_ID, ErrorMessage.getMessage(e));
                    }
                    monitor.done();
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                    return Status.OK_STATUS;
                }
            };
            job.setUser(true);
            job.schedule();

        } catch (OutOfDateException e) {
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtils.EMPTY + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE));
        } catch (Throwable e) {
            MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), e.getLocalizedMessage());
        }
    }

    public void configurationsClicked(final ExecutionEvent event) throws ExecutionException {
        ConfigurationsDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
    }

    private ClipperArgs createClipperArgs() throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
        ClipperArgs args = new ClipperArgsImpl();

        String value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID);
        if (!StringUtil.isNullOrEmptyOrBlank(value)) {
            args.setNotebookGuid(value);
        }
        value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID);
        if (!StringUtil.isNullOrEmptyOrBlank(value)) {
            args.setNoteGuid(value);
        }
        value = IDialogSettingsUtil.get(SETTINGS_SECTION_TAGS, SETTINGS_KEY_NAME);
        if (!StringUtil.isNullOrEmptyOrBlank(value)) {
            args.setTags(value);
        }
        value = IDialogSettingsUtil.get(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_NAME);
        if (!StringUtil.isNullOrEmptyOrBlank(value)) {
            args.setComments(value);
        }

        return args;
    }

    private void hotsetClipperArgs(final ClipperArgs args, final Map<String, String> values) {
        if (!MapUtil.isNullOrEmptyMap(values)) {
            if (!StringUtil.isNullOrEmptyOrBlank(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK))) {
                args.setNotebookGuid(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK));
            }
            if (!StringUtil.isNullOrEmptyOrBlank(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE))) {
                args.setNoteGuid(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE));
            }
            if (!StringUtil.isNullOrEmptyOrBlank(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS))) {
                args.setTags(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS));
            }
            if (!StringUtil.isNullOrEmptyOrBlank(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS))) {
                args.setComments(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS));
            }
        }
    }

}
