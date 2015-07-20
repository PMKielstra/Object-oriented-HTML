package com.oohtml.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A subclass of Parser, this class parses 'blocks', namely, the 'expose'
 * element and the 'use' tag. These elements are used as follows: <br>
 * <br>
 * File a.oohtml: &lt;html&gt; ... &lt;div
 * expose="blockName"&gt;DIVCONTENT&lt;/div&gt; ... &lt;/html&gt; <br>
 * <br>
 * File b.oohtml: &lt;html&gt; ... &lt;use
 * name="relative/path/to/a.oohtml/blockName" /&gt; ... &lt;/html&gt; <br>
 * <br>
 * When put through the compiler, file a.oohtml is unchanged, but the &lt;use
 * name="relative/path/to/a.oohtml/blockName" /&gt; in file b.oohtml is replaced
 * with &lt;div&gt;DIVCONTENT&lt;/div&gt;. (Note that the 'expose' attribute is
 * removed.)
 *
 *
 * @see Parser
 */
public class BlockParser extends Parser {

	private List<String> usedPaths;
	private HashMap<String, NamedNode> parsedNodes = new HashMap<String, NamedNode>();

	/**
	 * This is the 'public face' of the parser.
	 *
	 * @param input
	 *            A NamedNode, preferably one built using the constructor that
	 *            takes a path.
	 * @return A NamedNode containing a JSoup document with all blocks resolved.
	 */
	@Override
	public NamedNode parseDocument(NamedNode input) {
		// We need two methods because we need to reset usedPaths when we start
		// to parse a new document and its whole extension tree, but not just
		// when we start to parse a new document.
		usedPaths = new ArrayList<String>();
		return parse(input);
	}

	private NamedNode parse(NamedNode input) {
		// Get all <use> elements in the document.
		Elements elements = ((Document) input.code).getElementsByTag(Language.BLOCK_TAG);
		for (Element e : elements) {
			// Get the name and path to the node.
			String nameAndPath = e.attr(Language.BLOCK_SRC_ATTRIBUTE);
			if (usedPaths.contains(nameAndPath)) {
				throw new BadCodeException("Circular reference to " + nameAndPath + " from " + input.path + ".");
			} else
				usedPaths.add(nameAndPath);
			NamedNode nn = null;
			String name = "";
			if (nameAndPath.contains("/") || nameAndPath.contains("\\")) {
				// Split up the path around occurrences of either \ or /.
				String[] segments = nameAndPath.split("\\\\|/");
				// The last item in segments is the block's name. The other
				// items form the path.
				name = segments[segments.length - 1];
				String path = "";
				// Join all the segments except the last one and put a file
				// separator between them.
				for (int i = 0; i < segments.length - 1; i++)
					path += (segments[i] + File.separator);
				// The final segment will have had a file separator appended.
				// Remove it.
				path = path.substring(0, path.length() - File.separator.length());
				// Get the canonical path to the node in question.
				String resolvedPath = resolveRelativePath(input, path);
				// If we haven't already parsed this node, parse it.
				if (!parsedNodes.containsKey(resolvedPath)) {
					nn = parse(new NamedNode(resolvedPath));
					parsedNodes.put(resolvedPath, nn);
				} else {
					nn = parsedNodes.get(resolvedPath);
				}
			} else {
				nn = input;
				name = nameAndPath;
			}
			// Find the required block inside the new node.
			Elements blocks = ((Document) nn.code).getElementsByAttribute(Language.EXPOSE_ATTRIBUTE)
					.select("[" + Language.IDENTIFICATION_ATTRIBUTE + "=" + name + "]");
			// If there isn't a block with the right name, throw an exception.
			if (blocks.size() < 1) {
				throw new BadCodeException("No such block as " + name + " in " + nn.path + ".");
				// If there are too many blocks with the right name, throw an
				// exception.
			} else if (blocks.size() > 1) {
				throw new BadCodeException("There are " + blocks.size() + " blocks called " + name + " in " + nn.path
						+ ".  There should only be one.");
			}
			// Clone the block (because otherwise the call to replaceWith would
			// remove block from nn), remove the 'expose' attribute from it, and
			// insert it into the document.
			Element block = blocks.first().clone();
			block.removeAttr(Language.EXPOSE_ATTRIBUTE);
			e.replaceWith(block);
		}
		return input;
	}
}
