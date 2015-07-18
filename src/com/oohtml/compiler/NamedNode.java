package com.oohtml.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;


/**
 * A class used for documents, sub-documents, and elements.  This class is quite versatile and can be used for pretty much any HTML out there.
 * */
public class NamedNode {
	public Node code; //The code can change, so it is not final.
	public final String name;
	public final String path; //Only used for documents.
	
	/**
	 * The constructor used for non-document nodes.  It gives the path a final value of "".
	 * @param name The name of the node.
	 * @param code A JSoup Node (or Document, Element etc.) that contains the HTML code.
	 * */
	public NamedNode(String name, Node code){
		this.name =  name;
		this.code = code;
		this.path = "";
	}
	
	
	/**
	 * The constructor used for document nodes.  It reads the HTML code from the file at the given path.
	 * @param path The path to the code.
	 * */
	public NamedNode(String path){
		this.path = path;
		File file = new File(path);
		this.name = file.getName();
		try {
			this.code = Jsoup.parse(file, null);
		} catch (IOException e) {
			throw new BadCodeException("File " + path + " could not be read.  (This does not necessarily mean it was bad code).");
		}
	}
	
	/**
	 * This method writes the contents of the NamedNode to the disk at the location given in the path variable, or does nothing if the path variable is empty.
	 * */
	public void saveToDisk(String pathToWriteTo){
		if(pathToWriteTo == null || pathToWriteTo.equals("")) return;
		try(PrintWriter pw = new PrintWriter(new FileOutputStream(pathToWriteTo, false))){  //Pass 'false' to overwrite the file.
			pw.write(((Document) code).html());
			pw.flush();
			pw.close();
		}catch(IOException e){
			throw new BadCodeException("Could not write to file at " + pathToWriteTo + ".");
		}
	}
}
