package com.cyanoryx.uni.crypto.rsa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.zip.DataFormatException;

import com.cyanoryx.uni.common.FileIO;

public class Encrypt {
	public static void main(String[] args) {
		File input_file, key_file, output_file;
		
		Scanner kb = new Scanner(System.in);
		
		System.out.print("Public key: ");
		key_file = new File(kb.next());
		
		System.out.print("Input file: ");
		input_file = new File(kb.next());
		
		System.out.print("Output file: ");
		output_file = new File(kb.next());
		
		try {
			PublicKey pk = new PublicKey(key_file);
			byte[] plaintext = FileIO.readFile(input_file.toString()).getBytes();
			
			RSA_OAEP rsa = new RSA_OAEP(pk);
			
			byte[] ciphertext = rsa.encrypt(plaintext);
			
			ObjectOutput out = new ObjectOutputStream(new FileOutputStream(output_file));
			log("Writing ciphertext to "+output_file);
			out.writeObject(ciphertext);
			log("File encrypted.");
			out.close();
		} catch (DataFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void log(String msg) {
		System.out.println(msg);
	}
}
