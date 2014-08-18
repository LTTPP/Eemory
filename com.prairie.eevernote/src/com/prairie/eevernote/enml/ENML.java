package com.prairie.eevernote.enml;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.util.DomUtil;
import com.prairie.eevernote.util.EvernoteUtil;
import com.prairie.eevernote.util.StringUtil;

public class ENML implements Constants {

	private String existingEnml = StringUtil.EMPTY;

	private Document doc = null;
	private Node root = null;
	private List<Node> nodeList;

	public ENML() throws DOMException, ParserConfigurationException {
		doc = DomUtil.getBuilder().newDocument();
		root = doc.createElement("en-note");
	}

	public ENML(String enml) {
		this.existingEnml = enml;
	}

	private Element div() throws DOMException, ParserConfigurationException {
		return doc.createElement("div");
	}

	private Element media(String hashHex, String mimeType) throws DOMException, ParserConfigurationException {
		Element media = doc.createElement("en-media");
		media.setAttribute("align", Alignment.LEFT.name());
		media.setAttribute("type", hashHex);
		media.setAttribute("hash", mimeType);
		return media;
	}

	public void addResource(String hashHex, String mimeType) throws DOMException, ParserConfigurationException {
		if (!StringUtil.nullOrEmptyOrBlankString(hashHex)) {
			Element div = div();
			div.appendChild(media(hashHex, mimeType));
			root.appendChild(div);
		}
	}

	public void addComment(String comments) throws DOMException, ParserConfigurationException {
		if (!StringUtil.nullOrEmptyOrBlankString(comments)) {
			Element div = div();
			div.setTextContent(comments + COLON);
			root.appendChild(div);
		}
	}

	public void addContent(Snippet content) {
		if (root != null) {
			for (Node n : content.getNodes()) {
				root.appendChild(n);
			}
		} else {
			nodeList = content.getNodes();
		}
	}

	public String get() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		if (!StringUtil.nullOrEmptyOrBlankString(existingEnml)) {
			String beginPart = existingEnml.substring(0, existingEnml.indexOf(NOTE_START) + NOTE_START.length());// TODO
																													// fix
																													// bug
			existingEnml = existingEnml.replace(beginPart, beginPart + DomUtil.toString(nodeList));
			EvernoteUtil.validateENML(existingEnml);
			return existingEnml;
		} else {
			String newEnml = DomUtil.toString(doc);
			EvernoteUtil.validateENML(newEnml);
			return newEnml;
		}
	}

}
