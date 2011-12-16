package com.adammulligan.uni;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.DataFormatException;

/**
 * Base struct for AES implementation to use - for storing Plain/Ciphertext, IV, etc
 * 
 * @author adammulligan
 *
 */
public class AES {
	protected byte[] cipherText,plainText,IV;
	protected Key key;
	protected Mode mode;
	protected Padding padding;
	
	public void setKey(Key k) {
		this.key = k;
	}
	
	public Key getKey() {
		return this.key;
	}
	
	public void setCipherText(byte[] cipherText) {
		this.cipherText = cipherText;
	}
	
	public byte[] getCipherText() {
		return this.cipherText;
	}
	
	public void setPlainText(byte[] plainText) {
		this.plainText = plainText;
	}
	
	public byte[] getPlainText() {
		return this.plainText;
	}
	
	public void setMode(Mode m) {
		this.mode = m;
	}
	
	public Mode getMode() {
		return this.mode;
	}
	
	public void setPadding(Padding p) {
		this.padding = p;
	}
	
	public Padding getPadding() {
		return this.padding;
	}
	
	public void setIV(byte[] IV) throws InvalidAlgorithmParameterException {
		int blockSize = KeySize.K128B.getBlockSizeBytes();
		if (IV.length != blockSize)
			throw new InvalidAlgorithmParameterException("Invalid initialisation vector block size");
		
		// Copy, rather than point to same copy of iv.
		// Idea from watne.seis720.project.AES_Common
        this.IV = new byte[blockSize];
        System.arraycopy(IV, 0, this.IV, 0, blockSize);
	}
	
	public byte[] getIV() {
		return this.IV;
	}
	
	public byte[] generateIV() {
		byte[] iv = new byte[KeySize.K128B.getBlockSizeBytes()];
		
		SecureRandom rng = new SecureRandom();
		rng.nextBytes(iv);
		
		return iv;
	}
	
	public static void main(String[] args) {
		String text = "Test";
		byte[] text_bytes = text.getBytes();
		
		AES aes = new AES();
		aes.setPlainText(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16});
		aes.setKey(new Key(KeySize.K256B));
		aes.setMode(Mode.ECB);
		aes.setPadding(Padding.PKCS5PADDING);
		
		byte[] encrypted,decrypted,test;
		try {
			 encrypted = aes.cipher();
			 aes.setCipherText(encrypted);
			 decrypted = aes.invcipher();
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] cipher() throws DataFormatException {
		System.out.println("\n\n\n\n\n CIPHER");
		
		AES.printByteArray("Key: ",this.getKey().getKey());
		
		byte[][] state = AES_Utils.arrayTo4xArray(this.getPlainText());
		
		AES.printByteArray("Initial state: ", state);
		
		state = AES_Transformations.addRoundKey(state, this.getKey(), 0);
		
		AES.printByteArray("Add round key: ",state);
		
		for (int r=1;r<this.getKey().getKeySize().getNumberOfRounds();r++) {
			System.out.println("*********");
			AES.printByteArray("Round "+r+": ", state);
			state = AES_Transformations.subBytes(state);
			AES.printByteArray("After subBytes: ", state);
			state = AES_Transformations.shiftRows(state);
			AES.printByteArray("After shiftRows: ", state);
			state = AES_Transformations.mixColumns(state);
			AES.printByteArray("After mixColumns: ", state);
			state = AES_Transformations.addRoundKey(state, this.getKey(), r);
			AES.printByteArray("After addRoundKey: ", state);
		}
		
		System.out.println("*********");
		AES.printByteArray("Round "+this.getKey().getKeySize().getNumberOfRounds()+": ", state);
		state = AES_Transformations.subBytes(state);
		AES.printByteArray("After subBytes: ", state);
		state = AES_Transformations.shiftRows(state);
		AES.printByteArray("After shiftRows: ", state);
		state = AES_Transformations.addRoundKey(state, this.getKey(), this.getKey().getKeySize().getNumberOfRounds());
		AES.printByteArray("After addRoundKey: ", state);
		
		return AES_Utils.array4xToArray(state);
	}
	
	public byte[] invcipher() throws DataFormatException {
		System.out.println("\n\n\n\n\n INVCIPHER");
		
		AES.printByteArray("Key: ",this.getKey().getKey());
		
		byte[][] state = AES_Utils.arrayTo4xArray(this.getCipherText());
		
		AES.printByteArray("Initial state: ", state);
		
		state = AES_Transformations.inverseAddRoundKey(state, this.getKey(), this.getKey().getKeySize().getNumberOfRounds()+1);
		
		AES.printByteArray("Inverse add round key: ",state);
		
		for (int r=this.getKey().getKeySize().getNumberOfRounds();r>1;r--) {
			System.out.println("*********");
			AES.printByteArray("Round "+r+": ", state);
			
			state = AES_Transformations.invShiftRows(state);
			AES.printByteArray("After invShiftRows: ", state);
			state = AES_Transformations.invSubBytes(state);
			AES.printByteArray("After invSubBytes: ", state);
			state = AES_Transformations.inverseAddRoundKey(state, this.getKey(), r);
			AES.printByteArray("After invAddRoundKey: ", state);
			state = AES_Transformations.inverseMixColumns(state);
			AES.printByteArray("After invMixColumns: ", state);
		}
		
		System.out.println("*********");
		AES.printByteArray("Round 1: ", state);
		state = AES_Transformations.invShiftRows(state);
		AES.printByteArray("After invShiftRows: ", state);
		state = AES_Transformations.invSubBytes(state);
		AES.printByteArray("After invSubBytes: ", state);
		state = AES_Transformations.inverseAddRoundKey(state, this.getKey(), 1);
		AES.printByteArray("After invAddRoundKey: ", state);
		
		return AES_Utils.array4xToArray(state);
	}
	
	private static String[] hex_digits = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
	public static void printByteArray(String prefix,byte[] a) {
		System.out.print(prefix+" ");
		
		for (int i=0;i<a.length;i++) {
			System.out.print(hex_digits[(a[i] & 0xff) >> 4]+hex_digits[a[i] & 0x0f] + " ");
		}
		
		System.out.println();
	}
	
	public static void printByteArray(String prefix,byte[][] a) {
		System.out.print(prefix+" ");
		
		for (int i=0;i<4;i++) {
			System.out.print("[");
			for (int j=0;j<4;j++) {
				System.out.print(hex_digits[(a[j][i] & 0xff) >> 4]+hex_digits[a[j][i] & 0x0f] + " ");
			}
			System.out.print("]");
		}
		
		System.out.println();
	}
}
