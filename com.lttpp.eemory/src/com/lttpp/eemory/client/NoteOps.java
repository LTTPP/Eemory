package com.lttpp.eemory.client;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.thrift.TException;
import com.lttpp.eemory.client.metadata.ENObjectType;
import com.lttpp.eemory.client.model.ENNote;


public abstract class NoteOps {

    private final StoreClientFactory factory;

    public NoteOps(final StoreClientFactory factory) {
        this.factory = factory;
    }

    public abstract void updateOrCreate(ENNote args) throws Exception;

    protected NoteStoreClient getNoteStoreClient(final ENNote args) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
        NoteStoreClient client;
        if (args.getNotebook().getType() == ENObjectType.LINKED) {
            // args.getNotebook().getLinkedObject() should NOT be null
            client = factory.getLinkedNoteStoreClient((LinkedNotebook) args.getNotebook().getLinkedObject());
        } else {
            client = factory.getNoteStoreClient();
        }
        return client;
    }

}
