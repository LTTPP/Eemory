package com.lttpp.eemory.client.impl;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Resource;
import com.evernote.thrift.TException;
import com.lttpp.eemory.Messages;
import com.lttpp.eemory.client.NoteOps;
import com.lttpp.eemory.client.StoreClientFactory;
import com.lttpp.eemory.client.metadata.EDAMLimits;
import com.lttpp.eemory.client.model.ENNote;
import com.lttpp.eemory.enml.ENML;
import com.lttpp.eemory.exception.EDAMDataModel;
import com.lttpp.eemory.exception.NoDataFoundException;
import com.lttpp.eemory.util.EvernoteUtil;
import com.lttpp.eemory.util.FileUtil;
import com.lttpp.eemory.util.ListUtil;

public class NoteOpsFileImpl extends NoteOps {

    public NoteOpsFileImpl(final StoreClientFactory factory) {
        super(factory);
    }

    @Override
    public void updateOrCreate(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, NoDataFoundException {
        if (ListUtil.isNullOrEmptyList(args.getAttachments())) {
            throw new NoDataFoundException(Messages.Plugin_Error_NoFile);
        }
        if (shouldUpdate(args)) {
            update(args);
        } else {
            create(args);
        }
    }

    private void create(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {
        Note note = new Note();
        note.setTitle(StringUtils.abbreviate(args.getName(), EDAMLimits.EDAM_NOTE_TITLE_LEN_MAX));
        if (StringUtils.isNotBlank(args.getNotebook().getGuid())) {
            note.setNotebookGuid(args.getNotebook().getGuid());
        }

        ENML enml = new ENML();
        enml.setTabWidth(args.getTabWidth());
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
            tagName = tagName.trim();
            if (StringUtils.isNotBlank(tagName)) {
                note.addToTagNames(tagName);
            }
        }

        getNoteStoreClient(args).createNote(note);
    }

    private void update(final ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException {
        NoteStoreClient client = getNoteStoreClient(args);

        Note note = client.getNote(args.getGuid(), true, false, false, false);
        if (!note.isActive()) {
            EDAMNotFoundException e = new EDAMNotFoundException();
            e.setIdentifier(EDAMDataModel.Note_noteGuid.toString());
            e.setKey(args.getGuid());
            throw e;
        }

        ENML enml = new ENML(note.getContent());
        enml.setTabWidth(args.getTabWidth());
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
