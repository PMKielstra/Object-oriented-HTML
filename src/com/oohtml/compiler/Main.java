package com.oohtml.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * The main class containing the command line argument parser.
 * */
public class Main {

	private static Parser[] parsers = new Parser[] { new BlockParser(),
			new ExtendParser() }; //The parsers to apply to each block, in order.
	
	/**
	 * The 'public face' of the program.  This does the actual parsing.
	 * 
	 * @param args A list of file paths to .oohtml files to parse.  Prefix them with '-o ' to overwrite the .oohtml file with the parsed HTML instead of creating a new .html file.
	 * */
	public static void main(String[] args) {
		if (args.length == 0) //Don't parse anything if we haven't been given anything to parse.
			return;
		List<OverwritablePath> paths = new ArrayList<OverwritablePath>(); //Create a list of paths along with data on whether they can be overwritten.
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("-o")){
				paths.add(new OverwritablePath(args[i+1], true));
				i++;
			}else{
				paths.add(new OverwritablePath(args[i], false));
			}
		}
		try {
			for (OverwritablePath op : paths) { //Parse the .oohtml file at each path going through each of the parsers in turn.
				NamedNode nn = new NamedNode(op.path);
				for (Parser p : parsers) {
					nn = p.parseDocument(nn);
				}
				if(op.overwrite){ //If we have to overwrite the .oohtml file, do so.  Otherwise, replace the .oohtml file extension with .html and save the code there.
					nn.saveToDisk(nn.path);
				}else{
					String pathWithoutFileExtension = op.path.substring(0, op.path.lastIndexOf('.'));
					pathWithoutFileExtension = pathWithoutFileExtension + ".html";
					nn.saveToDisk(pathWithoutFileExtension);
				}
			}
		} catch (BadCodeException e) {
			System.err.println(e.getMessage());
		}
	}
	
	private static class OverwritablePath{
		public String path;
		public boolean overwrite;
		public OverwritablePath(String p, boolean o){
			path = p;
			overwrite = o;
		}
	}

}
