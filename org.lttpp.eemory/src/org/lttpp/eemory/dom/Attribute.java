package org.lttpp.eemory.dom;

public interface Attribute extends Node {

	/**
	 * Returns the name of this attribute.
	 *
	 * @return the name of this attribute
	 */
	public String getName();

	/**
	 * Returns the value of this attribute.
	 * 
	 * @return the value of this attribute
	 */
	public String getValue();

	/**
	 * Set the value of this attribute.
	 * 
	 * @param newValue
	 *            the value to set
	 */
	public void setValue(String newValue);

	/**
	 * The Element node this attribute is attached to or null if this attribute
	 * is not in use.
	 * 
	 * @return The Element node or null
	 */
	public Element getOwnerElement();

}
