package com.prairie.eemory.dom;

import java.util.List;
import java.util.Map;

public interface Node {

    public static final short NODE = 0;
    public static final short ELEMENT_NODE = 1;
    public static final short ATTRIBUTE_NODE = 2;
    public static final short TEXT_NODE = 3;
    public static final short DOCUMENT_NODE = 4;
    public static final short DOCUMENT_TYPE_NODE = 5;
    public static final short DOCUMENT_FRAGMENT_NODE = 6;

    /**
     * The name of this node, depending on its type.
     *
     * @return The name of this node
     */
    public String getNodeName();

    /**
     * Get the value of this node, depending on its type.
     *
     * @return The value of this node
     */
    public String getNodeValue();

    /**
     * Set the value of this node, depending on its type.
     *
     * @param newValue
     *            The value to be set
     */
    public void setNodeValue(String newValue);

    /**
     * A code representing the type of the underlying object.
     *
     * @return A code of type
     */
    public short getNodeType();

    /**
     * The parent of this node. All nodes, except {@code Attribute}, Document,
     * DocumentFragment, may have a parent. However, if a node has just been
     * created and not yet added to the tree, or if it has been removed from the
     * tree, this is null.
     *
     * @return The parent of this node
     */
    public Node getParentNode();

    /**
     * A list of node that contains all children of this node. If there are no
     * children, this is an empty list containing no nodes.
     *
     * @return A list of node that contains all children of this node
     */
    public List<Node> getChildNodes();

    /**
     * The first child of this node. If there is no such node, this returns
     * null.
     *
     * @return The first node, or null
     */
    public Node getFirstChild();

    /**
     * The last child of this node. If there is no such node, this returns null.
     *
     * @return The last node, or null
     */
    public Node getLastChild();

    /**
     * The node immediately preceding this node. If there is no such node, this
     * returns null.
     *
     * @return The node preceding this node, or null
     */
    public Node getPreviousSibling();

    /**
     * The node immediately following this node. If there is no such node, this
     * returns null.
     *
     * @return The node following this node, or null
     */
    public Node getNextSibling();

    /**
     * A map containing the attributes of this node (if it is an Element) or
     * null otherwise.
     *
     * @return A map containing the attributes
     */
    public abstract Map<String, Node> getAttributes();

    /**
     * The Document object associated with this node. This is also the Document
     * object used to create new nodes. When this node is a Document or a
     * DocumentType which is not used with any Document yet, this is null.
     *
     * @return The Document, or null
     */
    public Document getOwnerDocument();

    /**
     * Inserts the node newChild before the existing child node refChild. If
     * refChild is null, insert newChild at the end of the list of children.If
     * the newChild is already in the tree, it is first removed.
     *
     * @param newChild
     *            The node to insert
     * @param refChild
     *            The reference node, i.e., the node before which the new node
     *            must be inserted.
     * @return The node being inserted
     * @throws DOMException
     *             <p>
     *             HIERARCHY_REQUEST_ERR: Raised if this node is of a type that
     *             does not allow children of the type of the newChild node, or
     *             if the node to insert is one of this node's ancestors or this
     *             node itself, or if this node is of type Document and the DOM
     *             application attempts to insert a second DocumentType or
     *             Element node.
     *             </p>
     *             <p>
     *             WRONG_DOCUMENT_ERR: Raised if newChild was created from a
     *             different document than the one that created this node.
     *             </p>
     *             <p>
     *             NOT_FOUND_ERR: Raised if {@code refChild} is not a child of
     *             this node.
     *             </p>
     */
    public Node insertBefore(Node newChild, Node refChild) throws DOMException;

    /**
     * Replaces the child node oldChild with newChild in the list of children,
     * and returns the oldChild node. If the newChild is already in the tree, it
     * is first removed.
     *
     * @param newChild
     *            The new node to put in the child list
     * @param oldChild
     *            The node being replaced in the list
     * @return The node replaced
     * @throws DOMException
     *             <p>
     *             HIERARCHY_REQUEST_ERR: Raised if this node is of a type that
     *             does not allow children of the type of the newChild node, or
     *             if the node to put in is one of this node's ancestors or this
     *             node itself, or if this node is of type Document and the
     *             result of the replacement operation would add a second
     *             DocumentType or Element on the Document node.
     *             </p>
     *             <p>
     *             WRONG_DOCUMENT_ERR: Raised if newChild was created from a
     *             different document than the one that created this node.
     *             </p>
     *             <p>
     *             NOT_FOUND_ERR: Raised if oldChild is not a child of this
     *             node.
     *             </p>
     */
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException;

