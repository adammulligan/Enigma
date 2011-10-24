package com.adammulligan.uni;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * An interactive console application that encrypts a file based on a provided RSA key pair.
 * 
 * @author adammulligan
 *
 */
public class Decrypt {
	private String key_file, input, output;
	
	private BigInteger E,N;
	private BigInteger[] plaintext;
	
	private boolean verbose;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Encrypt e = new Encrypt("/Users/adammulligan/id_rsa","/Users/adammulligan/output","/Users/adammulligan/plaintext");
		
		e.save();
	}
	
	/**
	 * 
	 * @param key_file The public key file for encryption
	 * @param input_file The file to be decrypted
	 * @param output_file The location for the plaintext to be written
	 */
	public Decrypt(String key_file,String input_file,String output_file) {
		this.key_file = key_file; // TODO check validity
		
		this.verbose = true;
		
		this.log("Validating and storing input/output");
		try {
			this.setInputFile(input_file);
			this.setOutputFile(output_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.log("Parsing key file");
		this.parseKeyFile();
		
		this.log("Reading input and decryptin");
		try {
			this.decrypt(this.readFile(this.input));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.log("Success!");
	}
	
	/**
	 * Takes a String, encrypts using RSA Basic and stores locally
	 * 
	 * @param message Ciphertext to be decrypted
	 */
	private void decrypt(String message) {
		byte[] temp = new byte[1], bytes;
		
		bytes = message.getBytes();

		BigInteger[] converted_bytes = new BigInteger[bytes.length];

		// Convert each byte of the message into a bigint
		for(int i=0; i<converted_bytes.length;i++) {
			temp[0] = bytes[i];
			converted_bytes[i] = new BigInteger(temp);
		}

		this.plaintext = new BigInteger[converted_bytes.length];

		// The actual encryption!
		// Loop through the array of bigint bytes m, and create an array making c = m
		for(int i=0;i<converted_bytes.length;i++) {
			this.plaintext[i] = converted_bytes[i].modPow(this.E, this.N);
		}
	}
	
	/**
	 * Converts the BigInteger ciphertext bytes to hex and writes them to the output file
	 */
	public void save() {
		String output="";
		
		// Convert each byte into a hex value
		for (BigInteger c : this.plaintext) {
			output += c.toString(16).toUpperCase();
		}
		
		try {
			this.writeFile(output,new File(this.output));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the local input file (plaintext)
	 * 
	 * @param file 
	 * @throws IOException
	 */
	private void setInputFile(String file) throws IOException {
		File filename = new File(file);
		
		if (!filename.exists()) {
	      throw new IOException("Input file does not exist");
	    }
		
	    if (!filename.isFile()) {
	      throw new IllegalArgumentException("Input file should not be a directory: " + filename);
	    }
	    
	    this.input = file;
	}
	
	/**
	 * Sets the local output file (ciphertext)
	 * 
	 * @param filename String path to where the ciphertext will be written
	 * @throws FileNotFoundException
	 */
	private void setOutputFile(String filename) throws FileNotFoundException {
		this.output = filename;
	}
	
	/**
	 * Reads the object key file and parses it to retrieve E and N
	 */
	private void parseKeyFile() {
		try {
			String key_string = this.readFile(this.key_file);
			String[] keys     = key_string.split(",");
			
			this.E = new BigInteger(keys[0]);
			this.N = new BigInteger(keys[1]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads a file and returns the contents, minus new lines
	 * 
	 * @param filename String path to the file to be read
	 * @return String contents of the file read
	 * @throws FileNotFoundException
	 */
	private String readFile(String filename) throws FileNotFoundException {
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
	
	/**
	 * Uses BufferedWrite to generically write a String to a file
	 * 
	 * @param content File content to be written
	 * @param filename Absolute path to file to be written
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void writeFile(String content, File filename) throws FileNotFoundException, IOException {
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
	
	private void log(String msg) {
		if (this.verbose) {
			System.out.println(msg);
		}
	}
}
