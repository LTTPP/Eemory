package com.prairie.eevernote.client;

import java.io.File;
import java.util.List;

import com.prairie.eevernote.enml.StyleText;

public interface ClipperArgs {

	public abstract String getNotebookGuid();

	public abstract void setNotebookGuid(String notebookGuid);

	public abstract String getNoteGuid();

	public abstract void setNoteGuid(String noteGuid);

	public abstract String getTags();

	public abstract void setTags(String tags);

	public abstract String getComments();

	public abstract void setComments(String comments);

	public abstract List<File> getFiles();

	public abstract void setFiles(List<File> files);

	public abstract String getTitle();

	public abstract void setTitle(String title);

	public abstract List<List<StyleText>> getStyleText();

	public abstract void setStyleText(List<List<StyleText>> styleText);

}