    /**
     * Removes the child node indicated by oldChild from the list of children,
     * and returns it.
     *
     * @param oldChild
     *            The node being removed
     * @return The node removed
     * @throws DOMException
     *             <p>
     *             NOT_FOUND_ERR: Raised if oldChild is not a child of this
     *             node.
     *             </p>
     */
    public Node removeChild(Node oldChild) throws DOMException;

    /**
     * Adds the node newChild to the end of the list of children of this node.
     * If the newChild is already in the tree, it is first removed.
     *
     * @param newChild
     *            The node to add.If it is a {@code DocumentFragment} object,
     *            the entire contents of the document fragment are moved into
     *            the child list of this node
     * @return The node added
     * @throws DOMException
     *             <p>
     *             HIERARCHY_REQUEST_ERR: Raised if this node is of a type that
     *             does not allow children of the type of the newChild node, or
     *             if the node to append is one of this node's ancestors or this
     *             node itself, or if this node is of type Document and the DOM
     *             application attempts to append a second DocumentType or
     *             Element node.
     *             </p>
     *             <p>
     *             WRONG_DOCUMENT_ERR: Raised if newChild was created from a
     *             different document than the one that created this node.
     *             </p>
     */
    public Node appendChild(Node newChild) throws DOMException;

    /**
     * Returns whether this node has any children.
     *
     * @return true if this node has any children, false otherwise
     */
    public boolean hasChildNodes();

    /**
     * Returns a clone of this node, i.e., serves as a generic copy constructor
     * for nodes. The duplicate node has no parent ( parentNode is null).
     * Cloning an Element copies all attributes and their values, but this
     * method does not copy any children it contains unless it is a deep clone.
     * This includes text contained in the Element since the text is contained
     * in a child Text node. Cloning an {@code Attribute} directly, as opposed
     * to be cloned as part of an Element cloning operation, returns a specified
     * attribute (specified is true). Cloning an {@code Attribute} always clones
     * its children, since they represent its value, no matter whether this is a
     * deep clone or not. Cloning any other type of node simply returns a copy
     * of this node. In addition, cloning Document, DocumentType is
     * implementation dependent.
     * 
     * @param deep
     *            If true, recursively clone the subtree under the specified
     *            node; if false, clone only the node itself (and its
     *            attributes, if it is an Element).
     * @return The cloned node
     */
    public Node cloneNode(boolean deep);

    /**
     * Puts all Text nodes in the full depth of the sub-tree underneath this
     * Node, including attribute nodes, into a "normal" form where only
     * structure separates Text nodes, i.e., there are neither adjacent Text
     * nodes nor empty Text nodes. This can be used to ensure that the DOM view
     * of a document is the same as if it were saved and re-loaded, and is
     * useful when operations that depend on a particular document tree
     * structure are to be used.
     */
    public void normalize();

    /**
     * Returns whether this node (if it is an element) has any attributes.
     *
     * @return true if this node has any attributes, false otherwise
     */
    public boolean hasAttributes();

    /**
     * This attribute returns the text content of this node and its descendants.
     * When it is defined to be null, setting it has no effect. On setting, any
     * possible children this node may have are removed and, if it the new
     * string is not empty or null, replaced by a single Text node containing
     * the string this attribute is set to. On getting, no serialization is
     * performed, the returned string does not contain any markup. No whitespace
     * normalization is performed and the returned string does not contain the
     * white spaces in element content (see the attribute
     * Text.isElementContentWhitespace). Similarly, on setting, no parsing is
     * performed either, the input string is taken as pure textual content. The
     * string returned is made of the text content of this node depending on its
     * type, as defined below:
     *
     * <pre>
     * <table>
     * <tr>
     * <td>Node type</td>
     * <td>Content</td>
     * </tr>
     * <tr>
     * <td>ELEMENT_NODE, ATTRIBUTE_NODE, ENTITY_NODE, ENTITY_REFERENCE_NODE,
     * DOCUMENT_FRAGMENT_NODE</td>
     * <td>concatenation of the textContent attribute value of every child node,
     * excluding COMMENT_NODE and PROCESSING_INSTRUCTION_NODE nodes. This is the
     * empty string if the node has no children.</td>
     * </tr>
     * <tr>
     * <td>TEXT_NODE, CDATA_SECTION_NODE, COMMENT_NODE,
     * PROCESSING_INSTRUCTION_NODE</td>
     * <td>nodeValue</td>
     * </tr>
     * <td>DOCUMENT_NODE, DOCUMENT_TYPE_NODE, NOTATION_NODE</td>
     * <td>null</td>
     * </table>
     * </pre>
     *
     * @return the text content
     */
    public String getTextContent();

