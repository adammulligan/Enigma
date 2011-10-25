package com.adammulligan.uni;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.zip.DataFormatException;

/**
 * An interactive console application that encrypts a file based on a provided RSA key pair.
 * 
 * @author adammulligan
 *
 */
public class Encrypt {
	private String key_file, input, output;
	
	private int length;
	
	private BigInteger E,N;
	private BigInteger[] ciphertext;
	
	private boolean verbose;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/* TEST */
		String msg = "Hello";
		//System.out.println(msg.getBytes());
		
		Encrypt e = new Encrypt("/Users/adammulligan/id_rsa.pub","/Users/adammulligan/input","/Users/adammulligan/output");
		
		//System.out.println(e.mgf1("test",200));
		e.OAEP_Encode("", "", 2);
		
		//e.save();
	}
	
	public String OAEP_Encode(String M, String P, int emLen) {
		MessageDigest sha1;
		
		try {
			sha1 = MessageDigest.getInstance("SHA1");
			String PS="";
		
			for (int i=1;i<=M.length();i++) {
				PS += 0;
			}
		
			byte[] pHash = sha1.digest(P.getBytes());
			
			
		} catch (NoSuchAlgorithmException e) {
			
		}
	}
	
	/**
	 * 
	 * @param mgfSeed Octet String - Seed from which the output mask is generated
	 * @param maskLen Desired length of the mask in octets
	 * 
	 * @return mask Octet string mask from seed
	 */
	public BigInteger mgf1(String mgfSeed,int maskLen) throws DataFormatException {
		MessageDigest sha1;
		String T = "";
		
		try {
			sha1 = MessageDigest.getInstance("SHA1");
			
			for (int i=0;i<=(Math.ceil((maskLen/sha1.getDigestLength())-1));i++) {
				String C = I2OSP(new BigInteger(Integer.toString(i)),4);
				log("C:"+C);
				
				String tmp="";
				for (byte b : sha1.digest((mgfSeed + C).getBytes())) {
					tmp += b;
				}
				
				T += tmp;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		byte[] TBytes = T.getBytes();
		String result="";
		
		for (int j=0;j<maskLen;j++) {
			try {
				result += TBytes[j];
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}
		
		return new BigInteger(result);
	}
	
	/**
	 * Converts a nonnegative integer to an octet string of a specified length.
	 * 
	 * See section 4.1 of RFC2437 PKCS#1v2.0
	 * 
	 * @param x Non-negative integer to be converted
	 * @param xLen Intended length of the output string
	 * 
	 * @return
	 */
	public String I2OSP(BigInteger x, int size) throws DataFormatException {
		if (x.signum() == -1) {
			throw new DataFormatException("Integer too large");
		}
	
		BigInteger[] xCalc = new BigInteger[size];
		BigInteger xResult = x;
		
		String result="";
		
		for (int i=0;i<size;i++) {
			xCalc[i] = xResult.remainder(new BigInteger("256"));
			
			byte[] xCalcBytes = xCalc[i].toByteArray();
			result += (xCalcBytes[0]);
			
			xResult = xResult.divide(new BigInteger("10"));
			log("xCalc["+i+"]:"+xCalc[i]);
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param key_file The public key file for encryption
	 * @param input_file The file to be encrypted
	 * @param output_file The location for the ciphertext to be written
	 */
	public Encrypt(String key_file,String input_file,String output_file) {
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
		
		this.log("Reading input and encrypting");
		try {
			this.encrypt(this.readFile(this.input));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes a String, encrypts using RSA Basic and stores locally
	 * 
	 * @param message Plaintext to be encrypted
	 */
	private void encrypt(String message) {
		byte[] temp = new byte[1], bytes;
		
		bytes = message.getBytes();

		BigInteger[] converted_bytes = new BigInteger[bytes.length];

		// Convert each byte of the message into a bigint
		for(int i=0; i<converted_bytes.length;i++) {
			temp[0] = bytes[i];
			converted_bytes[i] = new BigInteger(temp);
		}

		this.ciphertext = new BigInteger[converted_bytes.length];

		// The actual encryption!
		// Loop through the array of bigint bytes m, and create an array making c = m
		for(int i=0;i<converted_bytes.length;i++) {
			this.ciphertext[i] = converted_bytes[i].modPow(this.E, this.N);
		}
	}
	
	/**
	 * Converts the BigInteger ciphertext bytes to hex and writes them to the output file
	 */
	public void save() {
		String output="";
		
		// Convert each byte into a hex value
		for (BigInteger c : this.ciphertext) {
			output += c.toString(16).toUpperCase()+",";
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
			
			this.length = Integer.parseInt(keys[0]);
			this.N = new BigInteger(keys[1]);
			this.E = new BigInteger(keys[2]);
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
