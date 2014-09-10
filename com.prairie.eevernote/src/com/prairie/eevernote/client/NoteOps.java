package com.prairie.eevernote.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import com.prairie.eevernote.exception.OutOfDateException;

public abstract class NoteOps {

    public abstract void updateOrCreate(ENNote args) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException, NoSuchAlgorithmException, IOException, ParserConfigurationException, SAXException, TransformerException, OutOfDateException;

}
