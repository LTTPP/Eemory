package com.prairie.eevernote.client;

import java.util.List;
import java.util.Map;



public abstract class EEClipper {

    public abstract void clipFile(ENNote args) throws Exception;

    public abstract void clipSelection(ENNote args) throws Exception;

    public abstract Map<String, String> listNotebooks() throws Exception;

    public abstract Map<String, String> listNotesWithinNotebook(ENNote args) throws Exception;

    public abstract List<String> listTags() throws Exception;

}
