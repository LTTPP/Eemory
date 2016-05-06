package com.lttpp.eemory.dom;

public interface Text extends Node {

    /**
     * The length of text, may be zero.
     *
     * @return The length of text
     */
    public int getLength();

    /**
     * Append the string to the end of the character data of the node. Upon
     * success, data provides access to the concatenation of data and the
     * DOMString specified.
     *
     * @param text
     *            The DOMString to append.
     */
    public void appendText(String text);

    /**
     * Insert a string at the specified 16-bit unit offset.
     *
     * @param offset
     *            The character offset at which to insert.
     * @param text
     *            The DOMString to insert.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if the specified offset is negative or
     *             greater than the length of data.
     */
    public void insertText(int offset, String text) throws DOMException;

    /**
     * Remove a range of 16-bit units from the node. Upon success, data and
     * length reflect the change.
     *
     * @param offset
     *            The offset from which to start removing.
     * @param countThe
     *            number of 16-bit units to delete. If the sum of offset and
     *            count exceeds length then all 16-bit units from offset to the
     *            end of the data are deleted.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if the specified offset is negative or
     *             greater than the number of 16-bit units in data, or if the
     *             specified count is negative.
     */
    public void deleteText(int offset, int count) throws DOMException;

    /**
     * Replace the characters starting at the specified 16-bit unit offset with
     * the specified string.
     *
     * @param offset
     *            The offset from which to start replacing.
     * @param count
     *            The number of 16-bit units to replace. If the sum of offset
     *            and count exceeds length, then all 16-bit units to the end of
     *            the data are replaced; (i.e., the effect is the same as a
     *            remove method call with the same range, followed by an append
     *            method invocation).
     * @param text
     *            The DOMString with which the range must be replaced.
     * @throws DOMException
     *             INDEX_SIZE_ERR: Raised if the specified offset is negative or
     *             greater than the number of 16-bit units in data, or if the
     *             specified count is negative.
     */
    public void replaceText(int offset, int count, String text) throws DOMException;

    /**
     * Returns whether this text node content is empty("").
     * 
     * @return true if only whitespace content, false otherwise
     */
    boolean isContentEmpty();

}