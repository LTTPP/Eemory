package org.lttpp.eemory.client.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.lttpp.eemory.Messages;
import org.lttpp.eemory.client.NoteOps;
import org.lttpp.eemory.client.StoreClientFactory;
import org.lttpp.eemory.client.metadata.EDAMLimits;
import org.lttpp.eemory.client.model.ENNote;
import org.lttpp.eemory.enml.ENML;
import org.lttpp.eemory.exception.EDAMDataModel;
import org.lttpp.eemory.exception.NoDataFoundException;
import org.lttpp.eemory.util.ListUtil;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;

public class NoteOpsTextImpl extends NoteOps {

    public NoteOpsTextImpl(final StoreClientFactory factory) {
        super(factory);
    }

    @Override
    public void updateOrCreate(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException, DOMException, NoDataFoundException {
        if (ListUtil.isNullOrEmptyList(args.getContent())) {
            throw new NoDataFoundException(Messages.Plugin_Error_NoText);
        }
        if (shouldUpdate(args)) {
            update(args);
        } else {
            create(args);
        }
    }

    private void create(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException {
        Note note = new Note();
        note.setTitle(StringUtils.abbreviate(args.getName(), EDAMLimits.EDAM_NOTE_TITLE_LEN_MAX));
        if (StringUtils.isNotBlank(args.getNotebook().getGuid())) {
            note.setNotebookGuid(args.getNotebook().getGuid());
        }

        ENML enml = new ENML();
        enml.setTabWidth(args.getTabWidth());
        enml.addComment(args.getComments());
        enml.addContent(args.getContent());

        note.setContent(enml.get());

        for (String tagName : args.getTags()) {
            tagName = tagName.trim();
            if (StringUtils.isNotBlank(tagName)) {
                note.addToTagNames(tagName);
            }
        }

        getNoteStoreClient(args).createNote(note);
    }

    private void update(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, DOMException, ParserConfigurationException, SAXException, IOException {
        NoteStoreClient client = getNoteStoreClient(args);

        Note note = client.getNote(args.getGuid(), true, false, false, false);
        if (!note.isActive()) {
            EDAMNotFoundException e = new EDAMNotFoundException();
            e.setIdentifier(EDAMDataModel.Note_noteGuid.toString());
            e.setKey(args.getGuid());
            throw e;
        }
        note.unsetResources();

        // update content
        ENML enml = new ENML(note.getContent());
        enml.setTabWidth(args.getTabWidth());
        enml.addComment(args.getComments());
        enml.addContent(args.getContent());

        note.setContent(enml.get());

        // update tags
        for (String tagName : args.getTags()) {
            tagName = tagName.trim();
            if (StringUtils.isNotBlank(tagName)) {
                note.addToTagNames(tagName);
            }
        }

        note.setUpdated(System.currentTimeMillis());

        client.updateNote(note);
    }

    private boolean shouldUpdate(final ENNote args) {
        return StringUtils.isNotBlank(args.getGuid());
    }

}
