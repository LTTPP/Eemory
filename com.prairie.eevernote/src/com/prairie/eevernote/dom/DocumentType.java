package com.prairie.eevernote.dom;

public abstract interface DocumentType extends Node {

    public abstract String getName();

    public abstract String getPublicId();

    public abstract String getSystemId();

}
