package com.prairie.eemory.dom.impl;

import com.prairie.eemory.dom.Document;
import com.prairie.eemory.dom.DocumentBuilder;

public class DocumentBuilderImpl extends DocumentBuilder {

    protected DocumentBuilderImpl() {

    }

    @Override
    public Document newDocument() {
        return new DocumentImpl();
    }

}
