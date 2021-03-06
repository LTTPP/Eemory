package org.lttpp.eemory.dom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.lttpp.eemory.Messages;
import org.lttpp.eemory.dom.DOMException;
import org.lttpp.eemory.dom.Document;
import org.lttpp.eemory.dom.Node;
import org.lttpp.eemory.dom.Text;
import org.lttpp.eemory.util.ConstantsUtil;
import org.lttpp.eemory.util.DomUtil;
import org.lttpp.eemory.util.ListUtil;
import org.lttpp.eemory.util.MapUtil;

public class NodeImpl implements Node, Cloneable {

    private Document document;
    private String name;
    private Node parent;
    private List<Node> children;

    protected NodeImpl() {
        children = ListUtil.list();
    }

    protected NodeImpl(final String name) {
        this.name = name;
        children = ListUtil.list();
    }

    @Override
    public String getNodeName() {
        return name;
    }

    public void setNodeName(final String name) {
        this.name = name;
    }

    @Override
    public String getNodeValue() {
        return null;
    }

    @Override
    public void setNodeValue(final String value) {

    }

    @Override
    public short getNodeType() {
        return Node.NODE;
    }

    @Override
    public Node getParentNode() {
        return parent;
    }

    final public void setParentNode(final Node parent) {
        this.parent = parent;
    }

    @Override
    public List<Node> getChildNodes() {
        return children;
    }

    final public void setChildNodes(final List<Node> children) {
        this.children = children;
    }

    public Node getChildNode(final int index) {
        if (ListUtil.isIndexOutOfBounds(getChildNodes(), index)) {
            return null;
        }
        return children.get(index);
    }

    public Node getChildNodeByType(final int... types) {
        for (Node n : getChildNodes()) {
            for (int t : types) {
                if (n.getNodeType() == t) {
                    return n;
                }
            }
        }
        return null;
    }

    @Override
    public Node getFirstChild() {
        return children.get(0);
    }

    @Override
    public Node getLastChild() {
        return children.get(children.size() - 1);
    }

    @Override
    public Node getPreviousSibling() {
        List<Node> sibling = parent.getChildNodes();
        int i = sibling.indexOf(this) - 1;
        return i >= 0 && i < sibling.size() ? sibling.get(i) : null;
    }

    @Override
    public Node getNextSibling() {
        List<Node> sibling = parent.getChildNodes();
        int i = sibling.indexOf(this) + 1;
        return i >= 0 && i < sibling.size() ? sibling.get(i) : null;
    }

    @Override
    public Document getOwnerDocument() {
        return document;
    }

    public void setOwnerDocument(final Document document) {
        this.document = document;
    }

