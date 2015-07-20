package com.oohtml.compiler;

import java.util.Arrays;
import java.util.HashSet;

import org.jsoup.nodes.Element;

/**
 * A class of constants that defines the special additions to the HTML language required by OOHTML.
 * */

public class Language {
	public static final String BLOCK_TAG = "block";
	public static final String BLOCK_SRC_ATTRIBUTE = "src";
	public static final String EXPOSE_ATTRIBUTE = "expose";
	public static final String EXTEND_ATTRIBUTE = "extends";
	public static final String OVERRIDE_ATTRIBUTE = "override";
	public static final String IDENTIFICATION_ATTRIBUTE = "id";
	
	private static HashSet<String> AUTOMATICALLY_OVERRIDDEN = new HashSet<String>(Arrays.asList(new String[]{"title"}));
	public static boolean isOverridden(Element e){
		return AUTOMATICALLY_OVERRIDDEN.contains(e.tag().getName());
	}

}
