package com.prairie.eevernote.dom.impl;

import com.prairie.eevernote.dom.Document;
import com.prairie.eevernote.dom.DocumentBuilder;

public class DocumentBuilderImpl extends DocumentBuilder {

    protected DocumentBuilderImpl() {

    }

    @Override
    public Document newDocument() {
        return new DocumentImpl();
    }

}
