package com.prairie.eevernote.dom;

public interface DocumentFragment extends Node {

	/**
	 * Insert a fragment at the start. null to be "null".
	 *
	 * @param fragment
	 *            The document fragment to insert.
	 */
	public void insertFragment(DocumentFragment fragment);

	/**
	 * Append a fragment to the end. null to be "null".
	 *
	 * @param fragment
	 *            The document fragment to append.
	 */
	public void appendFragment(DocumentFragment fragment);

}