    /**
     * This attribute returns the text content of this node and its descendants.
     * When it is defined to be null, setting it has no effect. On setting, any
     * possible children this node may have are removed and, if it the new
     * string is not empty or null, replaced by a single Text node containing
     * the string this attribute is set to. On getting, no serialization is
     * performed, the returned string does not contain any markup. No whitespace
     * normalization is performed and the returned string does not contain the
     * white spaces in element content (see the attribute
     * Text.isElementContentWhitespace). Similarly, on setting, no parsing is
     * performed either, the input string is taken as pure textual content. The
     * string returned is made of the text content of this node depending on its
     * type, as defined below:
     *
     * <pre>
     * <table>
     * <tr>
     * <td>Node type</td>
     * <td>Content</td>
     * </tr>
     * <tr>
     * <td>ELEMENT_NODE, ATTRIBUTE_NODE, ENTITY_NODE, ENTITY_REFERENCE_NODE,
     * DOCUMENT_FRAGMENT_NODE</td>
     * <td>concatenation of the textContent attribute value of every child node,
     * excluding COMMENT_NODE and PROCESSING_INSTRUCTION_NODE nodes. This is the
     * empty string if the node has no children.</td>
     * </tr>
     * <tr>
     * <td>TEXT_NODE, CDATA_SECTION_NODE, COMMENT_NODE,
     * PROCESSING_INSTRUCTION_NODE</td>
     * <td>nodeValue</td>
     * </tr>
     * <td>DOCUMENT_NODE, DOCUMENT_TYPE_NODE, NOTATION_NODE</td>
     * <td>null</td>
     * </table>
     * </pre>
     *
     * @param text
     */
    public void setTextContent(String text);

    /**
     * Returns whether this node is the same node as the given one. This method
     * provides a way to determine whether two Node references returned by the
     * implementation reference the same object. When two Node references are
     * references to the same object, even if through a proxy, the references
     * may be used completely interchangeably, such that all attributes have the
     * same values and calling the same DOM method on either reference always
     * has exactly the same effect.
     *
     * @param other
     *            The node to test against
     * @return true if the nodes are the same, false otherwise
     */
    public boolean isSameNode(Node other);

	                                                                                        /**
     * Tests whether two nodes are equal. This method tests for equality of
     * nodes, not sameness (i.e., whether the two nodes are references to the
     * same object) which can be tested with {@code Node.isSameNode()}. All
     * nodes that are the same will also be equal, though the reverse may not be
     * true. Two nodes are equal if and only if the following conditions are
     * satisfied:
     * <ul>
     * <li>
     * The two nodes are of the same type.
     * <li>
     * The following string attributes are equal: {@code nodeName},
     * {@code nodeValue}. This is: they are both null, or they have the same
     * length and are character for character identical.</li>
     * <li>The attributes are equal. This is: they are both null, or they have
     * the same length and for each node that exists in one map there is a node
     * that exists in the other map and is equal, although not necessarily at
     * the same index.</li>
     * <li>The child nodes are equal. This is: they are both null, or they have
     * the same length and contain equal nodes at the same index. Note that
     * normalization can affect equality; to avoid this, nodes should be
     * normalized before being compared.</li>
     * </ul>
     * 
     * For two {@code DocumentType} nodes to be equal, the following conditions
     * must also be satisfied:
     * <ul>
     * <li>The following string attributes are equal: {@code publicId},
     * {@code systemId}, internalSubset.</li>
     * </ul>
     * 
     * On the other hand, the following do not affect equality: the
     * {@code ownerDocument} and {@code parentNode} attributes, the specified
     * attribute for {@code Attribute} nodes and Element nodes.
     * 
     * <p>
     * <b>Note</b>: As a general rule, anything not mentioned in the description
     * above is not significant in consideration of equality checking. Note that
     * future versions of this specification may take into account more
     * attributes and implementations conform to this specification are expected
     * to be updated accordingly.
     * </p>
     * 
     * @param other
     *            The node to compare equality with.
     * @return true if the nodes are equal, false otherwise.
     */
    public boolean isEqualNode(Node other);

}
