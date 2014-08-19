package com.prairie.eevernote.handlers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
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
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.EEClipperManager;
import com.prairie.eevernote.enml.StyleText;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.ui.CaptureView;
import com.prairie.eevernote.ui.ConfigurationsDialog;
import com.prairie.eevernote.ui.HotTextDialog;
import com.prairie.eevernote.util.EclipseUtil;
import com.prairie.eevernote.util.FileUtil;
import com.prairie.eevernote.util.IDialogSettingsUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.MapUtil;
import com.prairie.eevernote.util.StringUtil;

public class EEHandler extends AbstractHandler implements Constants {

	protected ISelection selection;
	protected File file;

	public EEHandler() {

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CLIP_TO_EVERNOTE)) {
			// TODO
		} else if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CLIP_SELECTION_TO_EVERNOTE)) {
			clipSelectionClicked(event);
		} else if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CLIP_FILE_TO_EVERNOTE)) {
			clipFileClicked(event);
		} else if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CLIP_SCREENSHOT_TO_EVERNOTE)) {
			clipScreenshotClicked(event);
		} else if (event.getCommand().getId().equals(EEPLUGIN_COMMAND_ID_CONFIGURATIONS)) {
			configurationsClicked(event);
		}
		return null;
	}

	// \/\/\/\/\/\/\/\/\/ Actions \/\/\/\/\/\/\/\/\/
	public void clipFileClicked(final ExecutionEvent event) throws ExecutionException {
		try {
			final EEClipper clipper = this.getEEClipper();
			int option = HotTextDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
			if (option == HotTextDialog.OK) {
				this.setClipperArgs(clipper, HotTextDialog.getThis().getQuickSettings());
			} else if (option == HotTextDialog.CANCEL) {
				return;
			}

			final List<File> files = EclipseUtil.getSelectedFiles(event);

			Job job = new Job(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE)) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE), IProgressMonitor.UNKNOWN);
					try {
						clipper.clipFile(files);
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
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtil.EMPTY + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE));
		} catch (Throwable e) {
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), e.getLocalizedMessage());
		}
	}

	public void clipSelectionClicked(final ExecutionEvent event) throws ExecutionException {
		try {
			final EEClipper clipper = this.getEEClipper();
			int option = HotTextDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
			if (option == HotTextDialog.OK) {
				this.setClipperArgs(clipper, HotTextDialog.getThis().getQuickSettings());
			} else if (option == HotTextDialog.CANCEL) {
				return;
			}

			final IEditorPart editor = HandlerUtil.getActiveEditor(event);
			StyledText styledText = (StyledText) editor.getAdapter(Control.class);
			final List<List<StyleText>> styleText = EclipseUtil.getStyleText(styledText);

			Job job = new Job(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE)) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {

					monitor.beginTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE), IProgressMonitor.UNKNOWN);
					try {
						clipper.clipSelection(styleText, editor.getTitle());
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
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtil.EMPTY + EEPlugin.getVersion() + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_OUTOFDATEMESSAGE));
		} catch (Throwable e) {
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), e.getLocalizedMessage());
		}
	}

	public void clipScreenshotClicked(final ExecutionEvent event) throws ExecutionException {
		try {
			final EEClipper clipper = this.getEEClipper();
			int option = HotTextDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
			if (option == HotTextDialog.OK) {
				this.setClipperArgs(clipper, HotTextDialog.getThis().getQuickSettings());
			} else if (option == HotTextDialog.CANCEL) {
				return;
			}

			BufferedImage screenshot = CaptureView.showView();
			if (screenshot == null) {
				return;
			}
			final File file = File.createTempFile(FileUtil.tempFileName(), FILENAME_DELIMITER + IMG_PNG);
			ImageIO.write(screenshot, IMG_PNG, file);

			final List<File> files = ListUtil.list();
			files.add(file);

			Job job = new Job(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE)) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_MESSAGE), IProgressMonitor.UNKNOWN);
					try {
						monitor.subTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_SUBTASK_MESSAGE));
						clipper.clipFile(files);
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
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtil.EMPTY + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE));
		} catch (Throwable e) {
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), e.getLocalizedMessage());
		}
	}

	public void configurationsClicked(ExecutionEvent event) throws ExecutionException {
		ConfigurationsDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
	}

	protected EEClipper getEEClipper() throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
		final EEClipper clipper = EEClipperManager.getInstance().getEEClipper(IDialogSettingsUtil.get(Constants.SETTINGS_KEY_TOKEN));

		String value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTEBOOK, SETTINGS_KEY_GUID);
		if (!StringUtil.nullOrEmptyOrBlankString(value)) {
			clipper.setNotebookGuid(value);
		}
		value = IDialogSettingsUtil.get(SETTINGS_SECTION_NOTE, SETTINGS_KEY_GUID);
		if (!StringUtil.nullOrEmptyOrBlankString(value)) {
			clipper.setNoteGuid(value);
		}
		value = IDialogSettingsUtil.get(SETTINGS_SECTION_TAGS, SETTINGS_KEY_NAME);
		if (!StringUtil.nullOrEmptyOrBlankString(value)) {
			clipper.setTags(value);
		}
		value = IDialogSettingsUtil.get(SETTINGS_SECTION_COMMENTS, SETTINGS_KEY_NAME);
		if (!StringUtil.nullOrEmptyOrBlankString(value)) {
			clipper.setComments(value);
		}

		return clipper;
	}

	protected void setClipperArgs(EEClipper clipper, Map<String, String> values) {
		if (!MapUtil.nullOrEmptyMap(values)) {
			if (!StringUtil.nullOrEmptyOrBlankString(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK))) {
				clipper.setNotebookGuid(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_NOTEBOOK));
			}
			if (!StringUtil.nullOrEmptyOrBlankString(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE))) {
				clipper.setNoteGuid(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_NOTE));
			}
			if (!StringUtil.nullOrEmptyOrBlankString(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS))) {
				clipper.setTags(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_TAGS));
			}
			if (!StringUtil.nullOrEmptyOrBlankString(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS))) {
				clipper.setComments(values.get(EECLIPPERPLUGIN_CONFIGURATIONS_COMMENTS));
			}
		}
	}

}
