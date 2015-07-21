package com.oohtml.compiler;

import java.io.File;
import java.io.IOException;

/**
 * The base class for the parsers for all the various language elements. A
 * parser's job is to take a JSoup document with a certain object-oriented
 * feature, like inheritance, and convert that to a document that is
 * browser-readable.
 */
public class Parser {

	/**
	 * A utility method to resolve a path from one document to another.
	 * 
	 * @param node
	 *            The file from which the path starts. This NamedNode must have
	 *            a path.
	 * @param path
	 *            The relative path from the given node to the referenced one.
	 * @return The node at the end of the relative path from the given node or
	 *         null if a filesystem error caused the files to be unreadable.
	 */
	protected String resolveRelativePath(NamedNode node, String path) {
		File parentFolder = new File(new File(node.path).getParent());
		File newNode = new File(parentFolder, path);
		try {
			return newNode.getCanonicalPath();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * The 'public face' of the parser. This method should be where the actual
	 * parsing happens.
	 * 
	 * @param input
	 *            The NamedNode (should be a Document) to parse.
	 * @return The parsed document (or, in this case, because no actual parsing
	 *         happens in the Parser base class, null).
	 */
	public NamedNode parseDocument(NamedNode input) {
		return null;
	}
}
