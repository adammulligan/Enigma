/**
 * 
 */
package com.cyanoryx.uni.crypto.rsa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

import com.cyanoryx.uni.common.FileIO;

/**
 * An interactive console application that generates and stores a pair of RSA keys.
 * 
 * @author adammulligan
 *
 */
public class GenerateKeys {
	private static File output_file;
	
	/**
	 * @param args
	 * @return void
	 */
	public static void main(String[] args) {
		Scanner kb = new Scanner(System.in);
		
		System.out.print("Key length (bits): ");
		int length = kb.nextInt();
		
		while (length>KeyGenerator.SIZE_MAX || length<KeyGenerator.SIZE_MIN) {
			System.out.print("Invalid key size.\nKey length (bits): ");
			length = kb.nextInt();
		}
		
		System.out.print("File to output to:");
		String file = kb.next();
		
		try {
			setOutputFile(file);
		} catch (IllegalArgumentException e) {
			while (true) {
				try {
					System.out.println("Error: "+e.getMessage()+", File to output to:");
					file = kb.next();
					setOutputFile(file);
					break;
				} catch (IllegalArgumentException f) {
					continue;
				}
			}
		}

		KeyGenerator kg = new KeyGenerator(length);
		
		BigInteger[] pair = kg.generatePair();
		
		String priv_key = pair[0]+","+pair[2];
		String pub_key  = pair[0]+","+pair[1];
		
		try {
			FileIO.writeFile(priv_key,output_file);
			FileIO.writeFile(pub_key,new File(output_file+".pub"));
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		}
		

		System.out.println("Keys generated. Saved to "+file);
	}
	
	private static void setOutputFile(String filename) throws IllegalArgumentException {
		File file = new File(filename);
		
		try {
			output_file = file;
			// Create the file
			FileIO.writeFile("",output_file);
			
			if (!file.isFile()) {
		      throw new IllegalArgumentException("Should not be a directory: " + file);
		    }
		    
		    if (!file.canWrite()) {
		      throw new IllegalArgumentException("File cannot be written: " + file);
		    }
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			throw new IllegalArgumentException("IOException: "+e.getMessage());
		}
	}
}
