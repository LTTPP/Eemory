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
import com.prairie.eevernote.client.ClipperArgs;
import com.prairie.eevernote.client.EDAMLimits;
import com.prairie.eevernote.client.NoteOps;
import com.prairie.eevernote.enml.ENML;
import com.prairie.eevernote.exception.OutOfDateException;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.EvernoteUtil;
import com.prairie.eevernote.util.FileUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.StringUtil;

public class NoteOpsFileImpl extends NoteOps {

    private final NoteStoreClient noteStoreClient;

    public NoteOpsFileImpl(final NoteStoreClient noteStoreClient) {
        this.noteStoreClient = noteStoreClient;
    }

    @Override
    public void updateOrCreate(final ClipperArgs args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException, OutOfDateException {
        if (ListUtil.isNullOrEmptyList(args.getFiles())) {
            return;
        }
        if (shouldUpdate(args)) {
            update(args);
        } else {
            create(args);
        }
    }

    private void create(final ClipperArgs args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException, OutOfDateException {
        Note note = new Note();
        note.setTitle(!StringUtils.isBlank(args.getTitle()) ? args.getTitle() : FileUtil.concatNameOfFiles(args.getFiles(), EDAMLimits.EDAM_NOTE_TITLE_LEN_MAX));
        if (!StringUtil.isNullOrEmptyOrBlank(args.getNotebookGuid())) {
            note.setNotebookGuid(args.getNotebookGuid());
        }

        ENML enml = new ENML();
        enml.addComment(args.getComments());

        for (File f : args.getFiles()) {
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
        if (!StringUtil.isNullOrEmptyOrBlank(args.getTags())) {
            note.setTagNames(ListUtil.toList(args.getTags().split(ConstantsUtil.TAGS_SEPARATOR)));
        }

        noteStoreClient.createNote(note);
    }

    private void update(final ClipperArgs args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException, OutOfDateException {
        Note note = noteStoreClient.getNote(args.getNoteGuid(), true, false, false, false);
        if (!note.isActive()) {
            EDAMNotFoundException e = new EDAMNotFoundException();
            e.setIdentifier(EvernoteDataModel.Note_noteGuid.toString());
            e.setKey(args.getNoteGuid());
            throw e;
        }

        ENML enml = new ENML(note.getContent());
        // update content
        enml.addComment(args.getComments());

        // update resource
        Iterator<File> iter = args.getFiles().iterator();
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
