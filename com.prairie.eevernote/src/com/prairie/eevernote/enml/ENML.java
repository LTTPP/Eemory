package com.prairie.eevernote.enml;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.util.EnmlUtil;
import com.prairie.eevernote.util.StringUtil;

public class ENML implements Constants {

	private String existingEnml = StringUtil.EMPTY;
	private Note note;

	public ENML() {
		note = new Note();
	}

	public ENML(String enml) {
		this.existingEnml = enml;
	}

	private Div resource(String hashHex, String mimeType) {
		Media media = new Media();
		media.setAlign(Alignment.LEFT.name());
		media.setHash(hashHex);
		media.setType(mimeType);

		Div div = new Div();
		div.addMedia(media);
		return div;
	}

	private Div comments(String comments) {
		if (StringUtil.nullOrEmptyOrBlankString(comments)) {
			return null;
		}
		Div div = new Div();
		div.addText(comments + COLON);
		return div;
	}

	public void addResource(String hashHex, String mimeType) {
		if (!StringUtil.nullOrEmptyOrBlankString(hashHex)) {
			note.addDiv(resource(hashHex, mimeType));
		}
	}

	public void addComment(String comments) {
		if (!StringUtil.nullOrEmptyOrBlankString(comments)) {
			note.addDiv(comments(comments));
		}
	}

	public void addSnippet(String snippet) {
		note.addSnippet(snippet);
	}

	public String get() throws ParserConfigurationException, SAXException, IOException {
		if (!StringUtil.nullOrEmptyOrBlankString(existingEnml)) {
			String beginPart = existingEnml.substring(0, existingEnml.indexOf(NOTE_START) + NOTE_START.length());
			existingEnml = existingEnml.replace(beginPart, beginPart + note);
			EnmlUtil.validate(existingEnml);
			return existingEnml;
		} else {
			String newEnml = XML_DECLARATION + DOCTYPE_DECLARATION + note;
			EnmlUtil.validate(newEnml);
			return newEnml;
		}
	}
}
