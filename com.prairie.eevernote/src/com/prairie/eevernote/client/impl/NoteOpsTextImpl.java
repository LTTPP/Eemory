package com.prairie.eevernote.client.impl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;
import com.prairie.eevernote.client.ClipperArgs;
import com.prairie.eevernote.client.NoteOps;
import com.prairie.eevernote.enml.ENML;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.StringUtil;

public class NoteOpsTextImpl extends NoteOps {

    private final NoteStoreClient noteStoreClient;

    public NoteOpsTextImpl(final NoteStoreClient noteStoreClient) {
        this.noteStoreClient = noteStoreClient;
    }

    @Override
    public void updateOrCreate(final ClipperArgs args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException, TransformerException, DOMException, OutOfDateException {
        if (ListUtil.isNullOrEmptyList(args.getStyleText())) {
            return;
        }
        if (shouldUpdate(args)) {
            update(args);
        } else {
            create(args);
        }
    }

    private void create(final ClipperArgs args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, ParserConfigurationException, SAXException, IOException, TransformerException, OutOfDateException {
        Note note = new Note();
        note.setTitle(args.getTitle());
        if (!StringUtil.isNullOrEmptyOrBlank(args.getNotebookGuid())) {
            note.setNotebookGuid(args.getNotebookGuid());
        }

        ENML enml = new ENML();
        enml.addComment(args.getComments());
        enml.addContent(args.getStyleText());

        note.setContent(enml.get());

        if (!StringUtil.isNullOrEmptyOrBlank(args.getTags())) {
            note.setTagNames(ListUtil.toList(args.getTags().split(ConstantsUtil.TAGS_SEPARATOR)));
        }

        noteStoreClient.createNote(note);
    }

    private void update(final ClipperArgs args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, DOMException, ParserConfigurationException, SAXException, IOException, TransformerException, OutOfDateException {
        Note note = noteStoreClient.getNote(args.getNoteGuid(), true, false, false, false);
        note.unsetResources();

        // update content
        ENML enml = new ENML(note.getContent());
        enml.addComment(args.getComments());
        enml.addContent(args.getStyleText());

        note.setContent(enml.get());

        // update tags
        if (!StringUtil.isNullOrEmptyOrBlank(args.getTags())) {
            String[] tagNames = args.getTags().split(ConstantsUtil.TAGS_SEPARATOR);
            for (String tagName : tagNames) {
                note.addToTagNames(tagName);
            }
        }

        noteStoreClient.updateNote(note);
    }

    private boolean shouldUpdate(final ClipperArgs args) {
        return !StringUtil.isNullOrEmptyOrBlank(args.getNoteGuid());
    }

}
