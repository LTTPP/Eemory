package com.prairie.eevernote.client.impl;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.thrift.TException;
import com.prairie.eevernote.ErrorMessage.EvernoteDataModel;
import com.prairie.eevernote.client.EDAMLimits;
import com.prairie.eevernote.client.ENNote;
import com.prairie.eevernote.client.NoteOps;
import com.prairie.eevernote.enml.ENML;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.util.EvernoteUtil;
import com.prairie.eevernote.util.FileUtil;
import com.prairie.eevernote.util.ListUtil;

public class NoteOpsFileImpl extends NoteOps {

    private final NoteStoreClient noteStoreClient;

    public NoteOpsFileImpl(final NoteStoreClient noteStoreClient) {
        this.noteStoreClient = noteStoreClient;
    }

    @Override
    public void updateOrCreate(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException, OutOfDateException {
        if (ListUtil.isNullOrEmptyList(args.getAttachments())) {
            return;
        }
        if (shouldUpdate(args)) {
            update(args);
        } else {
            create(args);
        }
    }

    private void create(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException, OutOfDateException {
        Note note = new Note();
        note.setTitle(!StringUtils.isBlank(args.getName()) ? args.getName() : FileUtil.concatNameOfFiles(args.getAttachments(), EDAMLimits.EDAM_NOTE_TITLE_LEN_MAX));
        if (!StringUtils.isBlank(args.getNotebook().getGuid())) {
            note.setNotebookGuid(args.getNotebook().getGuid());
        }

        ENML enml = new ENML();
        enml.addComment(args.getComments());

        for (File f : args.getAttachments()) {
            // create resource
            String mimeType = FileUtil.mimeType(f); // E.g "image/png"
            Resource resource = EvernoteUtil.createResource(f, mimeType);
            note.addToResources(resource);

            // create content
            String hashHex = FileUtil.bytesToHex(resource.getData().getBodyHash());
            enml.addResource(hashHex, mimeType);
        }

        note.setContent(enml.get());

        // create tags
        for (String tagName : args.getTags()) {
            if (!StringUtils.isBlank(tagName)) {
                note.addToTagNames(tagName);
            }
        }

        noteStoreClient.createNote(note);
    }

    private void update(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException, OutOfDateException {
        Note note = noteStoreClient.getNote(args.getGuid(), true, false, false, false);
        if (!note.isActive()) {
            EDAMNotFoundException e = new EDAMNotFoundException();
            e.setIdentifier(EvernoteDataModel.Note_noteGuid.toString());
            e.setKey(args.getGuid());
            throw e;
        }

        ENML enml = new ENML(note.getContent());
        // update content
        enml.addComment(args.getComments());

        // update resource
        Iterator<File> iter = args.getAttachments().iterator();
        while (iter.hasNext()) {
            File file = iter.next();
            String mimeType = FileUtil.mimeType(file); // E.g "image/png"
            Resource resource = EvernoteUtil.createResource(file, mimeType);
            note.addToResources(resource);

            String hashHex = FileUtil.bytesToHex(resource.getData().getBodyHash());
            enml.addResource(hashHex, mimeType);
        }

        note.setContent(enml.get());

        // update tags
        for (String tagName : args.getTags()) {
            if (!StringUtils.isBlank(tagName)) {
                note.addToTagNames(tagName);
            }
        }

        noteStoreClient.updateNote(note);
    }

    private boolean shouldUpdate(final ENNote args) {
        return !StringUtils.isBlank(args.getGuid());
    }

}
