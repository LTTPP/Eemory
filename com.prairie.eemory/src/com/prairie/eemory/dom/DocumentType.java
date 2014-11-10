package com.prairie.eemory.dom;

public abstract interface DocumentType extends Node {

    public abstract String getName();

    public abstract String getPublicId();

    public abstract String getSystemId();

}
