package com.prairie.eevernote.client;

import java.util.Map;


public abstract class EEClipper {

	public abstract void clipFile(ClipperArgs args) throws Exception;

	public abstract void clipSelection(ClipperArgs args) throws Exception;

	public abstract Map<String, String> listNotebooks() throws Exception;

	public abstract Map<String, String> listNotesWithinNotebook(ClipperArgs args) throws Exception;

	public abstract String[] listTags() throws Exception;

}
