package com.oohtml.compiler;

import java.util.HashMap;

/**
 * A small class that calls the parsers in order.
 * */
public class Processor {
	private static Parser[] parsers = new Parser[] {new ExtendParser(), new BlockParser()}; // The parsers to apply to each block, in order.
	
	private static HashMap<String, NamedNode> parsedNodes = new HashMap<String, NamedNode>();
	
	/**
	 * The method to parse a NamedNode.  It maintains a list of all previously parsed NamedNodes and checks to see if it has already parsed one before it parses it.
	 * @param nn The NamedNode to parse.
	 * @return The parsed NamedNode.
	 * */
	public static NamedNode processNamedNode(NamedNode nn){
		if(parsedNodes.containsKey(nn.path)) return parsedNodes.get(nn.path);
		for (Parser p : parsers) {
			nn = p.parseDocument(nn);
		}
		parsedNodes.put(nn.path, nn);
		return nn;
	}
}
