package org.lttpp.eemory.dom.impl;

import org.lttpp.eemory.dom.Document;
import org.lttpp.eemory.dom.DocumentBuilder;

public class DocumentBuilderImpl extends DocumentBuilder {

    protected DocumentBuilderImpl() {

    }

    @Override
    public Document newDocument() {
        return new DocumentImpl();
    }

}
