package com.prairie.eevernote.client.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.TException;
import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEPlugin;
import com.prairie.eevernote.EEProperties;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.enml.ENML;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.util.FileUtil;
import com.prairie.eevernote.util.ListStringizer;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.MapStringizer;
import com.prairie.eevernote.util.StringUtil;

public class EEClipperImpl extends EEClipper {

	private NoteStoreClient noteStore;

	// parent Notebook is optional; if omitted, default notebook is used
	private String notebookGuid;
	// existing Note is optional; if omitted, create a new note; otherwise append
	private String noteGuid;
	private String tags;
	private String comments;

	private List<Notebook> notebooks;

	public EEClipperImpl(String token) throws TException, EDAMUserException, EDAMSystemException, OutOfDateException {
		ClientFactory factory = auth(token);
		checkVersion(factory);
		noteStore = factory.createNoteStoreClient();
	}

	private ClientFactory auth(String token) {
		EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token);
		return new ClientFactory(evernoteAuth);
	}

	private void checkVersion(ClientFactory factory) throws TException, OutOfDateException {
		UserStoreClient userStore = factory.createUserStoreClient();
		boolean versionOk = userStore.checkVersion(EEPlugin.getName(), com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR, com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
		if (!versionOk) {
			throw new OutOfDateException(EEProperties.getProperties().getProperty(Constants.EECLIPPERPLUGIN_EECLIPPERIMPL_EXCEPTION_MESSAGE));
		}
	}

	/**
	 * Clip the file(s) as attachment to Evernote.
	 *
	 * @throws TException
	 * @throws EDAMNotFoundException
	 * @throws EDAMSystemException
	 * @throws EDAMUserException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Override
	public void clipFile(List<File> files) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {
		if (ListUtil.nullOrEmptyList(files)) {
			return;
		}
		if (shouldUpdateNote()) {
			updateNote(files);
		} else {
			Iterator<File> iter = files.iterator();
			while (iter.hasNext()){
				File file = iter.next();
				createNote(file, file.getName());
			}
		}
	}

	/**
	 * Clip the selection to Evernote.
	 *
	 * @throws TException
	 * @throws EDAMNotFoundException
	 * @throws EDAMSystemException
	 * @throws EDAMUserException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 *
	 */
	@Override
	public void clipSelection(String selection, String title) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException {
		if (StringUtil.nullOrEmptyOrBlankString(selection)) {
			return;
		}
		if (shouldUpdateNote()) {
			updateNote(selection);
		} else {
			createNote(selection, title); // E.g. EEClipperImpl.java:2014-02-21T18:35:32
		}
	}

	private boolean shouldUpdateNote() {
		if (!StringUtil.nullOrEmptyOrBlankString(this.noteGuid)) {
			return true;
		}
		return false;
	}

	private void createNote(File file, String noteTitle) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {
		Note note = new Note();
		note.setTitle(noteTitle);
		// parent Notebook is optional; if omitted, default notebook is used
		if (!StringUtil.nullOrEmptyOrBlankString(this.notebookGuid)) {
			note.setNotebookGuid(this.notebookGuid);
		}

		// create resource
		String mimeType = FileUtil.mimeType(file); // E.g "image/png"
		Resource resource = createResource(file, mimeType);
		note.addToResources(resource);

		// create content
		ENML enml = new ENML();
		String hashHex = FileUtil.bytesToHex(resource.getData().getBodyHash());
		enml.addResource(hashHex, mimeType);
		enml.addComment(comments);
		note.setContent(enml.get());

		// create tags
		if (!StringUtil.nullOrEmptyOrBlankString(tags)) {
			note.setTagNames(ListUtil.arrayToList(tags.split(Constants.TAGS_SEPARATOR)));
		}

		noteStore.createNote(note);
	}

	private void createNote(String selection, String noteTitle) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException {
		// simple text note with syntax highlighting/rich format
		Note note = new Note();
		note.setTitle(noteTitle);
		// notebook is optional; if omitted, default notebook is used
		if (!StringUtil.nullOrEmptyOrBlankString(this.notebookGuid)) {
			note.setNotebookGuid(this.notebookGuid);
		}

		ENML enml = new ENML();
		enml.addSnippet(selection);
		enml.addComment(comments);
		note.setContent(enml.get());

		if (!StringUtil.nullOrEmptyOrBlankString(tags)) {
			note.setTagNames(ListUtil.arrayToList(tags.split(Constants.TAGS_SEPARATOR)));
		}

		noteStore.createNote(note);
	}

	private Resource createResource(File file, String mimeType) throws NoSuchAlgorithmException, IOException {
		Resource resource = new Resource();
		resource.setData(readFileAsData(file));
		resource.setMime(mimeType);
		ResourceAttributes attributes = new ResourceAttributes();
		attributes.setFileName(file.getName());
		resource.setAttributes(attributes);
		return resource;
	}

	private void updateNote(List<File> files) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {
		if (ListUtil.nullOrEmptyList(files)) {
			return;
		}

		Note note = noteStore.getNote(this.noteGuid, true, false, false, false);

		ENML enml = new ENML(note.getContent());

		// update resource
		Iterator<File> iter = files.iterator();
		while (iter.hasNext()) {
			File file = iter.next();
			String mimeType = FileUtil.mimeType(file); // E.g "image/png"
			Resource resource = createResource(file, mimeType);
			note.addToResources(resource);

			String hashHex = FileUtil.bytesToHex(resource.getData().getBodyHash());
			enml.addResource(hashHex, mimeType);
		}

		// update content
		if (!StringUtil.nullOrEmptyOrBlankString(comments)) {
			enml.addComment(comments);
		}
		note.setContent(enml.get());

		// update tags
		if (!StringUtil.nullOrEmptyOrBlankString(tags)) {
			String[] tagNames = tags.split(Constants.TAGS_SEPARATOR);
			for (int i = 0; i < tagNames.length; i++) {
				note.addToTagNames(tagNames[i]);
			}
		}

		noteStore.updateNote(note);
	}

	private void updateNote(String selection) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
		Note note = noteStore.getNote(this.noteGuid, true, false, false, false);
		note.unsetResources();

		// update content
		note.getContent();
		System.out.println(note.getContent());

		// update tags
		if (!StringUtil.nullOrEmptyOrBlankString(tags)) {
			String[] tagNames = tags.split(Constants.TAGS_SEPARATOR);
			for (int i = 0; i < tagNames.length; i++) {
				note.addToTagNames(tagNames[i]);
			}
		}

		noteStore.updateNote(note);
	}

	/**
	 * Helper method to read the contents of a file on disk and create a new Data object.
	 *
	 */
	private Data readFileAsData(File file) throws IOException, NoSuchAlgorithmException {
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

	/**
	 * return a user's all notebooks.
	 *
	 * @return The user's notebooks, filtered by <code>filterString</code>.
	 *
	 * @throws TException
	 * @throws EDAMSystemException
	 * @throws EDAMUserException
	 */
	@Override
	public Map<String, String> listNotebooks() throws EDAMUserException, EDAMSystemException, TException {
		// List the notes in the user's account
		notebooks = noteStore.listNotebooks();
		return ListUtil.toStringMap(notebooks, new MapStringizer() {
			@Override
			public String key(Object o) {
				return ((Notebook) o).getName();
			}

			@Override
			public String value(Object o) {
				return ((Notebook) o).getGuid();
			}
		});
	}

	/**
	 * return a user's all notes inside certain notebook.
	 *
	 * @return The notes in the certain notebook, filtered by
	 *         <code>filterString</code>.
	 *
	 * @throws TException
	 * @throws EDAMNotFoundException
	 * @throws EDAMSystemException
	 * @throws EDAMUserException
	 */
	@Override
	public Map<String, String> listNotesWithinNotebook(String notebookGuid) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
		NotesMetadataList notesMetadataList = new NotesMetadataList();

		NoteFilter filter = new NoteFilter();
		if (!StringUtil.nullOrEmptyOrBlankString(notebookGuid)) {
			filter.setNotebookGuid(notebookGuid);
		}

		NotesMetadataResultSpec resultSpec = new NotesMetadataResultSpec();
		resultSpec.setIncludeTitle(true);

		notesMetadataList = noteStore.findNotesMetadata(filter, 0, com.evernote.edam.limits.Constants.EDAM_USER_NOTES_MAX, resultSpec);

		return ListUtil.toStringMap(notesMetadataList.getNotes(), new MapStringizer() {
			@Override
			public String key(Object o) {
				return ((NoteMetadata) o).getTitle() + Constants.LEFT_PARENTHESIS + ((NoteMetadata) o).getGuid() + Constants.RIGHT_PARENTHESIS;
			}

			@Override
			public String value(Object o) {
				return ((NoteMetadata) o).getGuid();
			}
		});
	}

	/**
	 * return a user's all tags.
	 */
	@Override
	public String[] listTags() throws Exception {
		return ListUtil.toStringArray(this.noteStore.listTags(), new ListStringizer() {
			@Override
			public String element(Object o) {
				return ((Tag) o).getName();
			}
		});
	}

	@Override
	public void setNotebookGuid(String notebookGuid) {
		this.notebookGuid = notebookGuid;
	}

	@Override
	public void setNoteGuid(String noteGuid) {
		this.noteGuid = noteGuid;
	}

	@Override
	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public void setComments(String comments) {
		this.comments = comments;
	}

}
