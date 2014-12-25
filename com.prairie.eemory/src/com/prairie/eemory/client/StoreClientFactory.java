package com.prairie.eemory.client;

import com.evernote.auth.EvernoteAuth;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.thrift.TException;
import com.evernote.thrift.transport.TTransportException;
import com.prairie.eemory.EemoryPlugin;
import com.prairie.eemory.Messages;
import com.prairie.eemory.exception.OutOfDateException;
import com.prairie.eemory.util.EvernoteUtil;

public class StoreClientFactory {

    private final ClientFactory factory;

    private NoteStoreClient noteStoreClient;
    private UserStoreClient userStoreClient;

    public StoreClientFactory(final String token) throws TException, OutOfDateException {
        factory = auth(token);
        checkVersion();
    }

    private ClientFactory auth(final String token) {
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteUtil.evernoteService(), token);
        return new ClientFactory(evernoteAuth);
    }

    private void checkVersion() throws TException, OutOfDateException {
        UserStoreClient userStore = getUserStoreClient();
        boolean versionOk = userStore.checkVersion(EemoryPlugin.getName(), com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR, com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
        if (!versionOk) {
            throw new OutOfDateException(Messages.Plugin_Error_OutOfDate);
        }
    }

    public NoteStoreClient getNoteStoreClient() throws EDAMUserException, EDAMSystemException, TException {
        if (noteStoreClient == null) {
            synchronized (StoreClientFactory.class) {
                if (noteStoreClient == null) {
                    noteStoreClient = factory.createNoteStoreClient();
                }
            }
        }
        return noteStoreClient;
    }

    public NoteStoreClient getLinkedNoteStoreClient(final LinkedNotebook linkedNotebook) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
        return factory.createLinkedNoteStoreClient(linkedNotebook).getClient();
    }

    public UserStoreClient getUserStoreClient() throws TTransportException {
        if (userStoreClient == null) {
            synchronized (StoreClientFactory.class) {
                if (userStoreClient == null) {
                    userStoreClient = factory.createUserStoreClient();
                }
            }
        }
        return userStoreClient;
    }

}
