package com.cyanoryx.uni.crypto.rsa.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Scanner;

import com.cyanoryx.uni.common.FileIO;
import com.cyanoryx.uni.crypto.rsa.PrivateKey;
import com.cyanoryx.uni.crypto.rsa.RSA_OAEP;

public class Decrypt {
	public static void main(String[] args) {
		File input_file, key_file, output_file;
		
		Scanner kb = new Scanner(System.in);
		
		System.out.println("Private key: ");
		key_file = new File(kb.next());
		
		System.out.println("Input file: ");
		input_file = new File(kb.next());
		
		System.out.println("Output file: ");
		output_file = new File(kb.next());
		
		try {
			PrivateKey pk = new PrivateKey(key_file);
			log("Reading in "+input_file);
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(input_file));
			byte[] ciphertext = (byte[])in.readObject();
			
			RSA_OAEP rsa = new RSA_OAEP(pk);
			
			byte[] plaintext = rsa.decrypt(ciphertext);
			String output = new String(plaintext);
			
			log("Writing plaintext to "+output_file);
			FileIO.writeFile(output, output_file);
			log("File decrypted.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void log(String msg) {
		System.out.println(msg);
	}
}
