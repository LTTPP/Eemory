package com.prairie.eevernote.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.CharEncoding;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.thrift.TException;
import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEPlugin;
import com.prairie.eevernote.EEProperties;
import com.prairie.eevernote.exception.OutOfDateException;

public class EvernoteUtil implements Constants {

	public static NoteStoreClient getNoteStoreClient(String token) throws TException, OutOfDateException, EDAMUserException, EDAMSystemException {
		ClientFactory factory = auth(token);
		checkVersion(factory);
		return factory.createNoteStoreClient();
	}

	private static ClientFactory auth(String token) {
		EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token);
		return new ClientFactory(evernoteAuth);
	}

	private static void checkVersion(ClientFactory factory) throws TException, OutOfDateException {
		UserStoreClient userStore = factory.createUserStoreClient();
		boolean versionOk = userStore.checkVersion(EEPlugin.getName(), com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR, com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
		if (!versionOk) {
			throw new OutOfDateException(EEProperties.getProperties().getProperty(Constants.EECLIPPERPLUGIN_EECLIPPERIMPL_EXCEPTION_MESSAGE));
		}
	}

	public static Resource createResource(File file, String mimeType) throws NoSuchAlgorithmException, IOException {
		Resource resource = new Resource();
		resource.setData(readFileAsData(file));
		resource.setMime(mimeType);
		ResourceAttributes attributes = new ResourceAttributes();
		attributes.setFileName(file.getName());
		resource.setAttributes(attributes);
		return resource;
	}

	private static Data readFileAsData(File file) throws IOException, NoSuchAlgorithmException {
		// Read the full binary contents of the file
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byte[] block = new byte[10240];
		int len;
		while ((len = in.read(block)) >= 0) {
			byteOut.write(block, 0, len);
		}
		in.close();
		byte[] body = byteOut.toByteArray();

		// Create a new Data object to contain the file contents
		Data data = new Data();
		data.setSize(body.length);
		data.setBodyHash(MessageDigest.getInstance(Constants.MD5).digest(body));
		data.setBody(body);

		return data;
	}

	public static void validateENML(String enml) throws ParserConfigurationException, SAXException, IOException {
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
