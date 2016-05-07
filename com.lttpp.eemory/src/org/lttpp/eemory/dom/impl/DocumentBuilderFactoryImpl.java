package org.lttpp.eemory.dom.impl;

import org.lttpp.eemory.dom.DocumentBuilder;
import org.lttpp.eemory.dom.DocumentBuilderFactory;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

    protected DocumentBuilderFactoryImpl() {

    }

    @Override
    public DocumentBuilder newDocumentBuilder() {
        return new DocumentBuilderImpl();
    }

}
