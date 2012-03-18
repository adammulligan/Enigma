package com.cyanoryx.uni.crypto.aes;

import java.util.zip.DataFormatException;

/**
 * Main AES struct that stores plain/cipher text and encrypts/decrypts them
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
	
	/**
	 * Encrypts the currently stored plaintext using a pre-set key.
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
		
		// Loop through all blocks and run the rounds on them
		for (int b=0;b<blocks;b++) {
			// Get the b'th block of 16 bytes to be ciphered
			byte[] curr_block = new byte[16];
			System.arraycopy(this.plainText, b*16, curr_block, 0, 16);
			// And cipher them
			System.arraycopy(this.cipher(curr_block), 0, this.cipherText, b*16, 16);
		}
		
		return this.cipherText;
	}
	
	/**
	 * Decrypts the currently stored ciphertext using a pre-set key.
	 * 
	 * @return
	 * @throws DataFormatException
	 */
	public byte[] decrypt() throws DataFormatException {
		if (this.cipherText.length%this.getKey().getKeySize().getBlockSizeBytes()!=0) throw new DataFormatException("Cipher text block length error");
		
		int blocks     = this.cipherText.length/16;
		this.plainText = new byte[this.cipherText.length];
		
		// Loop through all blocks and run the rounds on them
		for (int b=0;b<blocks;b++) {
			// Get the b'th block of 16 bytes to be invciphered
			byte[] curr_block = new byte[16];
			System.arraycopy(this.cipherText, b*16, curr_block, 0, 16);
			// And invcipher them
			System.arraycopy(this.invcipher(curr_block), 0, this.plainText, b*16, 16);
		}
		
		return this.plainText;
	}

	/**
	 * Encrypts a 16 byte block as defined by FIPS-197
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
		
		// Last round excludes mixcolumns
		state = AES_Transformations.subBytes(state);
		state = AES_Transformations.shiftRows(state);
		state = AES_Transformations.addRoundKey(state, this.getKey(), this.getKey().getKeySize().getNumberOfRounds());
		
		return AES_Utils.array4xToArray(state);
	}
	
	/**
	 * Decrypts a 16 byte block as defined by FIPS-197
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
		
		// Last round excludes mixcolumns
		state = AES_Transformations.invShiftRows(state);
		state = AES_Transformations.invSubBytes(state);
		state = AES_Transformations.inverseAddRoundKey(state, this.getKey(), 1);
		
		return AES_Utils.array4xToArray(state);
	}
}
