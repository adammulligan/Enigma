package com.adammulligan.uni;

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;

/**
 * Base struct for AES implementation to use - for storing Plain/Ciphertext, IV, etc
 * 
 * @author adammulligan
 *
 */
public class AES {
	protected byte[] cipherText,plainText,key,IV;
	protected Mode mode;
	protected Padding padding;
	
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

	/*
	 * public byte[] cipher() {}
	 * public byte[] invCipher() {}
	 * 
	 */
}
