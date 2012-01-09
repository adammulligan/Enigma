package com.cyanoryx.uni.crypto.aes;

import java.security.SecureRandom;
import java.util.zip.DataFormatException;

public class Key {
	private byte[] key;
	private byte[] expanded_key;
	
	private KeySize ksize;
	
	public Key(KeySize k) throws DataFormatException {
		byte[] key = new byte[k.getKeySizeBytes()];
		this.ksize = k;
		
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(key);
        
        this.setKey(key);
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
	
	// TODO comment
	public byte[] getExpandedKey() {
		// If the key has already been expanded, return it rather than re-calculating
		if (expanded_key != null) return this.expanded_key;
	
		// Make following FIPS-197 pseudo-code easier and use their conventions
		int Nk = this.getKeySize().getKeySizeWords();
		int Nr = this.getKeySize().getNumberOfRounds();
		int Nb = 4;
		
		this.expanded_key = new byte[Nb*4*(Nr+1)];
		
	    byte[] temp = new byte[4];
	    
	    // The first round key is the original key
	    // Copy that block to the firt expanded key block
	    for (int index=0;index<4*Nk;index++) {
	       this.expanded_key[index] = this.key[index++];
	    }
	    
	    int i,j=4*Nk;  
	    while(j < 4*Nb*(Nr+1)) {
	       i = j/4; // j is always multiple of 4 here
	       
	       // Get the next 4 bytes of the key
	       for (int k = 0; k < 4; k++) temp[k] = this.expanded_key[j-4+k];
	         
	       if (i % Nk == 0) {
	    	   byte temp2, rcon;
	           byte temp_first = temp[0];
	           
	           for (int k=0;k<4;k++) {
	        	   // Determine which byte of the word to use
	              if (k==3) { temp2 = temp_first; }
	              else { temp2 = temp[k+1]; }
	              
	              // Determine rcon value
	              if (k == 0) { rcon = AES_Constants.RCON[(i/Nk)-1]; }
	              else { rcon = 0; }
	              
	              temp[k] = (byte)(AES_Transformations.getSBoxValue(temp2) ^ rcon);
	           }
	       } else if ((Nk>6) && ((i%Nk)==4)) {
	    	   for (int k=0;k<4;k++) {
	    		   temp[k] = AES_Transformations.getSBoxValue(temp[k]);
	    	   }
	       }
	       
	       for (int k=0;k<4;k++) {
	    	   this.expanded_key[j+k] = (byte)(this.expanded_key[j - 4*Nk + k] ^ temp[k]);
	       }
	       
	       j += 4;
	    }
	      
        return this.expanded_key;
	}
}
