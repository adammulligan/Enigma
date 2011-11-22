package com.adammulligan.uni;

import java.security.SecureRandom;

public class Key {
	private byte[] key,expanded_key;
	
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
	
	public byte[] getExpandedKey() {
		if (expanded_key != null) return this.expanded_key;
		
		byte[] tmp = new byte[4];
		byte[] output = new byte[this.ksize.getKeySizeBytes()];
		
		int Nk = this.ksize.getKeySizeWords();
		int Nr = Nk + 6;
		int Nb = 4; // Number of words in a block, always 4
		
		int j=0;
		while (j<(4*Nk)) {
			output[j] = this.key[j++];
		}
		
		int i = 0;
		while (j<(4*Nb*(Nr+1))) {
			i = j/4;
			
			for (int tmpI=0;tmpI<4;tmpI++) {
				tmp[tmpI] = output[j-4+tmpI];
			}
			
			if (i % Nk == 0) {
				byte ttmp,tRcon,oldtmp0 = tmp[0];
				
				for (int tmpI=0;tmpI<4;tmpI++) {
					if (tmpI==3) ttmp = oldtmp0;
					else ttmp = tmp[tmpI+1];
					
					if (tmpI==0) tRcon = tab.Rcon(i/Nk);
					else tRcon = 0;
					
					tmp[tmpI] = (byte)(AES_Transformations.getSBoxValue(ttmp) ^ tRcon);
				}
			} else if (Nk > 6 && (i%4)==4) {
				for (int tmpI = 0; tmpI<4; tmpI++) {
					tmp[tmpI] = AES_Transformations.getSBoxValue(tmp[tmpI]);
				}
			}
			
			for (int tmpI=0;tmpI<4;tmpI++) {
				output[j+tmpI] = (byte)(output[j-4*Nk+tmpI] ^ tmp[tmpI]);
			}
			
			j=j+4;
		}
		
		return output;
	}
}
