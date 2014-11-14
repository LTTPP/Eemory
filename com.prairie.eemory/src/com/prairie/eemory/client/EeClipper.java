package com.prairie.eemory.client;

import java.util.List;
import java.util.Map;

public abstract class EeClipper {

    private boolean valid = true;

    public abstract void clipFile(ENNote args) throws Exception;

    public abstract void clipSelection(ENNote args) throws Exception;

    public abstract Map<String, String> listNotebooks() throws Exception;

    public abstract Map<String, ENNote> listNotesWithinNotebook(ENNote args) throws Exception;

    public abstract List<String> listTags() throws Exception;

    public boolean isValid() {
        return valid;
    };

    public void setInvalid() {
        valid = false;
    };

}
