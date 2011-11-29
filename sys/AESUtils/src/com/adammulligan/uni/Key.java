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
		
		this.expanded_key = new int[this.getKeySize().getExpandedKeyWords()];
		
		byte[] rc_array = new byte[KeySize.K256B.getNumberOfRounds()];
        rc_array[0] = (byte)1;

        for (int i = 1; i < rc_array.length; i++) {
            rc_array[i] = AES_Utils.gfProduct(rc_array[i - 1], (byte) 2);
        }
		
	    int[] roundConstant = new int[rc_array.length];

        for (int i = 0; i < rc_array.length; i++) { roundConstant[i] = ((rc_array[i] << 24) & 0xff000000); }
		
		for (int i=0;i<4;i++) {
			this.expanded_key[i] = AES_Utils.byteToWord(this.key[4*i],
														this.key[4*i+1],
														this.key[4*i+2],
														this.key[4*i+3]);
		}
		
		int temp;
		for (int i=4;i<this.getKeySize().getExpandedKeyWords();i++) {
			temp = (0xffffffff & this.expanded_key[i-1]);
			
			if (i%4==0) {
				if (i/4 >= 1 || i/4 <= roundConstant.length) {
					temp = AES_Transformations.subWord(AES_Transformations.rotWord(temp)) ^ roundConstant[(i/4)-1];
				} else { throw new ArrayIndexOutOfBoundsException(); }
			}
			
			this.expanded_key[i] = this.expanded_key[i-4]^temp;
		}
		
		return this.expanded_key;
	}
}
