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
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.LogUtil;

public class EDAMNotFoundHandler implements ConstantsUtil {

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

    public String findNotebookGuidByName(final String name) {
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

    public String findNoteGuidByName(final String notebookGuid, final String name) {
        String guid = null;
        try {
            EEClipper clipper = EEClipperFactory.getInstance().getEEClipper(token, false);
            ENNote args = new ENNoteImpl();
            args.getNotebook().setGuid(notebookGuid);
            String realName = StringUtils.substringBeforeLast(name, LEFT_PARENTHESIS);
            args.setName(realName);
            Map<String, String> map = clipper.listNotesWithinNotebook(args);
            guid = findNoteGuid(map, name);
        } catch (Throwable e) {
            // ignore and give up failure recovery
            LogUtil.logCancel(e);
        }
        return guid;
    }

    public static String findNoteGuid(final Map<String, String> noteMap, final String name) {
        List<String> titles = ListUtil.list();
        String realName = StringUtils.substringBeforeLast(name, LEFT_PARENTHESIS);
        for (Entry<String, String> e : noteMap.entrySet()) {
            String title = StringUtils.substringBeforeLast(e.getKey(), LEFT_PARENTHESIS);
            if (title.equals(realName)) {
                if (titles.size() != ZERO) {
                    return null;
                }
                titles.add(e.getKey());
            }
        }
        if (titles.size() == ONE) {
            return noteMap.get(titles.get(ZERO));
        }
        return null;
    }

    private boolean fixNotFoundNotebookGuid(final ENNote args) {
        String found = findNotebookGuidByName(args.getNotebook().getName());
        if (!StringUtils.isBlank(found)) {
            args.getNotebook().setGuid(found);
            args.getNotebook().setGuidReset(true);
            return true;
        }
        return false;
    }

    private boolean fixNotFoundNoteGuid(final ENNote args) {
        String found = findNoteGuidByName(args.getNotebook().getGuid(), args.getName());
        if (!StringUtils.isBlank(found)) {
            args.setGuid(found);
            args.setGuidReset(true);
            return true;
        }
        return false;
    }

}
