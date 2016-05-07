package org.lttpp.eemory.client.impl;

import java.util.List;
import java.util.Map;

import org.lttpp.eemory.client.EeClipper;
import org.lttpp.eemory.client.model.ENNote;
import org.lttpp.eemory.client.model.ENObject;
import org.lttpp.eemory.util.ListUtil;
import org.lttpp.eemory.util.MapUtil;

public class EeClipperNop extends EeClipper {

    @Override
    public void clipFile(final ENNote args) {

    }

    @Override
    public void clipSelection(final ENNote args) {

    }

    @Override
    public Map<String, ENObject> listNotebooks() {
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
