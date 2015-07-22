package com.oohtml.compiler;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * The main class containing the command line argument parser.
 */
public class Main {

	/**
	 * The 'public face' of the program. This does the actual parsing.
	 *
	 * @param args
	 *            A list of file paths to .oohtml files to parse. Prefix them
	 *            with '-o ' to overwrite the .oohtml file with the parsed HTML
	 *            instead of creating a new .html file.
	 * @throws UnsupportedEncodingException Generally this shouldn't happen.  It happens if the system doesn't support UTF-8.
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		if (args.length == 0) // Don't parse anything if we haven't been given anything to parse.
			return;
		List<OverwritablePath> paths = new ArrayList<OverwritablePath>(); // Create a list of paths along with data on whether they can be overwritten.
		File parentFolder = new File(URLDecoder.decode(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8"));
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-o")) {
				if(!Paths.get(args[i+1]).isAbsolute()){
					args[i+1] = new File(parentFolder, args[i+1]).getAbsolutePath();
				}
				paths.add(new OverwritablePath(args[i+1], true, new File(args[i + 1]).isDirectory()));
				i++;
			} else {
				if(!Paths.get(args[i]).isAbsolute()){
					args[i] = new File(parentFolder, args[i]).getAbsolutePath();
				}
				paths.add(new OverwritablePath(args[i], false, new File(args[i]).isDirectory()));
			}
		}
		try {
			for (OverwritablePath op : paths) {
				if (op.isDirectory) {
					Files.walkFileTree(new File(op.path).toPath(), new SimpleFileVisitor<Path>() { // Walk the file tree (depth-first) in the given argument, parsing every .oohtml file found there.
						@Override
						public FileVisitResult visitFile(Path arg0, BasicFileAttributes arg1) throws IOException {
							if (arg0.toString().endsWith(".oohtml"))
								handleNamedNodeAt(new OverwritablePath(arg0.toString(), op.overwrite, false));
							return FileVisitResult.CONTINUE;
						}

					});
				} else
					handleNamedNodeAt(op);
			}
		} catch (BadCodeException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) { // Usually, nothing should go wrong with the walkFileTree method.
			e.printStackTrace();
		}
	}

	private static void handleNamedNodeAt(OverwritablePath op) { // This is where the actual parsing happens.  Note that the method cannot cope with paths that point to directories.
		NamedNode nn = new NamedNode(op.path); // Parse the .oohtml file at each path going through each of the parsers in turn.
		nn = Processor.processNamedNode(nn);
		if (op.overwrite) { // If we have to overwrite the .oohtml file, do so.  Otherwise, replace the .oohtml extension with .html and save the code there.
			nn.saveToDisk(nn.path);
		} else {
			String pathWithoutFileExtension = op.path.substring(0, op.path.lastIndexOf('.'));
			pathWithoutFileExtension = pathWithoutFileExtension + ".html";
			nn.saveToDisk(pathWithoutFileExtension);
		}
	}

	private static class OverwritablePath {
		public final String path;
		public final boolean overwrite;
		public final boolean isDirectory;

		public OverwritablePath(String p, boolean o, boolean d) {
			path = p;
			overwrite = o;
			isDirectory = d;
		}
	}

}
