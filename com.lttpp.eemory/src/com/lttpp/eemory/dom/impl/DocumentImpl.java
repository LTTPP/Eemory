package com.lttpp.eemory.dom.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;

import com.lttpp.eemory.Constants;
import com.lttpp.eemory.Messages;
import com.lttpp.eemory.dom.Attribute;
import com.lttpp.eemory.dom.DOMException;
import com.lttpp.eemory.dom.Document;
import com.lttpp.eemory.dom.DocumentFragment;
import com.lttpp.eemory.dom.DocumentType;
import com.lttpp.eemory.dom.Element;
import com.lttpp.eemory.dom.Node;
import com.lttpp.eemory.dom.Text;
import com.lttpp.eemory.util.ConstantsUtil;
import com.lttpp.eemory.util.DomUtil;
import com.lttpp.eemory.util.ListUtil;

public class DocumentImpl extends NodeImpl implements Document {

    private boolean standalone;
    private String xmlVersion;
    private String encoding;

    protected DocumentImpl() {
        standalone = true;
        xmlVersion = Constants.XML_VERSION_1_0;
        encoding = CharEncoding.UTF_8;
    }

    @Override
    public Element getDocumentElement() {
        return (Element) getChildNodeByType(ELEMENT_NODE, DOCUMENT_FRAGMENT_NODE);
    }

    @Override
    public Element createElement(final String name) {
        ElementImpl e = new ElementImpl(name);
        e.setOwnerDocument(this);
        return e;
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        DocumentFragmentImpl f = new DocumentFragmentImpl();
        f.setOwnerDocument(this);
        return f;
    }

    @Override
    public Text createTextNode(final String text) {
        TextImpl t = new TextImpl(text);
        t.setOwnerDocument(this);
        return t;
    }

    @Override
    public Attribute createAttribute(final String name) {
        AttributeImpl a = new AttributeImpl(name);
        a.setOwnerDocument(this);
        return a;
    }

    @Override
    public DocumentType createDocumentType(final String name, final String publicId, final String systemId) {
        DocumentTypeImpl t = new DocumentTypeImpl(name, publicId, systemId);
        t.setOwnerDocument(this);
        return t;
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
    public String getInputEncoding() {
        return null;
    }

    @Override
    public String getXmlEncoding() {
        return encoding;
    }

    @Override
    public void setXMLEncoding(final String encoding) {
        this.encoding = encoding;
    }

    @Override
    public boolean getXmlStandalone() {
        return standalone;
    }

    @Override
    public void setXmlStandalone(final boolean standalone) {
        this.standalone = standalone;
    }

    @Override
    public String getXmlVersion() {
        return xmlVersion;
    }

    @Override
    public void setXmlVersion(final String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }

    @Override
    public short getNodeType() {
        return Node.DOCUMENT_NODE;
    }

    @Override
    public boolean getStrictErrorChecking() {
        return true;
    }

    @Override
    public void setStrictErrorChecking(final boolean strictErrorChecking) {

    }

    @Override
    public Node importNode(final Node sourceNode, final boolean deep) throws DOMException {
        if (!canImport(sourceNode)) {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, Messages.DOM_Error0);
        }

        Node clonedNode = sourceNode.cloneNode(deep);

        ((NodeImpl) clonedNode).setParentNode(null);
        if (clonedNode instanceof Attribute) {
            ((AttributeImpl) clonedNode).setOwnerElement(null);
        }
        impOrt(clonedNode);

        return clonedNode;
    }

    private void impOrt(final Node clonedNode) {
        adopt(clonedNode);
    }

    private boolean canImport(final Node sourceNode) {
        return canAdopt(sourceNode);
    }

    @Override
    public Node adoptNode(final Node sourceNode) throws DOMException {
        if (canAdopt(sourceNode)) {
            throw new DOMException(DOMException.NOT_SUPPORTED_ERR, Messages.DOM_Error1);
        }
        Node parent = sourceNode.getParentNode();
        if (parent != null) {
            parent.removeChild(sourceNode);
            ((NodeImpl) sourceNode).setParentNode(null);
        }
        if (sourceNode instanceof Attribute) {
            ((AttributeImpl) sourceNode).setOwnerElement(null);
        }
        adopt(sourceNode);
        return sourceNode;
    }

    private void adopt(final Node sourceNode) {
        ((NodeImpl) sourceNode).setOwnerDocument(this);
        Set<Entry<String, Node>> set = sourceNode.getAttributes().entrySet();
        for (Entry<String, Node> e : set) {
            adopt(e.getValue());
        }
        for (Node n : sourceNode.getChildNodes()) {
            adopt(n);
        }
    }

    private boolean canAdopt(final Node sourceNode) {
        return sourceNode.getNodeType() != DOCUMENT_NODE && sourceNode.getNodeType() != DOCUMENT_TYPE_NODE;
    }

    @Override
    public void normalizeDocument() {
        normalize();
    }

    @Override
    public String toString() {
        String standalone = this.standalone ? StringUtils.EMPTY : StringUtils.SPACE + Constants.STANDALONE + ConstantsUtil.EQUAL + ConstantsUtil.DOUBLE_QUOTE + Constants.NO + ConstantsUtil.DOUBLE_QUOTE;
        String head = ConstantsUtil.LEFT_ANGLE_BRACKET + ConstantsUtil.QUESTION_MARK + Constants.XML + StringUtils.SPACE + Constants.VERSION + ConstantsUtil.EQUAL + ConstantsUtil.DOUBLE_QUOTE + xmlVersion + ConstantsUtil.DOUBLE_QUOTE + StringUtils.SPACE + Constants.ENCODING + ConstantsUtil.EQUAL + ConstantsUtil.DOUBLE_QUOTE + encoding + ConstantsUtil.DOUBLE_QUOTE + standalone + ConstantsUtil.QUESTION_MARK + ConstantsUtil.RIGHT_ANGLE_BRACKET;

        String body = StringUtils.EMPTY;
        Node doctype = null;
        for (Node child : getChildNodes()) {
            if (child == null) {
                continue;
            }
            if (child.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
                doctype = child;
            } else {
                body += child.toString();
            }
        }
        body = DomUtil.toString(doctype) + body;

        return head + body;
    }

}
