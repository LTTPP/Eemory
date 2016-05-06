package com.lttpp.eemory.dom.impl;

import com.lttpp.eemory.dom.Document;
import com.lttpp.eemory.dom.DocumentBuilder;

public class DocumentBuilderImpl extends DocumentBuilder {

    protected DocumentBuilderImpl() {

    }

    @Override
    public Document newDocument() {
        return new DocumentImpl();
    }

}
