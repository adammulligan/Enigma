package com.cyanoryx.uni.crypto.aes;

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.util.zip.DataFormatException;

import com.cyanoryx.uni.common.Bytes;

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
	
	public void setIV(byte[] IV) throws InvalidAlgorithmParameterException {
		int blockSize = this.getKey().getKeySize().getBlockSizeBytes();
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
		byte[] iv = new byte[this.getKey().getKeySize().getBlockSizeBytes()];
		
		SecureRandom rng = new SecureRandom();
		rng.nextBytes(iv);
		
		return iv;
	}
	
	public static void main(String[] args) {
		AES aes = new AES();
		try {
			aes.setKey(new Key(KeySize.K256));
			aes.setPlainText("Hello".getBytes());
			aes.setCipherText(aes.encrypt());
			
			byte[] decrypted = aes.decrypt();
			if (Bytes.equals(decrypted,("Hello".getBytes()))) {
				System.out.println("It works!");
			} else {
				System.out.println("It doesn't work :(");
			}
		} catch (DataFormatException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws DataFormatException
	 */
	public byte[] encrypt() throws DataFormatException {
		if (this.plainText.length%16!=0) {
			this.plainText = AES_Utils.pad(this.plainText, this.getKey().getKeySize());
		}
		
		int blocks      = this.plainText.length/16;
		this.cipherText = new byte[this.plainText.length];
		
		for (int b=0;b<blocks;b++) {
			byte[] curr_block = new byte[16];
			System.arraycopy(this.plainText, b*16, curr_block, 0, 16);
			System.arraycopy(this.cipher(curr_block), 0, this.cipherText, b*16, 16);
		}
		
		return this.cipherText;
	}
	
	/**
	 * 
	 * @return
	 * @throws DataFormatException
	 */
	public byte[] decrypt() throws DataFormatException {
		if (this.cipherText.length%this.getKey().getKeySize().getBlockSizeBytes()!=0) throw new DataFormatException("Cipher text block length error");
		
		int blocks     = this.cipherText.length/16;
		this.plainText = new byte[this.cipherText.length];
		
		for (int b=0;b<blocks;b++) {
			byte[] curr_block = new byte[16];
			System.arraycopy(this.cipherText, b*16, curr_block, 0, 16);
			System.arraycopy(this.invcipher(curr_block), 0, this.plainText, b*16, 16);
		}
		
		return this.plainText;
	}

	/**
	 * 
	 * @param block
	 * @return
	 * @throws DataFormatException
	 */
	private byte[] cipher(byte[] block) throws DataFormatException {
		byte[][] state = AES_Utils.arrayTo4xArray(block);
		
		state = AES_Transformations.addRoundKey(state, this.getKey(), 0);
		
		for (int r=1;r<this.getKey().getKeySize().getNumberOfRounds();r++) {
			state = AES_Transformations.subBytes(state);
			state = AES_Transformations.shiftRows(state);
			state = AES_Transformations.mixColumns(state);
			state = AES_Transformations.addRoundKey(state, this.getKey(), r);
		}
		
		state = AES_Transformations.subBytes(state);
		state = AES_Transformations.shiftRows(state);
		state = AES_Transformations.addRoundKey(state, this.getKey(), this.getKey().getKeySize().getNumberOfRounds());
		
		return AES_Utils.array4xToArray(state);
	}
	
	/**
	 * 
	 * @param block
	 * @return
	 * @throws DataFormatException
	 */
	private byte[] invcipher(byte[] block) throws DataFormatException {
		byte[][] state = AES_Utils.arrayTo4xArray(block);
		
		state = AES_Transformations.inverseAddRoundKey(state, this.getKey(), this.getKey().getKeySize().getNumberOfRounds()+1);
		
		for (int r=this.getKey().getKeySize().getNumberOfRounds();r>1;r--) {
			state = AES_Transformations.invShiftRows(state);
			state = AES_Transformations.invSubBytes(state);
			state = AES_Transformations.inverseAddRoundKey(state, this.getKey(), r);
			state = AES_Transformations.inverseMixColumns(state);
		}
		
		state = AES_Transformations.invShiftRows(state);
		state = AES_Transformations.invSubBytes(state);
		state = AES_Transformations.inverseAddRoundKey(state, this.getKey(), 1);
		
		return AES_Utils.array4xToArray(state);
	}
}
