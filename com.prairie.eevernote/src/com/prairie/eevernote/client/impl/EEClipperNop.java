package com.prairie.eevernote.client.impl;

import java.util.Map;

import com.prairie.eevernote.client.ClipperArgs;
import com.prairie.eevernote.client.EEClipper;
import com.prairie.eevernote.util.ConstantsUtil;
import com.prairie.eevernote.util.MapUtil;

public class EEClipperNop extends EEClipper {

    @Override
    public void clipFile(final ClipperArgs args) throws Exception {

    }

    @Override
    public void clipSelection(final ClipperArgs args) throws Exception {

    }

    @Override
    public Map<String, String> listNotebooks() throws Exception {
        return MapUtil.map();
    }

    @Override
    public Map<String, String> listNotesWithinNotebook(final ClipperArgs args) throws Exception {
        return MapUtil.map();
    }

    @Override
    public String[] listTags() throws Exception {
        return new String[ConstantsUtil.ZERO];
    }

}
