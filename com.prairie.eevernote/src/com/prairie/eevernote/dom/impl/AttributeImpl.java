package com.prairie.eevernote.dom.impl;

import org.apache.commons.lang3.StringUtils;

import com.prairie.eevernote.dom.Attribute;
import com.prairie.eevernote.dom.Element;
import com.prairie.eevernote.dom.Node;
import com.prairie.eevernote.util.ConstantsUtil;

public class AttributeImpl extends NodeImpl implements Attribute, ConstantsUtil {

    private String value;
    private Element ownerElement;

    protected AttributeImpl(final String name) {
        super(name);
        value = StringUtils.EMPTY;
    }

    protected AttributeImpl(final String name, final String value) {
        super(name);
        this.value = value;
    }

    public void setName(final String name) {
        ((NodeImpl) this).setNodeName(name);
    }

    @Override
    public String getName() {
        return getNodeName();
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(final String newValue) {
        value = newValue;
    }

    @Override
    public Element getOwnerElement() {
        return ownerElement;
    }

    final public void setOwnerElement(final Element ownerElement) {
        this.ownerElement = ownerElement;
    }

    @Override
    public String getNodeValue() {
        return getValue();
    }

    @Override
    public void setNodeValue(final String value) {
        setValue(value);
    }

    @Override
    public short getNodeType() {
        return Node.ATTRIBUTE_NODE;
    }

    @Override
    public String getTextContent() {
        return getValue();
    }

    @Override
    public void setTextContent(final String text) {
        setNodeValue(text);
    }

    @Override
    public Node clone() {
        AttributeImpl newObject = (AttributeImpl) super.clone();
        newObject.setOwnerElement(null);
        return newObject;
    }

    @Override
    public String toString() {
        return getName() + EQUAL + DOUBLE_QUOTATION + getValue() + DOUBLE_QUOTATION;
    }

}
