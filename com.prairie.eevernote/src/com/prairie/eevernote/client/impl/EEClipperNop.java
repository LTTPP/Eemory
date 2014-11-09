package com.prairie.eevernote.client.impl;

import java.util.List;
import java.util.Map;

import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.client.ENNote;
import com.prairie.eevernote.util.ListUtil;
import com.prairie.eevernote.util.MapUtil;

public class EEClipperNop extends EEClipper {

    @Override
    public void clipFile(final ENNote args) {

    }

    @Override
    public void clipSelection(final ENNote args) {

    }

    @Override
    public Map<String, String> listNotebooks() {
        return MapUtil.map();
    }

    @Override
    public Map<String, ENNote> listNotesWithinNotebook(final ENNote args) {
        return MapUtil.map();
    }

    @Override
    public List<String> listTags() {
        return ListUtil.list();
    }

    @Override
    public void setInvalid() {

    }

}
