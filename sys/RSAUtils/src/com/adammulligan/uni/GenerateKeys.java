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
import java.security.SecureRandom;
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
	private BigInteger N, phi, E, D;
	
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
	 * Constructor
	 * 
	 * @param length The length of the key in bits
	 * @param output_file The absolute path to the file to output the key to
	 */
	public GenerateKeys(int length,String output_file) {
		this.length      = length;
		this.output_file = output_file;
	}
	
	/**
	 * Tries to generate the key pair, and cleans up any files if any of the key pair fails to generate.
	 */
	private void generateKeys() {
		try {
			System.out.println("Generating...");
			
			this.generatePrimes(100);
			
			// N = pq
			N = this.p.multiply(this.q);
			
			// r = (p-1)*(q-1)
			phi = p.subtract(BigInteger.valueOf(1));
			phi = phi.multiply(q.subtract(BigInteger.valueOf(1)));
			
			// E = coprime of r
			// Chosen by fair dice roll, guaranteed to be random
			// http://xkcd.com/221/
		    /*E = new BigInteger("3");
			while((E.compareTo(phi) != -1) ||
				  (E.gcd(phi).compareTo(BigInteger.valueOf(1)) != 0)) {
				E = E.add(new BigInteger("2")); // Increase E until we get a valid value
			}*/
		    do
			{
				E = new BigInteger( this.length/2, new SecureRandom() ) ;
			}
			while( ( E.compareTo( phi ) != -1 ) || ( E.gcd( phi ).compareTo( BigInteger.valueOf( 1 ) ) != 0 ) ) ;
		    // D = 1/E mod r
			D = E.modInverse(phi);
			
			writeFile(this.generateRSAFormat(false),new File(this.output_file));
			writeFile(this.generateRSAFormat(true),new File(this.output_file+".pub"));
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
	 * Returns public or private key arrays in a string format
	 * TODO: Possibly to be changed to a toString() overload
	 * 
	 * @param public_key Which format is desired - true for public key, false for private
	 * @return Imploded array of String values, depending on which type of key is selected
	 */
	private String generateRSAFormat(boolean public_key) {
		StringBuilder content = new StringBuilder();
		String[] components = new String[6];
		String glue = ",";
		
		if (public_key) {
			components[0] = Integer.toString(this.length);
			components[1] = this.N.toString();
			components[2] = this.E.toString();
		} else {
			components[0] = Integer.toString(this.length);
			components[1] = this.N.toString();
			components[2] = this.E.toString();
			components[3] = this.D.toString();
			components[4] = this.p.toString();
			components[5] = this.q.toString();
		}
		
		// Implode the array of strings together using the specified glue
		content.append(components[0]);
		for (int i=1;i<components.length;i++) {
			if (components[i]!=null) {
				content.append(glue);
				content.append(components[i]);
			}
		}
		
		return content.toString();
	}
	
	/**Uses BigInteger to generate two large primes of length length/2 with a certainty provided
	 * 
	 * 
	 * @param certainty 
	 */
	private void generatePrimes(int certainty) {
		this.p = new BigInteger(this.length/2,certainty,new SecureRandom());
		this.q = new BigInteger(this.length/2,certainty,new SecureRandom());
		
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
