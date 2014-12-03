package com.prairie.eemory.client;

import java.util.Map;

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
import com.prairie.eemory.util.MapUtil;

public class StoreClientFactory {

    private static Map<String, StoreClientFactory> instanceMap = MapUtil.map();

    private final ClientFactory factory;

    private NoteStoreClient noteStoreClient;
    private UserStoreClient userStoreClient;

    private StoreClientFactory(final String token) throws TException, OutOfDateException {
        factory = auth(token);
        checkVersion();
    }

    public static StoreClientFactory getInstance(final String token) throws TException, OutOfDateException {
        if (instanceMap.get(token) == null) {
            synchronized (instanceMap) {
                if (instanceMap.get(token) == null) {
                    instanceMap.put(token, new StoreClientFactory(token));
                }
            }
        }
        return instanceMap.get(token);
    }

    private ClientFactory auth(final String token) {
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteUtil.evernoteService(), token);
        return new ClientFactory(evernoteAuth);
    }

    private void checkVersion() throws TException, OutOfDateException {
        UserStoreClient userStore = factory.createUserStoreClient();
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
