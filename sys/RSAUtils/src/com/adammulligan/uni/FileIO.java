/**
 * 
 */
package com.adammulligan.uni;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

/**
 * File Input/Output controls
 * 
 * @author adammulligan
 *
 */
public class FileIO {
	/**
	 * Uses BufferedWrite to write a String to a file
	 * 
	 * @param content File content to be written
	 * @param filename Absolute path to file to be written
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeFile(String content, File filename) throws FileNotFoundException, IOException {
	    if (filename == null) {
		      throw new IllegalArgumentException("File should not be null.");
		    }
		    if (!filename.exists()) {
		      filename.createNewFile();
		    }
		    if (!filename.isFile()) {
		      throw new IllegalArgumentException("Should not be a directory: " + filename);
		    }
		    if (!filename.canWrite()) {
		      throw new IllegalArgumentException("File cannot be written: " + filename);
		    }

		    Writer output = new BufferedWriter(new FileWriter(filename));
		    try {
		      output.write(content);
		    } finally {
		      output.close();
		    }
		}
	
	/**
	 * Reads a file and returns the contents, minus new lines
	 * 
	 * @param filename String path to the file to be read
	 * @return String contents of the file read
	 * @throws FileNotFoundException
	 */
	public static String readFile(String filename) throws FileNotFoundException {
	    StringBuilder text = new StringBuilder();
	    Scanner scanner = new Scanner(new FileInputStream(filename));
	    try {
	      while (scanner.hasNextLine()){
	    	// We're not adding new lines because our files should not have them
	        text.append(scanner.nextLine());
	      }
	    }
	    finally{
	      scanner.close();
	    }
	    
	    return text.toString();
	}
}
