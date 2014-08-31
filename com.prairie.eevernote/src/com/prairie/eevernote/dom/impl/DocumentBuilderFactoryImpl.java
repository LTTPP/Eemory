package com.prairie.eevernote.dom.impl;

import com.prairie.eevernote.dom.DocumentBuilder;
import com.prairie.eevernote.dom.DocumentBuilderFactory;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

    protected DocumentBuilderFactoryImpl() {

    }

    @Override
    public DocumentBuilder newDocumentBuilder() {
        return new DocumentBuilderImpl();
    }

}
