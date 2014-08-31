package com.prairie.eevernote.client.impl;

import java.io.File;
import java.util.List;

import com.prairie.eevernote.client.ClipperArgs;
import com.prairie.eevernote.enml.StyleText;

public class ClipperArgsImpl implements ClipperArgs {

	/* parent Notebook is optional; if omitted, default notebook is used */
	private String notebookGuid;
	/*
	 * existing Note is optional; if omitted, create a new note; otherwise
	 * append
	 */
	private String noteGuid;

	// new added
	private String title;
	private List<List<StyleText>> styleText;
	private List<File> files;
	private String tags;
	private String comments;

	/**
	 * Create a <code>ClipperArgs</code> instance using given notebook guid.
	 *
	 * @param guid
	 * @return
	 */
	public static ClipperArgs forNotebookGuid(String guid) {
		ClipperArgs args = new ClipperArgsImpl();
		args.setNotebookGuid(guid);
		return args;
	}

	@Override
	public String getNotebookGuid() {
		return notebookGuid;
	}

	@Override
	public void setNotebookGuid(String notebookGuid) {
		this.notebookGuid = notebookGuid;
	}

	@Override
	public String getNoteGuid() {
		return noteGuid;
	}

	@Override
	public void setNoteGuid(String noteGuid) {
		this.noteGuid = noteGuid;
	}

	@Override
	public String getTags() {
		return tags;
	}

	@Override
	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public String getComments() {
		return comments;
	}

	@Override
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public List<File> getFiles() {
		return files;
	}

	@Override
	public void setFiles(List<File> files) {
		this.files = files;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public List<List<StyleText>> getStyleText() {
		return styleText;
	}

	@Override
	public void setStyleText(List<List<StyleText>> styleText) {
		this.styleText = styleText;
	}

}
