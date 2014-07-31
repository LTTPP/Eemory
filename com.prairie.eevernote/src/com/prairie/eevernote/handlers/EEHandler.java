package com.prairie.eevernote.handlers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEPlugin;
import com.prairie.eevernote.EEProperties;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.EEClipperManager;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.util.DateTimeUtil;
import com.prairie.eevernote.util.FileUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.MapUtil;
import com.prairie.eevernote.util.StringUtil;
import com.prairie.eevernote.widgets.CaptureView;
import com.prairie.eevernote.widgets.ConfigurationsDialog;
import com.prairie.eevernote.widgets.HotTextDialog;
import com.prairie.eevernote.widgets.Settings;

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

			ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
			final List<File> files = ListUtil.list();
			if (selection instanceof IStructuredSelection) {
				Iterator<?> iterator = ((StructuredSelection) selection).iterator();
				while (iterator.hasNext()) {
					IFile iFile;
					Object object = iterator.next();
					if (object instanceof IFile) {
						iFile = (IFile) object;
					} else if (object instanceof ICompilationUnit) {
						ICompilationUnit compilationUnit = (ICompilationUnit) object;
						IResource resource = compilationUnit.getResource();
						if (resource instanceof IFile) {
							iFile = (IFile) resource;
						} else {
							continue;
						}
					} else {
						continue;
					}
					File file = iFile.getLocation().makeAbsolute().toFile();
					files.add(file);
				}
			} else if (selection instanceof ITextSelection) {
				IEditorPart editorPart = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActiveEditor();
				IFile iFile = (IFile) ((editorPart.getEditorInput().getAdapter(IFile.class)));
				if (iFile != null) {// TODO iFile == null: how to handle this case in XML file
					File file = iFile.getLocation().makeAbsolute().toFile();
					files.add(file);
				} else {
					return;
				}
			} else {
				return;
			}

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
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtil.STRING_EMPTY + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE));
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

			// For Evaluation
			IEditorPart editor = HandlerUtil.getActiveEditor(event);
			StyledText text = (StyledText) editor.getAdapter(Control.class);
			StyleRange[] r = text.getStyleRanges();
			for (int i =0;i<r.length;i++) {
				System.out.println(r[i].toString());
			}
			// For Evaluation

			Display.getDefault().syncExec(new Runnable() {// TODO to remove display sync
						@Override
						public void run() {
							// Selection
							selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();// TODO
							if (selection instanceof ITextSelection) {
								// File
								IEditorPart editorPart = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActiveEditor();
								IFile iFile = (IFile) ((editorPart.getEditorInput().getAdapter(IFile.class)));
								if (iFile != null) {
									file = iFile.getLocation().makeAbsolute().toFile();
								} else {
									return;
								}
							} else {
								return;
							}
						}
					});

			Job job = new Job(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE)) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {

					monitor.beginTask(EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_MESSAGE), IProgressMonitor.UNKNOWN);
					try {
						clipper.clipSelection(((ITextSelection) selection).getText(), file.getName() + Constants.COLON + DateTimeUtil.timestamp());
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
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtil.STRING_EMPTY + EEPlugin.getVersion() + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDSELECTIONTOEVERNOTE_OUTOFDATEMESSAGE));
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
			final File  file = File.createTempFile(FileUtil.tempFileName(), FILENAME_DELIMITER + IMG_PNG);
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
						return new Status(Status.ERROR, EEPlugin.PLUGIN_ID, e.getLocalizedMessage());
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
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), EEPlugin.getName() + StringUtil.STRING_EMPTY + EEPlugin.getVersion() + EEProperties.getProperties().getProperty(EECLIPPERPLUGIN_ACTIONDELEGATE_ADDFILETOEVERNOTE_OUTOFDATEMESSAGE));
		} catch (Throwable e) {
			MessageDialog.openError(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell(), event.getParameter("com.prairie.eevernote.command.parameter"), e.getLocalizedMessage());
		}
	}

	public void configurationsClicked(ExecutionEvent event) throws ExecutionException {
		ConfigurationsDialog.show(HandlerUtil.getActiveWorkbenchWindowChecked(event).getShell());
	}

	protected EEClipper getEEClipper() throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
		final EEClipper clipper = EEClipperManager.getInstance().getEEClipper(Settings.get(Constants.SETTINGS_KEY_TOKEN));

		if (!StringUtil.nullOrEmptyOrBlankString(Settings.get(Constants.SETTINGS_KEY_NOTEBOOK_GUID))) {
			clipper.setNotebookGuid(Settings.get(Constants.SETTINGS_KEY_NOTEBOOK_GUID));
		}
		if (!StringUtil.nullOrEmptyOrBlankString(Settings.get(Constants.SETTINGS_KEY_NOTE_GUID))) {
			clipper.setNoteGuid(Settings.get(Constants.SETTINGS_KEY_NOTE_GUID));
		}
		if (!StringUtil.nullOrEmptyOrBlankString(Settings.get(Constants.SETTINGS_KEY_TAGS))) {
			clipper.setTags(Settings.get(Constants.SETTINGS_KEY_TAGS));
		}
		if (!StringUtil.nullOrEmptyOrBlankString(Settings.get(Constants.SETTINGS_KEY_COMMENTS))) {
			clipper.setComments(Settings.get(Constants.SETTINGS_KEY_COMMENTS));
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
