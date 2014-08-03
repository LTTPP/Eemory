package com.prairie.eevernote.client;

import java.io.File;
import java.util.List;
import java.util.Map;

public abstract class EEClipper {

	public abstract void clipFile(List<File> file) throws Exception;

	public abstract void clipSelection(String selection, String title) throws Exception;

	public abstract Map<String, String> listNotebooks() throws Exception;

	public abstract Map<String, String> listNotesWithinNotebook(String notebookGuid) throws Exception;

	public abstract String[] listTags() throws Exception;

	public abstract void setNotebookGuid(String notebookGuid);

	public abstract void setNoteGuid(String noteGuid);

	public abstract void setTags(String tags);

	public abstract void setComments(String comments);

}
