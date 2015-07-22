package com.oohtml.compiler;

import java.util.Arrays;
import java.util.HashSet;

import org.jsoup.nodes.Element;

/**
 * A class of constants that defines the special additions to the HTML language
 * required by OOHTML.
 */

public class Language {
	public static final String BLOCK_TAG = "block";
	public static final String BLOCK_SRC_ATTRIBUTE = "src";
	public static final String EXPOSE_ATTRIBUTE = "expose";
	public static final String EXTEND_ATTRIBUTE = "extends";
	public static final String OVERRIDE_ATTRIBUTE = "override";
	public static final String IDENTIFICATION_ATTRIBUTE = "id";
	
	public static final String FILE_EXTENSION = ".oohtml";

	//A list of elements to automatically override.
	private static HashSet<String> AUTOMATICALLY_OVERRIDDEN = new HashSet<String>(
			Arrays.asList(new String[] { "title" }));

	/**
	 * Some elements are automatically overridden no matter what their 'id' attributes are.  This method tells you if they are.
	 * @param e The element that could possibly be overridden.
	 * */
	public static boolean isOverridden(Element e) {
		return AUTOMATICALLY_OVERRIDDEN.contains(e.tag().getName()); //Is the element in the list?
	}

}
