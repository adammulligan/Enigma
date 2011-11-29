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
		aes.setPlainText(text_bytes);
		aes.setKey(new Key(KeySize.K128B));
		aes.setMode(Mode.ECB);
		aes.setPadding(Padding.PKCS5PADDING);
		
		byte[] encrypted,decrypted,test;
		try {
			 encrypted = aes.cipher();
			 test = aes.getCipherText();
			 decrypted = aes.invcipher();
			 
			 System.out.println("ok");
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] cipher() throws InvalidAlgorithmParameterException,
								  DataFormatException,
								  NoSuchAlgorithmException {
		byte[] last_block = new byte[16];
		
		if (this.getMode()==Mode.CBC) { 
			if (this.getIV()==null) throw new InvalidAlgorithmParameterException("No IV");
			System.arraycopy(this.getIV(), 0, last_block, 0, 16);
		}
		
		byte[] padded_plaintext = AES_Utils.pad(this.getPlainText(), this.getKey().getKeySize(), this.getPadding());
		this.cipherText         = new byte[padded_plaintext.length];
		
		int block_size = this.getKey().getKeySize().getBlockSizeBytes();
		
		for (int i=0;i<padded_plaintext.length;i+=block_size) {
			byte[] block = new byte[this.getKey().getKeySize().getBlockSizeBytes()];
			System.arraycopy(padded_plaintext,i,block,0,block_size);
			
			if (this.getMode() == Mode.CBC) {
				for (int j=0;j<block.length;j++) {
					block[j] ^= (0xff & last_block[j]);
				}
			}
			
			block = AES_Transformations.addRoundKey(block, this.getKey().getExpandedKey(), 0);
			
			int num_rounds = this.getKey().getKeySize().getNumberOfRounds();
			for (int r=0;r<num_rounds-1;r++) {
				block = AES_Transformations.subBytes(block);
				block = AES_Transformations.shiftRows(block);
				block = AES_Transformations.mixColumns(block);
				block = AES_Transformations.addRoundKey(block, this.getKey().getExpandedKey(), r+1);
			}
			
			// Last round
			block = AES_Transformations.subBytes(block);
			block = AES_Transformations.shiftRows(block);
			block = AES_Transformations.addRoundKey(block, this.getKey().getExpandedKey(), num_rounds);
			
			System.arraycopy(block,0,this.cipherText,i,block_size);
			if (this.getMode() == Mode.CBC) {
				System.arraycopy(block, 0, last_block, 0, block.length);
			}
		}
		
		return this.cipherText;
	}
	
	public byte[] invcipher() throws InvalidAlgorithmParameterException,
									 DataFormatException {
		byte[] last_block = new byte[16];
		
		if (this.getMode() == Mode.CBC){
			if (this.getIV()==null) throw new InvalidAlgorithmParameterException("No IV");
			System.arraycopy(this.getIV(),0,last_block,0,16);
		}
		
		// Copy the cipher text in to a temporary array
		byte[] tmp = new byte[this.getCipherText().length];
		System.arraycopy(this.getCipherText(),0,tmp,0,this.getCipherText().length);
		
		for (int i=0;i<tmp.length;i+=this.getKey().getKeySize().getBlockSizeBytes()) {
			byte[] block = new byte[this.getKey().getKeySize().getBlockSizeBytes()];
			
			// Copy the current block to the temp block array
			// ECB and CBC are the same in this case
			System.arraycopy(tmp,i,block,0,this.getKey().getKeySize().getBlockSizeBytes());
			
			byte[] block_cipher = new byte[16];
			System.arraycopy(block,0,block_cipher,0,this.getKey().getKeySize().getBlockSizeBytes());
			
			int num_rounds = this.getKey().getKeySize().getNumberOfRounds();
			block = AES_Transformations.addRoundKey(block, this.getKey().getExpandedKey(), num_rounds);
			
			for (int r=num_rounds-1;r>=0;r--) {
				block = AES_Transformations.invShiftRows(block);
				block = AES_Transformations.invSubBytes(block);
				block = AES_Transformations.addRoundKey(block, this.getKey().getExpandedKey(), r+1);
				block = AES_Transformations.inverseMixColumns(block);
			}
			
			block = AES_Transformations.invShiftRows(block);
			block = AES_Transformations.invSubBytes(block);
			block = AES_Transformations.addRoundKey(block, this.getKey().getExpandedKey(), 0);
			
			if (this.getMode() == Mode.CBC) {
				for (int j=0;j<block.length;j++) {
					block[j] ^= (0xff & last_block[j]);
				}
				last_block = block_cipher;
			}
			System.arraycopy(block,0,tmp,i,this.getKey().getKeySize().getBlockSizeBytes());
		}
		
		this.plainText = new byte[tmp.length];
		System.arraycopy(tmp,0,this.plainText,0,tmp.length);
		
		//return AES_Utils.unpad(this.plainText, this.getPadding());
		return this.getPlainText();
	}
}
