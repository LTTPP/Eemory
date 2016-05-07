package org.lttpp.eemory.client;

import java.util.List;
import java.util.Map;

import org.lttpp.eemory.client.model.ENNote;
import org.lttpp.eemory.client.model.ENObject;

public abstract class EeClipper {

    private boolean valid = true;

    public abstract void clipFile(ENNote args) throws Exception;

    public abstract void clipSelection(ENNote args) throws Exception;

    public abstract Map<String, ENObject> listNotebooks() throws Exception;

    public abstract Map<String, ENNote> listNotesWithinNotebook(ENNote args) throws Exception;

    public abstract List<String> listTags() throws Exception;

    public boolean isValid() {
        return valid;
    };

    public void setInvalid() {
        valid = false;
    };

}
