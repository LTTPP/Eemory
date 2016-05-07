package org.lttpp.eemory.client;

import org.lttpp.eemory.EemoryPlugin;
import org.lttpp.eemory.Messages;
import org.lttpp.eemory.exception.OutOfDateException;
import org.lttpp.eemory.util.EvernoteUtil;

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
