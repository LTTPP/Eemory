package com.prairie.eemory.client.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.SharedNotebook;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.TException;
import com.evernote.thrift.transport.TTransportException;
import com.prairie.eemory.Constants;
import com.prairie.eemory.client.ENNote;
import com.prairie.eemory.client.ENObject;
import com.prairie.eemory.client.ENObjectType;
import com.prairie.eemory.client.EeClipper;
import com.prairie.eemory.client.StoreClientFactory;
import com.prairie.eemory.exception.OutOfDateException;
import com.prairie.eemory.util.ConstantsUtil;
import com.prairie.eemory.util.ListStringizer;
import com.prairie.eemory.util.ListUtil;
import com.prairie.eemory.util.MapUtil;
import com.prairie.eemory.util.StringUtil;

public class EeClipperImpl extends EeClipper {

    private final String token;
    private final NoteStoreClient noteStoreClient;

    /**
     * create a Clipper instance which can be reused.
     *
     * @param token
     *            used to auth Evernote
     * @throws TException
     *             Please refer to Evernote SDK
     * @throws OutOfDateException
     *             This plug-in is out of date
     * @throws EDAMUserException
     *             Please refer to Evernote SDK
     * @throws EDAMSystemException
     *             Please refer to Evernote SDK
     */
    public EeClipperImpl(final String token) throws TException, OutOfDateException, EDAMUserException, EDAMSystemException {
        this.token = token;
        noteStoreClient = StoreClientFactory.getInstance(token).getNoteStoreClient();
    }

    /**
     * Clip the file(s) as attachment to Evernote.
     *
     * @param args
     *            all things needed to clip file, such as notebook guid, note
     *            guid, tags, comments and file itself.
     * @throws OutOfDateException
     *             This plug-in is out of date
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws TException
     *             Please refer to Evernote SDK
     * @throws EDAMNotFoundException
     *             Please refer to Evernote SDK
     * @throws EDAMSystemException
     *             Please refer to Evernote SDK
     * @throws EDAMUserException
     *             Please refer to Evernote SDK
     * @throws NoSuchAlgorithmException
     */
    @Override
    public void clipFile(final ENNote args) throws NoSuchAlgorithmException, EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, IOException, ParserConfigurationException, SAXException, OutOfDateException {
        new NoteOpsFileImpl(token).updateOrCreate(args);
    }

    /**
     * Clip the selection to Evernote.
     *
     * @param args
     *            all things needed to clip file, such as notebook guid, note
     *            guid, tags, comments and selection itself.
     * @throws OutOfDateException
     *             This plug-in is out of date
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TException
     *             Please refer to Evernote SDK
     * @throws EDAMNotFoundException
     *             Please refer to Evernote SDK
     * @throws EDAMSystemException
     *             Please refer to Evernote SDK
     * @throws EDAMUserException
     *             Please refer to Evernote SDK
     * @throws DOMException
     *             Something wrong when parsing ENML
     *
     */
    @Override
    public void clipSelection(final ENNote args) throws DOMException, EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException, OutOfDateException {
        new NoteOpsTextImpl(token).updateOrCreate(args);
    }

    /**
     * return a user's all notebooks(including linked notebooks).
     *
     * @return The user's notebooks.
     *
     * @throws TException
     *             Please refer to Evernote SDK
     * @throws EDAMSystemException
     *             Please refer to Evernote SDK
     * @throws EDAMUserException
     *             Please refer to Evernote SDK
     * @throws OutOfDateException
     *             This plug-in is out of date
     * @throws EDAMNotFoundException
     *             Please refer to Evernote SDK
     */
    @Override
    public Map<String, ENObject> listNotebooks() throws EDAMUserException, EDAMSystemException, TException, OutOfDateException, EDAMNotFoundException {
        List<Notebook> notebooks = noteStoreClient.listNotebooks();
        List<LinkedNotebook> linkedNotebooks = noteStoreClient.listLinkedNotebooks();

        Map<String, ENObject> map = MapUtil.map();
        for (Notebook n : notebooks) {
            map.put(n.getName(), ENObjectImpl.forNameAndGuid(n.getName(), n.getGuid()));
        }

        for (LinkedNotebook linkedNotebook : linkedNotebooks) {
            NoteStoreClient linkedNoteStore = StoreClientFactory.getInstance(token).getLinkedNoteStoreClient(linkedNotebook);
            SharedNotebook sharedNotebook = linkedNoteStore.getSharedNotebookByAuth();

            if (map.containsKey(linkedNotebook.getShareName())) {
                map.put(linkedNotebook.getShareName() + ConstantsUtil.LEFT_PARENTHESIS + linkedNotebook.getUsername() + ConstantsUtil.COLON + sharedNotebook.getNotebookGuid() + ConstantsUtil.RIGHT_PARENTHESIS, ENObjectImpl.forValues(linkedNotebook.getShareName(), sharedNotebook.getNotebookGuid(), ENObjectType.LINKED, linkedNotebook));
            } else {
                map.put(linkedNotebook.getShareName(), ENObjectImpl.forValues(linkedNotebook.getShareName(), sharedNotebook.getNotebookGuid(), ENObjectType.LINKED, linkedNotebook));
            }
        }

        return map;
    }

