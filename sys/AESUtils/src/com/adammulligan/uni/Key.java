package com.adammulligan.uni;

import com.adammulligan.uni.KeySize;

import java.security.SecureRandom;

public class Key {
	private byte[] key;
	private int[] expanded_key;
	
	private KeySize ksize;
	
	public Key(KeySize k) {
		byte[] key = new byte[k.getKeySizeBytes()];
		this.ksize = k;
		
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(key);
        
        this.setKey(key);
	}
	
	public byte[] getKey() {
		return this.key;
	}
	
	public void setKey(byte[] key) {
		this.key = key;
	}
	
	public KeySize getKeySize() {
		return this.ksize;
	}
	
	public int[] getExpandedKey() {
		if (expanded_key != null) return this.expanded_key;
		
	}
}
