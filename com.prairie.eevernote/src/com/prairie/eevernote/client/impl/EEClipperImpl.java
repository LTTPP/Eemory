package com.prairie.eevernote.client.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

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
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.TException;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.ENNote;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.EvernoteUtil;
import com.prairie.eevernote.util.ListStringizer;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.MapStringizer;
import com.prairie.eevernote.util.MapUtil;

public class EEClipperImpl extends EEClipper {

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
    public EEClipperImpl(final String token) throws TException, OutOfDateException, EDAMUserException, EDAMSystemException {
        noteStoreClient = EvernoteUtil.getNoteStoreClient(token);
    }

    /**
     * Clip the file(s) as attachment to Evernote.
     *
     * @param args
     *            all things needed to clip file, such as notebook guid, note
     *            guid, tags, comments and file itself.
     * @throws OutOfDateException
     *             This plug-in is out of date
     * @throws TransformerException
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
    public void clipFile(final ENNote args) throws NoSuchAlgorithmException, EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, IOException, ParserConfigurationException, SAXException, TransformerException, OutOfDateException {
        new NoteOpsFileImpl(noteStoreClient).updateOrCreate(args);
    }

    /**
     * Clip the selection to Evernote.
     *
     * @param args
     *            all things needed to clip file, such as notebook guid, note
     *            guid, tags, comments and selection itself.
     * @throws OutOfDateException
     *             This plug-in is out of date
     * @throws TransformerException
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
     *
     */
    @Override
    public void clipSelection(final ENNote args) throws DOMException, EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException, TransformerException, OutOfDateException {
        new NoteOpsTextImpl(noteStoreClient).updateOrCreate(args);
    }

    /**
     * return a user's all notebooks.
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
     */
    @Override
    public Map<String, String> listNotebooks() throws EDAMUserException, EDAMSystemException, TException, OutOfDateException {
        // List the notes in the user's account
        List<Notebook> notebooks = noteStoreClient.listNotebooks();
        return ListUtil.toStringMap(notebooks, new MapStringizer() {
            @Override
            public String key(final Object o) {
                return ((Notebook) o).getName();
            }

            @Override
            public String value(final Object o) {
                return ((Notebook) o).getGuid();
            }
        });
    }

    /**
     * return a user's all notes inside the specified notebook.
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
        if (!StringUtils.isBlank(args.getNotebook().getGuid())) {
            filter.setNotebookGuid(args.getNotebook().getGuid());
        }
        if (!StringUtils.isBlank(args.getName())) {
            filter.setWords("intitle" + ConstantsUtil.COLON + ConstantsUtil.DOUBLE_QUOTATION_MARK + args.getName() + ConstantsUtil.DOUBLE_QUOTATION_MARK);
        }

        NotesMetadataResultSpec resultSpec = new NotesMetadataResultSpec();
        resultSpec.setIncludeTitle(true);

        NotesMetadataList notesMetadataList = noteStoreClient.findNotesMetadata(filter, ConstantsUtil.ZERO, com.evernote.edam.limits.Constants.EDAM_USER_NOTES_MAX, resultSpec);
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

}
