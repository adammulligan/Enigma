package com.adammulligan.uni;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.DataFormatException;

/**
 * An interactive console application that encrypts a file based on a provided RSA key pair.
 * 
 * @author adammulligan
 *
 */
public class Encrypt {
	private String key_file, input, output;
	private boolean verbose = false;
	
	private BigInteger E,N;
	private byte[] ciphertext;
	
	private MessageDigest md;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String keyfile,input,output;
		keyfile = input = output = null;
		boolean verbose = false;
		
		for (int i=0;i<args.length;i++) {
			if (args[i].equals("-k")) { // Public keyfile
				keyfile = args[i+1];
			} else if (args[i].equals("-f")) { // Input file to be encrypted
				input = args[i+1];
			} else if (args[i].equals("-o")) { // Output file location
				output = args[i+1];
			} else if (args[i].equals("-v")) { // Verbosity
				verbose = true;
			}
		}
		
		Encrypt e = new Encrypt(keyfile,input,output,verbose);
		e.save();
	}
	
	/**
	 * 
	 * @param key_file The public key file for encryption
	 * @param input_file The file to be encrypted
	 * @param output_file The location for the ciphertext to be written
	 */
	public Encrypt(String key_file,String input_file,String output_file,boolean verbose) {
		this.verbose = verbose;
		
		try {
			this.md = MessageDigest.getInstance("sha1");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		
		this.log("Validating and storing input/output/keyfile...");
		try {
			this.setInputFile(input_file);
			this.setOutputFile(output_file);
			this.setKeyFile(key_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log("Values received...");
		log("Keyfile: "+key_file);
		log("Input File: "+input_file);
		log("Output File: "+output_file);
		
		this.log("Parsing key file...");
		this.parseKeyFile();
		
		log("E:"+this.E);
		log("N:"+this.N);
		
		this.log("Reading input and encrypting...");
		try {
			this.ciphertext = this.encrypt(FileIO.readFile(this.input).getBytes());
		} catch (DataFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
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
	private byte[] encrypt(byte[] M) throws DataFormatException {
		// length mLen of m 
		int mLen = M.length;
		
		int k = (this.N.bitLength()+7)/8;

		if (mLen > (k - 2*this.md.getDigestLength() - 2)) {
			throw new DataFormatException("Block size too large");
		}
		
		byte[] PS = new byte[k - mLen - 2*this.md.getDigestLength() - 2];
		
		byte[] DB = Bytes.concat(this.md.digest(),PS, new byte[]{0x01},M);
		
		SecureRandom rng = new SecureRandom();
		byte[] seed = new byte[this.md.getDigestLength()];
		rng.nextBytes(seed);
		
		MGF1 mgf1 = new MGF1(this.md);
		byte[] dbMask = mgf1.generateMask(seed, k - this.md.getDigestLength() - 1);

		byte[] maskedDB = Bytes.xor(DB, dbMask);

		byte[] seedMask = mgf1.generateMask(maskedDB, this.md.getDigestLength());

		byte[] maskedSeed = Bytes.xor(seed, seedMask);

		byte[] EM = Bytes.concat(new byte[]{ 0x00 }, maskedSeed, maskedDB);
		
		BigInteger m = new BigInteger(1,EM);
		
		BigInteger c = m.modPow(this.E,this.N);
		
		byte[] C = Bytes.toFixedLenByteArray(c, k);
		
		if(C.length != k) {
			throw new DataFormatException();
		}
		
		return C;
	}
	
	/**
	 * Converts the BigInteger ciphertext bytes to hex and writes them to the output file
	 */
	public void save() {
		// TODO: convert to hex
		//String output = String.format("%x", new BigInteger(this.ciphertext));
		BigInteger intCipher = new BigInteger(this.ciphertext);
		String output = intCipher.toString();
		
		try {
			log("Saving encrypted txt to "+this.output+"...");
			FileIO.writeFile(output,new File(this.output));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setKeyFile(String file) throws IOException {
		File filename = new File(file);
		
		if (!filename.exists()) {
	      throw new IOException("Key file does not exist");
	    }
		
	    if (!filename.isFile()) {
	      throw new IllegalArgumentException("Key file should not be a directory: " + filename);
	    }
	    
	    this.key_file = file;
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
			this.E = new BigInteger(keys[2]);
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
