package com.lttpp.eemory.dom;

import java.util.List;

public interface Document extends Node {

    /**
     * This is a convenience attribute that allows direct access to the child
     * node that is the document element of the document.
     * 
     * @return
     */
    public Element getDocumentElement();

    /**
     * Creates an element of the type specified. Note that the instance returned
     * implements the Element interface, so attributes can be specified directly
     * on the returned object. In addition, if there are known attributes with
     * default values, {@code Attribute} nodes representing them are
     * automatically created and attached to the element.
     * 
     * @param tagName
     *            The name of the element type to instantiate. For XML, this is
     *            case-sensitive, otherwise it depends on the case-sensitivity
     *            of the markup language in use. In that case, the name is
     *            mapped to the canonical form of that markup by the DOM
     *            implementation.
     * @return A new Element object with the nodeName attribute set to tagName
     */
    public Element createElement(String tagName);

    /**
     * Creates an empty DocumentFragment object.
     * 
     * @return A new DocumentFragment.
     */
    public DocumentFragment createDocumentFragment();

    /**
     * Creates a Text node given the specified string.
     * 
     * @param data
     *            The data for the node
     * @return The new Text object
     */
    public Text createTextNode(String data);

    /**
     * Creates an {@code Attribute} of the given name. Note that the
     * {@code Attribute} instance can then be set on an Element using the
     * setAttributeNode method.
     * 
     * @param name
     *            The name of the attribute
     * @return A new {@code Attribute} object with the nodeName attribute set to
     *         name. The value of the attribute is the empty string.
     */
    public Attribute createAttribute(String name);

    /**
     * Create an document type declaration with given name, public ID, system
     * ID.
     * 
     * @param name
     *            name of the document type
     * @param publicId
     *            public ID of the document type
     * @param systemId
     *            system ID of the document type
     * @return the created document type declaration
     */
    public DocumentType createDocumentType(final String name, final String publicId, final String systemId);

    /**
     * Returns a list of all the Elements in document order with a given tag
     * name and are contained in the document.
     * 
     * @param tagName
     *            The name of the tag to match on. The special value "*" matches
     *            all tags. For XML, the {@code tagName} parameter is
     *            case-sensitive, otherwise it depends on the case-sensitivity
     *            of the markup language in use.
     * @return A new list containing all the matched Elements.
     */
    public List<Node> getElementsByTagName(String tagName);

    /**
     * An attribute specifying the encoding used for this document at the time
     * of the parsing. This is null when it is not known, such as when the
     * Document was created in memory.
     * 
     * @return the encoding used for pasring
     */
    public String getInputEncoding();

    /**
     * An attribute specifying, as part of the XML declaration, the encoding of
     * this document. This is null when unspecified or when it is not known,
     * such as when the Document was created in memory.
     * 
     * @return the encoding of this document
     */
    public String getXmlEncoding();

    /**
     * Set the attribute specifying, as part of the XML declaration, the
     * encoding of this document.
     * 
     * @param encoding
     *            XML encoding as part of declaration
     */
    public void setXMLEncoding(String encoding);

    /**
     * An attribute specifying, as part of the XML declaration, whether this
     * document is standalone. This is false when unspecified.
     * 
     * @return true if XML is standalone, false otherwise
     */
    public boolean getXmlStandalone();

    /**
     * An attribute specifying, as part of the XML declaration, whether this
     * document is standalone. This is false when unspecified.
     * 
     * @param standalone
     *            document standalone value to set
     */
    public void setXmlStandalone(boolean standalone);

    /**
     * An attribute specifying, as part of the XML declaration, the version
     * number of this document. If there is no declaration and if this document
     * supports the "XML" feature, the value is "1.0". If this document does not
     * support the "XML" feature, the value is always null.
     * 
     * @return XML version
     */
    public String getXmlVersion();

    /**
     * Set the attribute specifying, as part of the XML declaration, the version
     * number of this document. If there is no declaration and if this document
     * supports the "XML" feature, the value is "1.0". If this document does not
     * support the "XML" feature, the value is always null.
     * 
     * @param xmlVersion
     *            XML version to set
     */
    public void setXmlVersion(String xmlVersion);

    /**
     * An attribute specifying whether error checking is enforced or not. When
     * set to false, the implementation is free to not test every possible error
     * case normally defined on DOM operations, and not raise any DOMException
     * on DOM operations or report errors while using
     * Document.normalizeDocument(). In case of error, the behavior is
     * undefined. This attribute is true by default.
     * 
     * @return true if strict error checking, false otherwise
     */
    public boolean getStrictErrorChecking();

    /**
     * An attribute specifying whether error checking is enforced or not. When
     * set to false, the implementation is free to not test every possible error
     * case normally defined on DOM operations, and not raise any DOMException
     * on DOM operations or report errors while using
     * Document.normalizeDocument(). In case of error, the behavior is
     * undefined. This attribute is true by default.
     * 
     * @param strictErrorChecking
     *            error checking to set
     */
    public void setStrictErrorChecking(boolean strictErrorChecking);

    /**
     * Imports a node from another document to this document, without altering
     * or removing the source node from the original document; this method
     * creates a new copy of the source node. The returned node has no parent;
     * (parentNode is null). For all nodes, importing a node creates a node
     * object owned by the importing document, with attribute values identical
     * to the source node's nodeName and nodeType.
     * 
     * @param sourceNode
     *            The node to import
     * @param deep
     *            If true, recursively import the subtree under the specified
     *            node; if false, import only the node itself, as explained
     *            above. This has no effect on nodes that cannot have any
     *            children, and on {@code Attribute}, and EntityReference nodes.
     * @return The imported node that belongs to this Document.
     * @throws DOMException
     *             NOT_SUPPORTED_ERR: Raised if the type of node being imported
     *             is not supported.
     */
    public Node importNode(Node sourceNode, boolean deep) throws DOMException;

    /**
     * Attempts to adopt a node from another document to this document. If
     * supported, it changes the ownerDocument of the source node, its children,
     * as well as the attached attribute nodes if there are any. If the source
     * node has a parent it is first removed from the child list of its parent.
     * This effectively allows moving a subtree from one document to another
     * (unlike importNode() which create a copy of the source node instead of
     * moving it). When it fails, applications should use Document.importNode()
     * instead. Note that if the adopted node is already part of this document
     * (i.e. the source and target document are the same), this method still has
     * the effect of removing the source node from the child list of its parent,
     * if any.
     * 
     * @param sourceNode
     *            The node to move into this document
     * @return The adopted node, or null if this operation fails, such as when
     *         the source node comes from a different implementation.
     * @throws DOMException
     *             NOT_SUPPORTED_ERR: Raised if the source node is of type
     *             DOCUMENT, DOCUMENT_TYPE.
     */
    public Node adoptNode(Node sourceNode) throws DOMException;

    /**
     * This method acts as if the document was going through a save and load
     * cycle, putting the document in a "normal" form. As a consequence, this
     * method normalizes {@code Text} nodes, as defined in the method
     * {@code Node.normalize()}.
     */
    public void normalizeDocument();
}
