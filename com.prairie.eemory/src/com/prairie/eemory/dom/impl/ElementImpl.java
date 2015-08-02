package com.prairie.eemory.dom.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.prairie.eemory.Messages;
import com.prairie.eemory.dom.Attribute;
import com.prairie.eemory.dom.DOMException;
import com.prairie.eemory.dom.Element;
import com.prairie.eemory.dom.Node;
import com.prairie.eemory.dom.Text;
import com.prairie.eemory.util.ConstantsUtil;
import com.prairie.eemory.util.ListUtil;
import com.prairie.eemory.util.MapUtil;

public class ElementImpl extends NodeImpl implements Element {

    private Map<String, Node> attrs;

    protected ElementImpl(final String name) {
        super(name);
        attrs = MapUtil.map();
    }

    @Override
    public String getTagName() {
        return getNodeName();
    }

    @Override
    public String getAttribute(final String name) {
        return ((Attribute) attrs.get(name)).getValue();
    }

    @Override
    public void setAttribute(final String name, final String value) {
        AttributeImpl attr = new AttributeImpl(name, value);
        attr.setOwnerElement(this);
        attrs.put(name, attr);
    }

    @Override
    public void removeAttribute(final String name) {
        AttributeImpl attr = (AttributeImpl) attrs.get(name);
        attr.setOwnerElement(null);
        attrs.remove(attr);
    }

    @Override
    public Attribute getAttributeNode(final String name) {
        return (Attribute) attrs.get(name);
    }

    @Override
    public Attribute setAttributeNode(final Attribute newAttr) throws DOMException {
        if (!newAttr.getOwnerDocument().isSameNode(this)) {
            throw new DOMException(DOMException.WRONG_DOCUMENT_ERR, Messages.DOM_Error2);
        }
        if (getOwnerDocument() != null) {
            throw new DOMException(DOMException.INUSE_ATTRIBUTE_ERR, Messages.DOM_Error3);
        }
        Attribute oldAttr = (Attribute) attrs.get(newAttr);
        if (oldAttr != null) {
            ((AttributeImpl) oldAttr).setOwnerElement(null);
        }
        ((AttributeImpl) newAttr).setOwnerElement(this);
        attrs.put(newAttr.getName(), newAttr);
        return oldAttr;
    }

    @Override
    public Attribute removeAttributeNode(final Attribute oldAttr) throws DOMException {
        AttributeImpl attr = (AttributeImpl) attrs.get(oldAttr);
        if (attr == null) {
            throw new DOMException(DOMException.NOT_FOUND_ERR, Messages.DOM_Error4);
        } else {
            ((AttributeImpl) oldAttr).setOwnerElement(null);
        }
        return (Attribute) attrs.remove(oldAttr);
    }

    @Override
    final public Map<String, Node> getAttributes() {
        return attrs;
    }

    final public void setAttributs(final Map<String, Node> attrs) {
        this.attrs = attrs;
    }

    @Override
    public List<Node> getElementsByTagName(final String name) {
        if (name.equals(ConstantsUtil.STAR)) {
            return getChildNodes();
        } else {
            List<Node> matched = ListUtil.list();
            Iterator<Node> iter = getChildNodes().iterator();
            while (iter.hasNext()) {
                Node node = iter.next();
                if (node.getNodeName().equals(name)) {
                    matched.add(node);
                }
                if (node instanceof Element) {
                    matched.addAll(((Element) node).getElementsByTagName(name));
                }
            }
            return matched;
        }
    }

    @Override
    public String getTextContent() {
        String content = StringUtils.EMPTY;
        for (Node n : getChildNodes()) {
            if (!StringUtils.isEmpty(content)) {
                content += StringUtils.SPACE;
            }
            content += n.getTextContent();
        }
        return content;
    }

    private void removeAllChildren() {
        for (Node n : getChildNodes()) {
            removeChild(n);
        }
    }

    @Override
    public void setTextContent(final String text) {
        Text textNode = getOwnerDocument().createTextNode(text);
        removeAllChildren();
        appendChild(textNode);
    }

    @Override
    public boolean hasAttribute(final String name) {
        return attrs.containsKey(name);
    }

    @Override
    public boolean hasAttributes() {
        return !MapUtil.isEmpty(attrs);
    }

    @Override
    public short getNodeType() {
        return Node.ELEMENT_NODE;
    }

    @Override
    public Node cloneNode(final boolean deep) {
        ElementImpl newObject = null;
        if (deep) {
            newObject = (ElementImpl) super.cloneNode(deep);
        } else {
            newObject = (ElementImpl) super.cloneNode(deep);
            newObject.setAttributs(MapUtil.cloneMap((HashMap<String, Node>) getAttributes(), true));
        }
        return newObject;
    }

    @Override
    public Element clone(){
        ElementImpl newObject = (ElementImpl) super.clone();
        newObject.setAttributs(MapUtil.cloneMap((HashMap<String, Node>) getAttributes(), true));
        return newObject;
    }

}
