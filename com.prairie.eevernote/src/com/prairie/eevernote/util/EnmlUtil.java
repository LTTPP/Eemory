package com.prairie.eevernote.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.CharEncoding;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.prairie.eevernote.Constants;

public class EnmlUtil implements Constants {

	private static TransformerHandler transformerHandler;

	public static void validate(String enml) throws ParserConfigurationException, SAXException, IOException {
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

	public static TransformerHandler getTransformerHandler() throws TransformerConfigurationException, FileNotFoundException {
		if (transformerHandler == null) {
			SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			transformerHandler = factory.newTransformerHandler();

			OutputStream output = new ByteArrayOutputStream();
			transformerHandler.setResult(new StreamResult(output));

			Transformer transformer = transformerHandler.getTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		}
		return transformerHandler;
	}

}
