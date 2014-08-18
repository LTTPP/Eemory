package com.prairie.eevernote.util;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.CharEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.prairie.eevernote.Constants;

public class DomUtil implements Constants {

	public static String toString(Node node) throws TransformerException {
		return toString(node, true);
	}

	public static String toString(Document node) throws TransformerException {
		return toString(node, false);
	}

	public static String toString(List<Node> nodeList) throws TransformerException {
		return toString(nodeList, true);
	}

	public static String toString(List<Node> nodeList, boolean omitDeclaration) throws TransformerException {
		String string = StringUtil.EMPTY;
		for (Node n : nodeList) {
			string += toString(n, omitDeclaration);
		}
		return string;
	}

	public static String toString(Node node, boolean omitDeclaration) throws TransformerException {
		Transformer transformer = getTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, CharEncoding.UTF_8);
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitDeclaration ? "yes" : "no");

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(output);
		transformer.transform(new DOMSource(node), result);

		return output.toString();
	}

	public static Transformer getTransformer() throws TransformerConfigurationException {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		return transformer;
	}

	public static DocumentBuilder getBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder;
	}

}
