package com.prairie.eemory.dom.impl;

import com.prairie.eemory.dom.DocumentBuilder;
import com.prairie.eemory.dom.DocumentBuilderFactory;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {

    protected DocumentBuilderFactoryImpl() {

    }

    @Override
    public DocumentBuilder newDocumentBuilder() {
        return new DocumentBuilderImpl();
    }

}
