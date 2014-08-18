package com.prairie.eevernote.util;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

public class EclipseUtil {

	public static List<File> getSelectedFiles(ExecutionEvent event) {
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
			IEditorPart editorPart = HandlerUtil.getActiveEditor(event);
			IFile iFile = (IFile) ((editorPart.getEditorInput().getAdapter(IFile.class)));
			if (iFile != null) {// TODO iFile == null: how to handle this
								// case in XML file
				File file = iFile.getLocation().makeAbsolute().toFile();
				files.add(file);
			}
		}

		return files;
	}

}
