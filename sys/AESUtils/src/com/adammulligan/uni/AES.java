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
		aes.setKey(new Key(KeySize.K128B));
		aes.setMode(Mode.ECB);
		aes.setPadding(Padding.PKCS5PADDING);
		
		byte[] encrypted,decrypted,test;
		try {
			 encrypted = aes.cipher();
			 aes.setCipherText(encrypted);
			 decrypted = aes.invcipher();
			 
			 System.out.println("ok");
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] cipher() throws DataFormatException {
		int wCount=0;
		
		byte[][] state = AES_Utils.arrayTo4xArray(this.getPlainText());
		
		state = AES_Transformations.addRoundKey(state, this.getKey().getExpandedKey(), wCount++);
		
		for (int r=1;r<this.getKey().getKeySize().getNumberOfRounds();r++) {
			state = AES_Transformations.subBytes(state);
			state = AES_Transformations.shiftRows(state);
			state = AES_Transformations.mixColumns(state);
			state = AES_Transformations.addRoundKey(state, this.getKey().getExpandedKey(), wCount++);
		}
		
		state = AES_Transformations.subBytes(state);
		state = AES_Transformations.shiftRows(state);
		state = AES_Transformations.addRoundKey(state, this.getKey().getExpandedKey(), wCount++);
		
		return AES_Utils.array4xToArray(state);
	}
	
	public byte[] invcipher() throws DataFormatException {
		int wCount=4*4*(this.getKey().getKeySize().getNumberOfRounds()+1);
		
		byte[][] state = AES_Utils.arrayTo4xArray(this.getCipherText());
		
		state = AES_Transformations.inverseAddRoundKey(state, this.getKey().getExpandedKey(), --wCount);
		
		for (int r=1;r<this.getKey().getKeySize().getNumberOfRounds();r++) {
			state = AES_Transformations.invSubBytes(state);
			state = AES_Transformations.invShiftRows(state);
			state = AES_Transformations.inverseMixColumns(state);
			state = AES_Transformations.inverseAddRoundKey(state, this.getKey().getExpandedKey(), --wCount);
		}
		
		state = AES_Transformations.invSubBytes(state);
		state = AES_Transformations.invShiftRows(state);
		state = AES_Transformations.inverseAddRoundKey(state, this.getKey().getExpandedKey(), --wCount);
		
		return AES_Utils.array4xToArray(state);
	}
}
