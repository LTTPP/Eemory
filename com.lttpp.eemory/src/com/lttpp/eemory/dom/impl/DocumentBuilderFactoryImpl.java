package com.lttpp.eemory.dom.impl;

import com.lttpp.eemory.dom.DocumentBuilder;
import com.lttpp.eemory.dom.DocumentBuilderFactory;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

    protected DocumentBuilderFactoryImpl() {

    }

    @Override
    public DocumentBuilder newDocumentBuilder() {
        return new DocumentBuilderImpl();
    }

}
