package com.oohtml.compiler;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

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
				paths.add(new OverwritablePath(args[i+1], true, new File(args[i+1]).isDirectory()));
				i++;
			}else{
				paths.add(new OverwritablePath(args[i], false, new File(args[i]).isDirectory()));
			}
		}
		try {
			for (OverwritablePath op : paths) { 
				if(op.isDirectory){
					Files.walkFileTree(new File(op.path).toPath(), new SimpleFileVisitor<Path>(){ //Walk the file tree (depth-first) given in the argument, parsing every .oohtml file found there.
						@Override
						public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
							if(arg0.toString().endsWith(".oohtml")) handleNamedNodeAt(new OverwritablePath(arg0.toString(), op.overwrite, false));
							return FileVisitResult.CONTINUE;
						}
						
					});
				}else handleNamedNodeAt(op);
			}
		} catch (BadCodeException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) { //Usually, nothing should go wrong with the walkFileTree method.
			e.printStackTrace();
		}
	}
	
	private static void handleNamedNodeAt(OverwritablePath op){ //This is where the actual parsing goes on.  Note that this method cannot cope with paths that point to directories.
		NamedNode nn = new NamedNode(op.path); //Parse the .oohtml file at each path going through each of the parsers in turn.
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
	
	private static class OverwritablePath{
		public final String path;
		public final boolean overwrite;
		public final boolean isDirectory;
		public OverwritablePath(String p, boolean o, boolean d){
			path = p;
			overwrite = o;
			isDirectory = d;
		}
	}

}
