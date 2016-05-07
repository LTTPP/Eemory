package org.lttpp.eemory.dom.impl;

import org.apache.commons.lang3.StringUtils;
import org.lttpp.eemory.dom.Attribute;
import org.lttpp.eemory.dom.Element;
import org.lttpp.eemory.dom.Node;
import org.lttpp.eemory.util.ConstantsUtil;

public class AttributeImpl extends NodeImpl implements Attribute {

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
        return getName() + ConstantsUtil.EQUAL + ConstantsUtil.DOUBLE_QUOTE + getValue() + ConstantsUtil.DOUBLE_QUOTE;
    }

}
