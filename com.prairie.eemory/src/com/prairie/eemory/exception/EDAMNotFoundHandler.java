package com.prairie.eemory.exception;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;

import com.evernote.edam.error.EDAMNotFoundException;
import com.prairie.eemory.client.EeClipper;
import com.prairie.eemory.client.EeClipperFactory;
import com.prairie.eemory.client.impl.model.ENNoteImpl;
import com.prairie.eemory.client.model.ENNote;
import com.prairie.eemory.client.model.ENObject;
import com.prairie.eemory.util.ListUtil;
import com.prairie.eemory.util.LogUtil;

public class EDAMNotFoundHandler {

    private final String token;

    public EDAMNotFoundHandler(final String token) {
        this.token = token;
    }

    public IStatus fixNotFoundException(final EDAMNotFoundException e, final ENNote args) {
        if (e.getIdentifier().equals(EDAMDataModel.Note_notebookGuid.toString())) {
            return fixNotFoundNotebookGuid(args) ? LogUtil.ok() : LogUtil.error(e);
        } else if (e.getIdentifier().equals(EDAMDataModel.Note_noteGuid.toString())) {
            return fixNotFoundNoteGuid(args) ? LogUtil.ok() : LogUtil.error(e);
        }
        return LogUtil.error(e);
    }

    public ENObject findNotebookByName(final String name) {
        try {
            EeClipper clipper = EeClipperFactory.getInstance().getEeClipper(token, false);
            Map<String, ENObject> map = clipper.listNotebooks();
            return map.get(name);
        } catch (Throwable e) {
            // ignore and give up failure recovery
            LogUtil.logCancel(e);
        }
        return null;
    }

    public ENNote findNoteByName(final ENObject notebook, final String name) {
        ENNote noteFound = null;
        try {
            EeClipper clipper = EeClipperFactory.getInstance().getEeClipper(token, false);
            ENNote args = new ENNoteImpl();
            args.setNotebook(notebook);
            args.setName(name);
            Map<String, ENNote> map = clipper.listNotesWithinNotebook(args);
            noteFound = findNote(map, name);
        } catch (EDAMNotFoundException e) {
            if (e.getIdentifier().equals(EDAMDataModel.Notebook_guid.toString())) {
                ENObject nb = findNotebookByName(notebook.getName());
                // notebook is found or null, so should not come here again, which will result in a dead loop.
                noteFound = findNoteByName(nb, name);
            } else {
                LogUtil.logCancel(e);
            }
        } catch (Throwable e) {
            // ignore and give up failure recovery
            LogUtil.logCancel(e);
        }
        return noteFound;
    }

    /**
     * find note by name
     *
     * @param noteMap
     *            uuid-ENNote map
     * @param name
     *            should be exact note name, not uuid.
     * @return
     */
    public static ENNote findNote(final Map<String, ENNote> noteMap, final String name) {
        List<String> titles = ListUtil.list();
        for (Entry<String, ENNote> e : noteMap.entrySet()) {
            if (e.getValue().getName().equals(name)) {
                if (titles.size() != 0) {
                    return null;
                }
                titles.add(e.getKey());
            }
        }
        if (titles.size() == 1) {
            return noteMap.get(titles.get(0));
        }
        return null;
    }

    private boolean fixNotFoundNotebookGuid(final ENNote args) {
        ENObject found = findNotebookByName(args.getNotebook().getName());
        if (found != null) {
            args.setNotebook(found);
            args.getNotebook().setArgsReset(true);
            return true;
        }
        return false;
    }

    private boolean fixNotFoundNoteGuid(final ENNote args) {
        ENNote found = findNoteByName(args.getNotebook(), args.getName());
        if (found != null && StringUtils.isNotBlank(found.getGuid())) {
            args.setGuid(found.getGuid());
            args.setArgsReset(true);
            return true;
        }
        return false;
    }

}
