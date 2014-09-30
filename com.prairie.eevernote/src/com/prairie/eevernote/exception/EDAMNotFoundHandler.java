package com.prairie.eevernote.exception;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.evernote.edam.error.EDAMNotFoundException;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.EEClipperFactory;
import com.prairie.eevernote.client.ENNote;
import com.prairie.eevernote.client.impl.ENNoteImpl;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.LogUtil;

public class EDAMNotFoundHandler {

    private final String token;

    public EDAMNotFoundHandler(final String token) {
        this.token = token;
    }

    public boolean fixNotFoundException(final EDAMNotFoundException e, final ENNote args) {
        if (e.getIdentifier().equals(EDAMDataModel.Note_notebookGuid.toString())) {
            return fixNotFoundNotebookGuid(args);
        } else if (e.getIdentifier().equals(EDAMDataModel.Note_noteGuid.toString())) {
            return fixNotFoundNoteGuid(args);
        }
        return false;
    }

    public String findNotebookByName(final String name) {
        String guid = null;
        try {
            EEClipper clipper = EEClipperFactory.getInstance().getEEClipper(token, false);
            Map<String, String> map = clipper.listNotebooks();
            guid = map.get(name);
        } catch (Throwable e) {
            // ignore and give up failure recovery
            LogUtil.logCancel(e);
        }
        return guid;
    }

    public ENNote findNoteByName(final String notebookGuid, final String name) {
        ENNote guid = null;
        try {
            EEClipper clipper = EEClipperFactory.getInstance().getEEClipper(token, false);
            ENNote args = new ENNoteImpl();
            args.getNotebook().setGuid(notebookGuid);
            args.setName(name);
            Map<String, ENNote> map = clipper.listNotesWithinNotebook(args);
            guid = findNote(map, name);
        } catch (Throwable e) {
            // ignore and give up failure recovery
            LogUtil.logCancel(e);
        }
        return guid;
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
        String found = findNotebookByName(args.getNotebook().getName());
        if (StringUtils.isNotBlank(found)) {
            args.getNotebook().setGuid(found);
            args.getNotebook().setGuidReset(true);
            return true;
        }
        return false;
    }

    private boolean fixNotFoundNoteGuid(final ENNote args) {
        ENNote found = findNoteByName(args.getNotebook().getGuid(), args.getName());
        if (found != null && StringUtils.isNotBlank(found.getGuid())) {
            args.setGuid(found.getGuid());
            args.setGuidReset(true);
            return true;
        }
        return false;
    }

}
