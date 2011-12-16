package com.adammulligan.uni;

import com.adammulligan.uni.KeySize;

import java.security.SecureRandom;
import java.util.zip.DataFormatException;

public class Key {
	private byte[] key;
	
	private KeySize ksize;
	
	public Key(KeySize k) throws DataFormatException {
		byte[] key = new byte[k.getKeySizeBytes()];
		this.ksize = k;
		
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(key);
        
        this.setKey(key);
	}
	
	private Object makeKey(byte[] m) {
		
	}
	
	public byte[] getKey() {
		return this.key;
	}
	
	public void setKey(byte[] key) throws DataFormatException {
		if (key.length!=this.getKeySize().getKeySizeBytes()) throw new DataFormatException("Key length does not much expected size ("+this.getKeySize().getKeySizeBytes()+")");
		this.key = key;
	}
	
	public KeySize getKeySize() {
		return this.ksize;
	}
}
