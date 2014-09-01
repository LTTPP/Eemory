package com.prairie.eevernote.handlers;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.evernote.edam.error.EDAMNotFoundException;
import com.prairie.eevernote.Constants;
import com.prairie.eevernote.ErrorMessage.EvernoteDataModel;
import com.prairie.eevernote.client.ClipperArgs;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.EEClipperFactory;
import com.prairie.eevernote.client.impl.ClipperArgsImpl;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.ListUtil;

public class EDAMNotFoundHandler implements Constants {

    private final String token;

    public EDAMNotFoundHandler(final String token) {
        this.token = token;
    }

    public boolean fixNotFoundException(final EDAMNotFoundException e, final ClipperArgs args) {
        if (e.getIdentifier().equals(EvernoteDataModel.Note_notebookGuid.toString())) {
            return fixNotFoundNotebookGuid(args);
        } else if (e.getIdentifier().equals(EvernoteDataModel.Note_noteGuid.toString())) {
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
        } catch (Exception e) {
            // ignore and give up failure recovery
        }
        return guid;
    }

    public String findNoteGuidByName(final String name) {
        String guid = null;
        try {
            EEClipper clipper = EEClipperFactory.getInstance().getEEClipper(token, false);
            ClipperArgs args = new ClipperArgsImpl();
            args.setNoteName(name);
            Map<String, String> map = clipper.listNotesWithinNotebook(args);
            List<String> titles = ListUtil.list();
            for (Entry<String, String> e : map.entrySet()) {
                String title = e.getKey().substring(ConstantsUtil.ZERO, e.getKey().indexOf(ConstantsUtil.LEFT_PARENTHESIS));
                if (title.equals(name)) {
                    if (titles.size() != ConstantsUtil.ZERO) {
                        return null;
                    }
                    titles.add(e.getKey());
                }
            }
            if (titles.size() == ConstantsUtil.ONE) {
                guid = map.get(titles.get(ConstantsUtil.ZERO));
            }
        } catch (Exception e) {
            // ignore and give up failure recovery
        }
        return guid;
    }

    private boolean fixNotFoundNotebookGuid(final ClipperArgs args) {
        String found = findNotebookGuidByName(args.getNotebookName());
        if (!StringUtils.isBlank(found)) {
            args.setNotebookGuid(found);
            args.setNotebookGuidReset(true);
            return true;
        }
        return false;
    }

    private boolean fixNotFoundNoteGuid(final ClipperArgs args) {
        String found = findNoteGuidByName(args.getNoteName());
        if (!StringUtils.isBlank(found)) {
            args.setNoteGuid(found);
            args.setNoteGuidReset(true);
            return true;
        }
        return false;
    }

}
