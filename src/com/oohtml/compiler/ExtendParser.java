package com.oohtml.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A subclass of Parser, this class parses inheritance, namely the 'extends' and
 * 'override' attributes (which go in the &lt;html&gt; element and any element
 * and contain the path to the file to extend and the id of the element to
 * override respectively).
 *
 * Extension works like this: when a file extends another file, all the elements
 * (but not body text) are copied from the parent file to the child file. The
 * exception is when one of the child elements overrides one of the parent
 * elements. In that case, the parent element is not copied and the child
 * element is left unchanged.
 *
 * @see Parser
 */
public class ExtendParser extends Parser {

	private List<String> usedPaths;
	@Override
	public NamedNode parseDocument(NamedNode input) {
		usedPaths = new ArrayList<String>();
		return parse(input);
	}

	private NamedNode parse(NamedNode input) {
		if (!((Document) input.code).child(0).hasAttr(Language.EXTEND_ATTRIBUTE))
			return input; // If we don't actually extend anything here, don't bother.
		NamedNode sup = new NamedNode(
				resolveRelativePath(input, ((Document) input.code).child(0).attr(Language.EXTEND_ATTRIBUTE))); // Get the NamedNode we extend.
		if (usedPaths.contains(sup.path)) { // Check for circular references.
			throw new BadCodeException("Circular inheritance from " + input.path + " to " + sup.path + ".");
		} else {
			usedPaths.add(sup.path);
		}
		sup = Processor.processNamedNode(sup); // Parse the NamedNode we extend in case it extends something itself.
		Element head = extend(((Document) sup.code).head(), ((Document) input.code).head()); //Actually extend the <head> and <body> elements.
		Element body = extend(((Document) sup.code).body(), ((Document) input.code).body());
		((Document) input.code).head().replaceWith(head.clone());
		((Document) input.code).body().replaceWith(body.clone());
		return input;
	}

	private Element extend(Element sup, Element sub) {
		// Get the child elements for both the sup (super) element and the sub
		// (extended) element.
		Elements subElements = sub.children();
		Elements supElements = sup.children().clone();
		// For each element in the sub group,
		loop: for (Element e : subElements) {
			// If it's overridden, delete it from sup.
			if (e.hasAttr(Language.OVERRIDE_ATTRIBUTE)) {
				for (Element el : supElements) {
					if (el.attr(Language.IDENTIFICATION_ATTRIBUTE).equals(e.attr(Language.IDENTIFICATION_ATTRIBUTE))) {
						supElements.remove(el);
						continue loop;
					}
				}
				// Fail silently if no element is found to override.
				continue loop;
			} else if (Language.isOverridden(e)) {
				// Some elements are automatically overridden if they exist.
				for (Element el : supElements) {
					if (el.tagName().equals(e.tagName())) {
						supElements.remove(el);
						continue loop;
					}
				}
				// Fail silently if no element is found to override.
				continue loop;
			} else {
				// If it's not overridden but does correspond to an element,
				// recursively extend it.
				for (Element el : supElements) {
					if (el.hasAttr(Language.IDENTIFICATION_ATTRIBUTE) && el.attr(Language.IDENTIFICATION_ATTRIBUTE)
							.equals(e.attr(Language.IDENTIFICATION_ATTRIBUTE))) {
						Element temp = extend(el.clone(), e.clone()).clone();
						e.replaceWith(temp);
						supElements.remove(el);
						continue loop;
					}
				}
			}
		}
		// Add the elements from the sup to the beginning of sub. This is where
		// the real extension happens.
		Collections.reverse(supElements);
		for (Element e : supElements) {
			sub.prependChild(e.clone());
		}
		return sub;
	}

}
