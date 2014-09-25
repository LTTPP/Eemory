package com.prairie.eevernote.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Data;
import com.evernote.edam.type.Resource;
import com.evernote.edam.type.ResourceAttributes;
import com.evernote.thrift.TException;
import com.prairie.eevernote.Constants;
import com.prairie.eevernote.EEPlugin;
import com.prairie.eevernote.Messages;
import com.prairie.eevernote.exception.OutOfDateException;

public class EvernoteUtil {

    public static NoteStoreClient getNoteStoreClient(final String token) throws TException, OutOfDateException, EDAMUserException, EDAMSystemException {
        ClientFactory factory = auth(token);
        checkVersion(factory);
        return factory.createNoteStoreClient();
    }

    private static ClientFactory auth(final String token) {
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token);
        return new ClientFactory(evernoteAuth);
    }

    private static void checkVersion(final ClientFactory factory) throws TException, OutOfDateException {
        UserStoreClient userStore = factory.createUserStoreClient();
        boolean versionOk = userStore.checkVersion(EEPlugin.getName(), com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR, com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
        if (!versionOk) {
            throw new OutOfDateException(Messages.getString(Constants.PLUGIN_ERROR_OOD));
        }
    }

    public static Resource createResource(final File file, final String mimeType) throws NoSuchAlgorithmException, IOException {
        Resource resource = new Resource();
        resource.setData(readFileAsData(file));
        resource.setMime(mimeType);
        ResourceAttributes attributes = new ResourceAttributes();
        attributes.setFileName(file.getName());
        resource.setAttributes(attributes);
        return resource;
    }

    private static Data readFileAsData(final File file) throws IOException, NoSuchAlgorithmException {
        // Read the full binary contents of the file
        FileInputStream in = new FileInputStream(file);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        byte[] block = new byte[10240];
        int len;
        while ((len = in.read(block)) >= 0) {
            byteOut.write(block, 0, len);
        }
        in.close();
        byte[] body = byteOut.toByteArray();

        // Create a new Data object to contain the file contents
        Data data = new Data();
        data.setSize(body.length);
        data.setBodyHash(MessageDigest.getInstance(ConstantsUtil.MD5).digest(body));
        data.setBody(body);

        return data;
    }

}
