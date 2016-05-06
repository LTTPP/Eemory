package com.lttpp.eemory.client.impl;

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
import com.lttpp.eemory.Constants;
import com.lttpp.eemory.client.EeClipper;
import com.lttpp.eemory.client.StoreClientFactory;
import com.lttpp.eemory.client.impl.model.ENNoteImpl;
import com.lttpp.eemory.client.impl.model.ENObjectImpl;
import com.lttpp.eemory.client.metadata.ENObjectType;
import com.lttpp.eemory.client.model.ENNote;
import com.lttpp.eemory.client.model.ENObject;
import com.lttpp.eemory.exception.NoDataFoundException;
import com.lttpp.eemory.exception.OutOfDateException;
import com.lttpp.eemory.util.ConstantsUtil;
import com.lttpp.eemory.util.ListStringizer;
import com.lttpp.eemory.util.ListUtil;
import com.lttpp.eemory.util.MapUtil;
import com.lttpp.eemory.util.StringUtil;

public class EeClipperImpl extends EeClipper {

    private final StoreClientFactory factory;

    /**
     * create a Clipper instance which can be reused.
     * 
     * @param token
     *            used to auth Evernote
     */
    public EeClipperImpl(final String token) throws TException, OutOfDateException {
        factory = new StoreClientFactory(token);
    }

    /**
     * Clip the file(s) as attachment to Evernote.
     * 
     * @param args
     *            all things needed to clip file, such as notebook guid, note
     *            guid, tags, comments and file itself.
     */
    @Override
    public void clipFile(final ENNote args) throws NoSuchAlgorithmException, EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, IOException, ParserConfigurationException, SAXException, NoDataFoundException {
        new NoteOpsFileImpl(factory).updateOrCreate(args);
    }

    /**
     * Clip the selection to Evernote.
     *
     * @param args
     *            all things needed to clip file, such as notebook guid, note
     *            guid, tags, comments and selection itself.
     */
    @Override
    public void clipSelection(final ENNote args) throws DOMException, EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException, NoDataFoundException {
        new NoteOpsTextImpl(factory).updateOrCreate(args);
    }

    /**
     * return a user's all notebooks(including linked notebooks).
     *
     * @return The user's notebooks.
     */
    @Override
    public Map<String, ENObject> listNotebooks() throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
        List<Notebook> notebooks = factory.getNoteStoreClient().listNotebooks();
        List<LinkedNotebook> linkedNotebooks = factory.getNoteStoreClient().listLinkedNotebooks();

        Map<String, ENObject> map = MapUtil.map();
        for (Notebook n : notebooks) {
            map.put(n.getName(), ENObjectImpl.forNameAndGuid(n.getName(), n.getGuid()));
        }

        for (LinkedNotebook linkedNotebook : linkedNotebooks) { // LinkedNotebook must be serializable
            NoteStoreClient linkedNoteStore = factory.getLinkedNoteStoreClient(linkedNotebook);
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
     */
    @Override
    public Map<String, ENNote> listNotesWithinNotebook(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
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

        NoteStoreClient client = factory.getNoteStoreClient();
        if (args.getNotebook().getType() == ENObjectType.LINKED) {
            // args.getNotebook().getLinkedObject() should NOT be null
            client = factory.getLinkedNoteStoreClient((LinkedNotebook) args.getNotebook().getLinkedObject());
        }

        List<NoteMetadata> noteList = ListUtil.list();
        int offset = 0, pageSize = com.evernote.edam.limits.Constants.EDAM_USER_NOTES_MAX;
        NotesMetadataList notesMetadataList = null;
        do {
            notesMetadataList = client.findNotesMetadata(filter, offset, pageSize, resultSpec);
            noteList.addAll(notesMetadataList.getNotes());
            offset = offset + notesMetadataList.getNotesSize();
        } while (notesMetadataList != null && offset < notesMetadataList.getTotalNotes());

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
     */
    @Override
    public List<String> listTags() throws EDAMUserException, EDAMSystemException, TException {
        return ListUtil.toStringList(factory.getNoteStoreClient().listTags(), new ListStringizer() {
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
        if (factory == null) {
            return false;
        }
        try {
            factory.getNoteStoreClient().getDefaultNotebook();
        } catch (Throwable e) {
            if (e instanceof TTransportException) {
                return false;
            }
            /*
             * // As new StoreClientFactory(token); will not identify
             * auth-expired exception, so the following code is not needed. if
             * (e instanceof EDAMUserException) { if (((EDAMUserException)
             * e).getErrorCode() == EDAMErrorCode.AUTH_EXPIRED) { return false;
             * } }
             */
        }
        return true;
    }

}
