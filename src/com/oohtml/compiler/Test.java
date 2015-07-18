package com.oohtml.compiler;

public class Test {
	
	public static void main(String[] args){
		NamedNode one = new NamedNode("C:/Users/Michael/Desktop/one.html");
		ExtendParser bp = new ExtendParser();
		System.out.println(bp.parseDocument(one).code.toString());
	}

}
