package com.lttpp.eemory.dom;

import java.util.List;

public interface Element extends Node {

    /**
     * The name of the element.
     *
     * @return the name of the element
     */
    public String getTagName();

    /**
     * Retrieves an attribute value by name.
     *
     * @param name
     *            The name of the attribute to retrieve.
     * @return The {@code Attribute} value as a string, or the empty string if
     *         that attribute does not have a specified or default value.
     */
    public String getAttribute(String name);

    /**
     * Adds a new attribute. If an attribute with that name is already present
     * in the element, its value is changed to be that of the value parameter.
     * This value is a simple string; it is not parsed as it is being set. So
     * any markup (such as syntax to be recognized as an entity reference) is
     * treated as literal text, and needs to be appropriately escaped by the
     * implementation when it is written out.
     *
     * @param name
     *            The name of the attribute to create or alter.
     * @param value
     *            Value to set in string form.
     */
    public void setAttribute(String name, String value);

    /**
     * Removes an attribute by name.
     *
     * @param name
     *            The name of the attribute to remove.
     */
    public void removeAttribute(String name);

    /**
     * Retrieves an attribute node by name.
     *
     * @param name
     *            The name (nodeName) of the attribute to retrieve.
     * @return The {@code Attribute} node with the specified name ( nodeName) or
     *         null if there is no such attribute.
     */
    public Attribute getAttributeNode(String name);

    /**
     *
     * @param newAattr
     *            The {@code Attribute} node to add to the attribute list.
     * @return If the {@code newAttr} attribute replaces an existing attribute,
     *         the replaced {@code Attribute} node is returned, otherwise null
     *         is returned.
     * @throws DOMException
     *             WRONG_DOCUMENT_ERR: Raised if {@code newAttr} was created
     *             from a different document than the one that created the
     *             element. INUSE_ATTRIBUTE_ERR: Raised if {@code newAttr} is
     *             already an attribute of another Element object. The DOM user
     *             must explicitly clone {@code Attribute} nodes to re-use them
     *             in other elements.
     */
    public Attribute setAttributeNode(Attribute newAattr) throws DOMException;

    /**
     * Removes the specified attribute node.
     *
     * @param oldAttr
     *            The {@code Attribute} node to remove from the attribute list.
     * @return The {@code Attribute} node that was removed.
     * @throws DOMException
     *             NOT_FOUND_ERR: Raised if oldAttr is not an attribute of the
     *             element.
     */
    public Attribute removeAttributeNode(Attribute oldAttr) throws DOMException;

	    /**
     * Returns a list of all descendant Elements with a given tag name, in
     * document order.
     * 
     * @param name
     *            The name of the tag to match on. The special value "*" matches
     *            all tags.
     * @return A list of matching Element nodes.
     */
    public List<Node> getElementsByTagName(String name);

    /**
     * Returns true when an attribute with a given name is specified on this
     * element or has a default value, false otherwise.
     *
     * @param name
     *            The name of the attribute to look for.
     * @return true if an attribute with the given name is specified on this
     *         element or has a default value, false otherwise.
     */
    public boolean hasAttribute(String name);

}
