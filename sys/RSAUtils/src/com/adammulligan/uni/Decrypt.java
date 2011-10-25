package com.adammulligan.uni;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;

/**
 * An interactive console application that decrypts a file based on a provided RSA key pair.
 * 
 * @author adammulligan
 *
 */
public class Decrypt {
	private String key_file, input, output;
	
	private BigInteger D,N;
	private byte[] plaintext;
	
	private MessageDigest md;
	
	private boolean verbose = false;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String keyfile,input,output;
		keyfile = input = output = null;
		boolean verbose = false;
		
		for (int i=0;i<args.length;i++) {
			if (args[i].equals("-k")) {
				keyfile = args[i+1];
			} else if (args[i].equals("-f")) {
				input = args[i+1];
			} else if (args[i].equals("-o")) {
				output = args[i+1];
			} else if (args[i].equals("-v")) {
				verbose = true;
			}
		}

		Decrypt d = new Decrypt(keyfile,input,output,verbose);
	}
	
	/**
	 * 
	 * @param key_file The public key file for encryption
	 * @param input_file The file to be encrypted
	 * @param output_file The location for the ciphertext to be written
	 */
	public Decrypt(String key_file,String input_file,String output_file,boolean verbose) {		
		this.key_file = key_file; // TODO check validity
		
		this.verbose = verbose;
		
		log("Keyfile:"+key_file);
		log("Input File:"+input_file);
		log("Output File:"+output_file);
		log("Verbosity:"+verbose);
		
		try {
			this.md = MessageDigest.getInstance("sha1");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		
		this.log("Validating and storing input/output");
		try {
			this.setInputFile(input_file);
			this.setOutputFile(output_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.log("Parsing key file");
		this.parseKeyFile();
		
		
		this.log("Reading input and encrypting");
		try {
			BigInteger intCipher = new BigInteger(FileIO.readFile(this.input));
			this.plaintext = this.decrypt(intCipher.toByteArray());
			for (byte b : this.plaintext) {
				System.out.println(new String(new byte[]{b}));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Implementation of RSAES-OAEP-ENCRYPT
	 * As in RFC2437 PKCS#1 v2.0
	 * 
	 * @param m
	 * @return
	 * @throws DataFormatException 
	 */
	private byte[] decrypt(byte[] C) throws DataFormatException {
		int k = (this.N.bitLength()+7)/8;
		
		log("k:"+k);
		log("C.length:"+C.length);
		
		if(C.length != k) {
			throw new DataFormatException();
		}

		BigInteger c = new BigInteger(1, C);

		BigInteger m = c.modPow(this.D,this.N);

		byte[] EM = Bytes.toFixedLenByteArray(m, k);
		if(EM.length != k) {
		    throw new DataFormatException();
		}
	
		if(EM[0] != 0x00)
		    throw new DataFormatException();
	
		byte[] maskedSeed = new byte[this.md.getDigestLength()];
		System.arraycopy(EM, 1, maskedSeed, 0, maskedSeed.length);
	
		byte[] maskedDB = new byte[k - this.md.getDigestLength() -1];
		System.arraycopy(EM, 1 + this.md.getDigestLength(), maskedDB, 0, maskedDB.length);
		
		MGF1 mgf1 = new MGF1(this.md);
		
		//  c. Let seedMask = MGF (maskedDB, hLen).
		byte[] seedMask = mgf1.generateMask(maskedDB, this.md.getDigestLength());
	
		//  d. Let seed = maskedSeed ^ seedMask.
		byte[] seed = Bytes.xor(maskedSeed, seedMask);
	
		//  e. Let dbMask = MGF (seed, k - hLen - 1).
		byte[] dbMask = mgf1.generateMask(seed, k - this.md.getDigestLength() -1);
	
		//  f. Let DB = maskedDB ^ dbMask.
		byte[] DB = Bytes.xor(maskedDB, dbMask);
	
		byte[] lHash1 = new byte[this.md.getDigestLength()];
		System.arraycopy(DB, 0, lHash1, 0, lHash1.length);
		if(!Bytes.equals(this.md.digest(), lHash1))
		    throw new DataFormatException();
	
		int i = this.md.getDigestLength();
		for( ; i < DB.length; i++)
		    if(DB[i] != 0x00)
		        break;
	
		if(DB[i++] != 0x01)
		    throw new DataFormatException();
	
		int mLen = DB.length - i;
		byte[] M = new byte[mLen];
		System.arraycopy(DB, i, M, 0, mLen);
		return M;
	}
	
	/**
	 * Converts the BigInteger ciphertext bytes to hex and writes them to the output file
	 */
	public void save() {
		String output = String.format("%x", new BigInteger(this.plaintext));
		log(output);
		
		try {
			FileIO.writeFile(output,new File(this.output));
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
			String key_string = FileIO.readFile(this.key_file);
			String[] keys     = key_string.split(",");
			
			this.N = new BigInteger(keys[1]);
			this.D = new BigInteger(keys[3]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void log(String msg) {
		if (this.verbose) {
			System.out.println(msg);
		}
	}
}