    @Override
    public Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
        if (!canHaveChild(newChild)) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, Messages.DOM_Error5);
        }
        if (isAncestor(newChild) || isSelf(newChild)) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, Messages.DOM_Error5);
        }
        if (!isSameDocument(newChild)) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, Messages.DOM_Error6);
        }
        if (!find(refChild)) {
            throw new DOMException(DOMException.NOT_FOUND_ERR, Messages.DOM_Error7);
        }

        List<Node> sibling = getChildNodes();
        if (refChild == null) {
            sibling.add(newChild);
        } else {
            sibling.add(sibling.indexOf(refChild), newChild);
            ((NodeImpl) newChild).setParentNode(this);
        }
        return newChild;
    }

    private boolean hasChildNodeOfType(final int nodeType) {
        for (Node n : getChildNodes()) {
            if (n.getNodeType() == nodeType) {
                return true;
            }
        }
        return false;
    }

    private boolean isAncestor(final Node newChild) {
        return getParentNode() != null && (getParentNode().isSameNode(newChild) || ((NodeImpl) getParentNode()).isAncestor(newChild));
    }

    private boolean isSelf(final Node newChild) {
        return isSameNode(newChild);
    }

    private boolean find(final Node refChild) {
        return getChildNodes().contains(refChild);
    }

    private boolean isSameDocument(final Node newChild) {
        Node document = getNodeType() == Node.DOCUMENT_NODE ? this : getOwnerDocument();
        return document.isSameNode(newChild.getOwnerDocument());
    }

    private boolean canHaveChild(final Node newChild) {
        if (getNodeType() == DOCUMENT_NODE) {
            if (newChild.getNodeType() == ATTRIBUTE_NODE || newChild.getNodeType() == TEXT_NODE || newChild.getNodeType() == DOCUMENT_NODE) {
                return false;
            } else if (hasChildNodeOfType(newChild.getNodeType())) {
                return false;
            } else {
                return true;
            }
        } else if (getNodeType() == ELEMENT_NODE) {
            if (newChild.getNodeType() == ATTRIBUTE_NODE || newChild.getNodeType() == DOCUMENT_NODE || newChild.getNodeType() == DOCUMENT_TYPE_NODE) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public Node replaceChild(final Node newChild, final Node oldChild) {
        if (!canHaveChild(newChild)) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, Messages.DOM_Error5);
        }
        if (isAncestor(newChild) || isSelf(newChild)) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, Messages.DOM_Error5);
        }
        if (!isSameDocument(newChild)) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, Messages.DOM_Error6);
        }
        if (!find(oldChild)) {
            throw new DOMException(DOMException.NOT_FOUND_ERR, Messages.DOM_Error7);
        }
        if (hasChildNode(newChild)) {
            removeChild(newChild);
        } else {
            List<Node> sibling = parent.getChildNodes();
            ListUtil.replace(sibling, newChild, sibling.indexOf(oldChild));
            ((NodeImpl) newChild).setParentNode(this);
            ((NodeImpl) oldChild).setParentNode(null);
        }
        return newChild;
    }

    private boolean hasChildNode(final Node refNode) {
        return getChildNodes().contains(refNode);
    }

    @Override
    public Node removeChild(final Node oldChild) {
        if (!find(oldChild)) {
            throw new DOMException(DOMException.NOT_FOUND_ERR, Messages.DOM_Error7);
        }
        children.remove(oldChild);
        ((NodeImpl) oldChild).setParentNode(null);
        return oldChild;
    }

    final public void removeAllChild() {
        children.clear();
    }

    @Override
    public Node appendChild(final Node newChild) {
        if (!canHaveChild(newChild)) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, Messages.DOM_Error5);
        }
        if (isAncestor(newChild) || isSelf(newChild)) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, Messages.DOM_Error5);
        }
        if (!isSameDocument(newChild)) {
            throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, Messages.DOM_Error6);
        }
        if (hasChildNode(newChild)) {
            removeChild(newChild);
        } else {
            children.add(newChild);
            ((NodeImpl) newChild).setParentNode(this);
        }
        return newChild;
    }

    @Override
    public boolean hasChildNodes() {
        return children.size() > 0;
    }

    @Override
    public boolean hasAttributes() {
        return false;
    }

    @Override
    public String getTextContent() {
        return null;
    }

    @Override
    public void setTextContent(final String text) {

    }

    @Override
    public Map<String, Node> getAttributes() {
        return MapUtil.map();
    }

    @Override
    public void normalize() {
        for (int i = 0; i < getChildNodes().size(); i++) {
            Node child = getChildNode(i);
            if (child.getNodeType() == TEXT_NODE) {
                Text textChild = (Text) child;
                if (textChild.isContentEmpty()) {
                    removeChild(textChild);
                    continue;
                }
                Node adjacentChild = getChildNode(i + 1);
                if (adjacentChild != null && adjacentChild.getNodeType() == TEXT_NODE) {
                    textChild.appendText(adjacentChild.getTextContent());
                    removeChild(adjacentChild);
                    i -= 1;
                }
            } else {
                child.normalize();
            }
        }
    }

    @Override
    public boolean isSameNode(final Node other) {
        return this == other;
    }

    @Override
    public boolean isEqualNode(final Node other) {
        if (this == other) {
            return true;
        }
        if (getNodeType() != other.getNodeType()) {
            return false;
        }
        if (!StringUtils.equals(getNodeName(), other.getNodeName()) || !StringUtils.equals(getNodeValue(), other.getNodeValue())) {
            return false;
        }
        if (!MapUtil.isEqualList(getAttributes(), other.getAttributes())) {
            return false;
        }
        if (!ListUtil.isEqualList(getChildNodes(), other.getChildNodes(), true)) {
            return false;
        }
        return true;
    }

    @Override
    public Node cloneNode(final boolean deep) {
        Node newObject = null;
        try {
            if (deep) {
                newObject = clone();
            } else {
                newObject = (Node) super.clone();
                ((NodeImpl) newObject).removeAllChild();
            }
        } catch (CloneNotSupportedException e) {
            // could not happen
        }
        return newObject;
    }

    @Override
    public Node clone() {
        NodeImpl newObject = null;
        try {
            newObject = (NodeImpl) super.clone();
            newObject.setParentNode(null);
            newObject.setChildNodes(ListUtil.cloneList((ArrayList<Node>) newObject.getChildNodes(), true));
        } catch (CloneNotSupportedException e) {
            // could not happen
        }
        return newObject;
    }

    @Override
    public String toString() {
        String string = ConstantsUtil.LEFT_ANGLE_BRACKET + getNodeName();
        for (Entry<String, Node> e : getAttributes().entrySet()) {
            String tempStr = DomUtil.toString(e.getValue());
            string += !StringUtils.isEmpty(tempStr) ? StringUtils.SPACE + tempStr : tempStr;
        }
        string += ConstantsUtil.RIGHT_ANGLE_BRACKET;
        for (Node child : getChildNodes()) {
            string += DomUtil.toString(child);
        }
        string += ConstantsUtil.LEFT_ANGLE_BRACKET + ConstantsUtil.SLASH + getNodeName() + ConstantsUtil.RIGHT_ANGLE_BRACKET;
        return string;
    }

}
