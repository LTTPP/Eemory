package com.prairie.eevernote.client.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.util.ArrayUtil;
import com.prairie.eevernote.util.MapUtil;

public class EEClipperNop extends EEClipper {

	@Override
	public void clipFile(List<File> files) throws Exception {

	}

	@Override
	public void clipSelection(String selection, String title) throws Exception {

	}

	@Override
	public Map<String, String> listNotebooks() throws Exception {
		return MapUtil.map();
	}

	@Override
	public Map<String, String> listNotesWithinNotebook(String notebookGuid) throws Exception {
		return MapUtil.map();
	}

	@Override
	public String[] listTags() throws Exception {
		return ArrayUtil.stringArray();
	}

	@Override
	public void setNotebookGuid(String notebookGuid) {

	}

	@Override
	public void setNoteGuid(String noteGuid) {

	}

	@Override
	public void setTags(String tags) {

	}

	@Override
	public void setComments(String comments) {

	}

}
