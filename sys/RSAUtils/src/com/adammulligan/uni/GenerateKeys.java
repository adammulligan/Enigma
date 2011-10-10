/**
 * 
 */
package com.adammulligan.uni;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 * An interactive console application that generates and stores a pair of RSA keys.
 * 
 * @author adammulligan
 *
 */
public class GenerateKeys {
	private int length;
	private String output_file;
	
	private BigInteger p,q;
	
	/**
	 * @param args
	 * @return void
	 */
	public static void main(String[] args) {
		System.out.println("Generating public/private RSA key pair.");
		
		Scanner kb = new Scanner(System.in);
		
		System.out.print("Key length (bits): ");
		int length = kb.nextInt();
		
		String output_file = System.getProperty("user.home")+"/id_rsa";
		System.out.print("Output to ["+output_file+"]: ");
		
		GenerateKeys gk = new GenerateKeys(length, output_file);
		
		gk.generateKeys();
		System.out.println("Keys generated. Saved to "+output_file);
	}
	
	/**
	 * 
	 * @param length The length of the key in bits
	 * @param output_file The absolute path to the file to output the key to
	 */
	public GenerateKeys(int length,String output_file) {
		this.length      = length;
		this.output_file = output_file;
	}
	
	/**
	 * Tries to generate the key pair, and cleans up any files if one half of the pair fails to generate.
	 */
	private void generateKeys() {
		try {
			System.out.println("Generating...");
			
			this.generatePrimes(4);
			BigInteger N, r, E, D;
			
			// N = pq
			N = this.p.multiply(this.q);
			
			// r = (p-1)*(q-1)
			r = p.subtract(BigInteger.valueOf(1));
			r = r.multiply(q.subtract(BigInteger.valueOf(1)));
			 
			// E = coprime of r
		    do {
		    	E = new BigInteger(this.length, new Random());
			} while((E.compareTo(r) != -1) ||
					(E.gcd(r).compareTo(BigInteger.valueOf(1)) != 0));

		    // D = 1/E mod r
			D = E.modInverse(r);
			
			String pub_key  = E.toString()+","+N.toString();
			String priv_key = D.toString()+","+N.toString();
			
			writeFile(priv_key,new File(this.output_file));
			writeFile(pub_key,new File(this.output_file+".pub"));
		} catch (IOException ioe) {
			System.out.println("File IO Error: "+ioe);
			System.out.println("Cleaning up..");
			
			File pub  = new File(this.output_file);
			File priv = new File(this.output_file+".pub");
			
			if (priv.exists()) { priv.delete(); }
			if (pub.exists()) { pub.delete(); }
			
			System.out.println("Terminated due to error");
		}
	}
	
	/**
	 * 
	 * @param certainty 
	 */
	private void generatePrimes(int certainty) {
		this.p = new BigInteger(this.length,certainty,new Random());
		this.q = new BigInteger(this.length,certainty,new Random());
		
		if (this.p == this.q) this.generatePrimes(certainty);
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
}
