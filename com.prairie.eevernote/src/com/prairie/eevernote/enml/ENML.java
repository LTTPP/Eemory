package com.prairie.eevernote.enml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.util.DomUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.StringUtil;

public class ENML implements Constants {

	// used to create new note
	private Document document;
	private Node root;
	// used for updating existing note
	private String existingEnml = StringUtil.EMPTY;
	private List<Node> newAddedNodes;

	public ENML() throws DOMException, ParserConfigurationException {
		document = DomUtil.getBuilder().newDocument();
		document.setXmlStandalone(true);

		root = document.createElement("en-note");
		document.appendChild(root);

		newAddedNodes = ListUtil.list();
	}

	public ENML(String enml) {
		this.existingEnml = enml;
		newAddedNodes = ListUtil.list();
	}

	public void addResource(String hashHex, String mimeType) throws DOMException, ParserConfigurationException {
		if (!StringUtil.nullOrEmptyOrBlankString(hashHex)) {
			Element div = div();
			div.appendChild(media(hashHex, mimeType));
			newAddedNodes.add(div);
		}
	}

	public void addComment(String comments) throws DOMException, ParserConfigurationException {
		if (!StringUtil.nullOrEmptyOrBlankString(comments)) {
			Element div = div();
			//div.setTextContent(comments + COLON);
			div.appendChild (document.createCDATASection (comments + COLON));
			newAddedNodes.add(div);
		}
	}

	public void addContent(List<List<StyleText>> content) throws DOMException, ParserConfigurationException {
		List<Node> list = parse(content);
		newAddedNodes.addAll(list);
	}

	public String get() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		if (isNewCreated()) {
			for (Node n : newAddedNodes) {
				root.appendChild(n);
			}
			String newEnml = DomUtil.toString(document);
			validateENML(newEnml);
			return newEnml;
		} else {
			String beginPart = existingEnml.substring(0, existingEnml.indexOf(NOTE_START) + NOTE_START.length());// TODO
			existingEnml = existingEnml.replace(beginPart, beginPart + DomUtil.toString(newAddedNodes));
			validateENML(existingEnml);
			return existingEnml;
		}
	}

	private boolean isNewCreated() {
		return document != null && root != null && StringUtil.nullOrEmptyOrBlankString(existingEnml);
	}

	private Element div() throws DOMException, ParserConfigurationException {
		return document.createElement("div");
	}

	private Element media(String hashHex, String mimeType) throws DOMException, ParserConfigurationException {
		Element media = document.createElement("en-media");
		media.setAttribute("align", Alignment.LEFT.name());
		media.setAttribute("type", hashHex);
		media.setAttribute("hash", mimeType);
		return media;
	}

	private List<Node> parse(List<List<StyleText>> styleTextBlocks) throws DOMException, ParserConfigurationException {
		List<Node> list = ListUtil.list();
		for (List<StyleText> lineBlocks : styleTextBlocks) {
			list.add(div(lineBlocks));
		}
		return list;
	}

	private Node div(List<StyleText> styleTextBlocks) throws DOMException, ParserConfigurationException {
		Element div = document.createElement("div");
		for (StyleText styletext : styleTextBlocks) {
			String escapedXml = StringUtil.escapeEnml(styletext.getText());
			div.appendChild(font(escapedXml, styletext.getFace(), styletext.getColorHexCode(), styletext.getSize(), styletext.getFontStyle()));
		}
		return div;
	}

	private Node font(String text, String face, String color, String size, FontStyle fontStyle) throws DOMException, ParserConfigurationException {
		Element font = document.createElement("font");
		font.appendChild(span(text, size, fontStyle));
		font.setAttribute("face", face);
		font.setAttribute("color", color);
		font.setAttribute("size", String.valueOf(TWO));
		return font;
	}

	private Node span(String text, String size, FontStyle fontStyle) throws DOMException, ParserConfigurationException {
		Element span = document.createElement("span");
		if (StringUtil.nullOrEmptyString(text)) {
			span.appendChild(document.createElement("br"));
		} else {
			if (fontStyle == FontStyle.BOLD) {
				span.appendChild(b(text));
			} else if (fontStyle == FontStyle.ITALIC) {
				span.appendChild(i(text));
			} else if (fontStyle == FontStyle.NORMAL) {
				span.appendChild(text(text));
			}
		}
		span.setAttribute("style", "font-size:" + size + "pt");
		return span;
	}

	private Node b(String text) throws DOMException, ParserConfigurationException {
		Node b = document.createElement("b");
		//b.setTextContent(text);
		b.appendChild (document.createCDATASection (text));
		return b;
	}

	private Node i(String text) throws DOMException, ParserConfigurationException {
		Node b = document.createElement("i");
		//b.setTextContent(text);
		b.appendChild (document.createCDATASection (text));
		return b;
	}

	private Node text(String text) throws DOMException, ParserConfigurationException {
		//return document.createTextNode(text);
		return document.createCDATASection (text);
	}

	public void validateENML(String enml) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		SAXParser parser = factory.newSAXParser();
		XMLReader reader = parser.getXMLReader();
		reader.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				if (systemId.endsWith(ENML_DTD)) {
					return new InputSource(getClass().getResourceAsStream(ENML_DTD_LOCATION));
				} else if (systemId.endsWith(XHTML_1_0_LATIN_1_ENT)) {
					return new InputSource(getClass().getResourceAsStream(XHTML_1_0_LATIN_1_ENT_LOCATION));
				} else if (systemId.endsWith(XHTML_1_0_SYMBOL_ENT)) {
					return new InputSource(getClass().getResourceAsStream(XHTML_1_0_SYMBOL_ENT_LOCATION));
				} else if (systemId.endsWith(XHTML_1_0_SPECIAL_ENT)) {
					return new InputSource(getClass().getResourceAsStream(XHTML_1_0_SPECIAL_ENT_LOCATION));
				} else {
					return null;
				}
			}
		});
		reader.setErrorHandler(new ErrorHandler() {
			@Override
			public void warning(SAXParseException exception) throws SAXException {
				throw exception;
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				throw exception;
			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				throw exception;
			}
		});
		reader.parse(new InputSource(new ByteArrayInputStream(enml.getBytes(CharEncoding.UTF_8))));
	}

}