    /**
     * return a user's all notes(uuid-ENNote) inside the specified notebook.
     *
     * @param args
     *            all things needed to list notes, such as notebook guid.
     * @return The notes in the certain notebook.
     *
     * @throws TException
     *             Please refer to Evernote SDK
     * @throws EDAMNotFoundException
     *             Please refer to Evernote SDK
     * @throws EDAMSystemException
     *             Please refer to Evernote SDK
     * @throws EDAMUserException
     *             Please refer to Evernote SDK
     * @throws OutOfDateException
     *             This plug-in is out of date
     */
    @Override
    public Map<String, ENNote> listNotesWithinNotebook(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, OutOfDateException {
        NoteFilter filter = new NoteFilter();
        filter.setInactive(false);
        if (StringUtils.isNotBlank(args.getNotebook().getGuid())) {
            filter.setNotebookGuid(args.getNotebook().getGuid());
        }
        if (StringUtils.isNotBlank(args.getName())) {
            filter.setWords(Constants.EDAM_SYNTAX_INTITLE + StringUtil.toQuotedString(args.getName()));
        }

        NotesMetadataResultSpec resultSpec = new NotesMetadataResultSpec();
        resultSpec.setIncludeTitle(true);

        NoteStoreClient client = noteStoreClient;
        if (args.getNotebook().getType() == ENObjectType.LINKED) {
            // args.getNotebook().getLinkedObject() should NOT be null
            client = StoreClientFactory.getInstance(token).getLinkedNoteStoreClient((LinkedNotebook) args.getNotebook().getLinkedObject());
        }
        NotesMetadataList notesMetadataList = client.findNotesMetadata(filter, 0, com.evernote.edam.limits.Constants.EDAM_USER_NOTES_MAX, resultSpec);
        List<NoteMetadata> noteList = notesMetadataList.getNotes();

        Map<String, ENNote> map = MapUtil.map();
        for (NoteMetadata n : noteList) {
            if (map.containsKey(n.getTitle())) {
                map.put(n.getTitle() + ConstantsUtil.LEFT_PARENTHESIS + n.getGuid() + ConstantsUtil.RIGHT_PARENTHESIS, ENNoteImpl.forNameAndGuid(n.getTitle(), n.getGuid()));
            } else {
                map.put(n.getTitle(), ENNoteImpl.forNameAndGuid(n.getTitle(), n.getGuid()));
            }
        }
        return map;
    }

    /**
     * return a user's all tags.
     *
     * @return The user's all tags.
     * @throws TException
     *             Please refer to Evernote SDK
     * @throws EDAMSystemException
     *             Please refer to Evernote SDK
     * @throws EDAMUserException
     *             Please refer to Evernote SDK
     */
    @Override
    public List<String> listTags() throws EDAMUserException, EDAMSystemException, TException {
        return ListUtil.toStringList(noteStoreClient.listTags(), new ListStringizer() {
            @Override
            public String element(final Object o) {
                return ((Tag) o).getName();
            }
        });
    }

    @Override
    public boolean isValid() {
        if (!super.isValid()) {
            return false;
        }
        if (noteStoreClient == null) {
            return false;
        }
        try {
            noteStoreClient.getDefaultNotebook();
        } catch (Throwable e) {
            if (e instanceof TTransportException) {
                return false;
            }
        }
        return true;
    }

}
