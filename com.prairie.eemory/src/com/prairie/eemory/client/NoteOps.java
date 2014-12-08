package com.prairie.eemory.client;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.thrift.TException;
import com.prairie.eemory.exception.OutOfDateException;


public abstract class NoteOps {

    private final String token;

    public NoteOps(final String token) {
        this.token = token;
    }

    public abstract void updateOrCreate(ENNote args) throws Exception;

    protected NoteStoreClient getNoteStoreClient(final ENNote args) throws EDAMUserException, EDAMSystemException, TException, OutOfDateException, EDAMNotFoundException {
        NoteStoreClient client;
        if (args.getNotebook().getType() == ENObjectType.LINKED) {
            // args.getNotebook().getLinkedObject() should NOT be null
            client = StoreClientFactory.getInstance(token).getLinkedNoteStoreClient((LinkedNotebook) args.getNotebook().getLinkedObject());
        } else {
            client = StoreClientFactory.getInstance(token).getNoteStoreClient();
        }
        return client;
    }

}
