package com.adammulligan.uni;

import com.adammulligan.uni.KeySize;

import java.security.SecureRandom;
import java.util.zip.DataFormatException;

public class Key {
	private byte[] key;
	private byte[] expanded_key;
	
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
	
	/*public byte[] getExpandedKey() throws DataFormatException {
		if (expanded_key != null) return this.expanded_key;
		
		// Following naming conventions from FIPS-197
		// Makes working from pseudo code easier on the brain
		int Nk = this.getKeySize().getKeySizeWords();
		int Nb = 4;
		int Nr = this.getKeySize().getNumberOfRounds();
		
		this.expanded_key = new byte[Nb*4*(Nr+1)];
		byte[] last_block = this.getKey();
		
		for (int r=0;r<Nr;r++) { // Each round
			byte[] this_block = new byte[16];
			
			for (int i=0;i<Nb;i++) { // Each block of the expanded key
				// First column of each block, rotword etc
				if (i==0) {
					int n = last_block.length;
					
					byte[] b = AES_Utils.wordToByte(AES_Transformations.rotWord(AES_Utils.byteToWord(last_block[n-4], last_block[n-3], last_block[n-2], last_block[n-1])));
					b = AES_Utils.array4xToArray(AES_Transformations.subBytes(AES_Utils.arrayTo4xArray(b)));
					
					for (int j=(i*4);j<(i*4)+4;j++)
						this_block[j] = (byte) (b[j] ^ last_block[n-(4-j)]) ^ this.Rcon(i);
				} else {
					int n = last_block.length;
					
					byte[] b = AES_Utils.wordToByte(AES_Transformations.rotWord(AES_Utils.byteToWord(last_block[n-4], last_block[n-3], last_block[n-2], last_block[n-1])));
					b = AES_Utils.array4xToArray(AES_Transformations.subBytes(AES_Utils.arrayTo4xArray(b)));
					
					for (int j=(i*4);j<(i*4)+4;j++)
						this_block[j] = (byte) (b[j] ^ last_block[n-(4-j)]);
				}
			}
			
			//last_block = this_block;
		}
		
		// i=0
		// Take Wi-1
		// RotWord
		// SubBytes
		// Wi XOR Wi-4 XOR Rcon(block_number * 4)
		
		// i=1,2,3
		// Wi-1
		// Wi XOR Wi-1
	}*/
	
	// TODO change this
	public byte[] getExpandedKey() {
		//System.out.println("*************\nKEY EXPANSION");
		//System.out.println("Trying to retrieve expanded key...");
		if (expanded_key != null) { 
			//AES.printByteArray("Expanded key: ",this.expanded_key);
			return this.expanded_key;
		}
	
		//System.out.println("No prior expansion found, expanding...");
		int Nk = this.getKeySize().getKeySizeWords();
		int Nb = 4;
		int Nr = this.getKeySize().getNumberOfRounds();
		
		this.expanded_key = new byte[Nb*4*(Nr+1)];
		
	      byte[] temp = new byte[4];
	      // first just copy key to w
	      int j = 0;
	      while (j < 4*Nk) {
	         this.expanded_key[j] = this.key[j++];
	      }
	      // here j == 4*Nk;
	      int i;  
	      while(j < 4*Nb*(Nr+1)) {
	         i = j/4; // j is always multiple of 4 here
	         // handle everything word-at-a time, 4 bytes at a time
	         for (int iTemp = 0; iTemp < 4; iTemp++)
	            temp[iTemp] = this.expanded_key[j-4+iTemp];
	         if (i % Nk == 0) {
	            byte ttemp, tRcon;
	            byte oldtemp0 = temp[0];
	            for (int iTemp = 0; iTemp < 4; iTemp++) {
	               if (iTemp == 3) ttemp = oldtemp0;
	               else ttemp = temp[iTemp+1];
	               if (iTemp == 0) tRcon = this.Rcon(i/Nk);
	               else tRcon = 0;
	               temp[iTemp] = (byte)(AES_Transformations.getSBoxValue(ttemp) ^ tRcon);
	            }
	         }
	         else if (Nk > 6 && (i%Nk) == 4) {
	            for (int iTemp = 0; iTemp < 4; iTemp++)
	               temp[iTemp] = AES_Transformations.getSBoxValue(temp[iTemp]);
	         }
	         for (int iTemp = 0; iTemp < 4; iTemp++)
	            this.expanded_key[j+iTemp] = (byte)(this.expanded_key[j - 4*Nk + iTemp] ^ temp[iTemp]);
	         j = j + 4;
	      }
	      
	    //AES.printByteArray("Expanded key",this.expanded_key);
        return this.expanded_key;
	}
	public byte Rcon(int i) {
		this.loadPowX();
		return powX[i-1];
	}
	
	private byte[] powX = new byte[15];
	private void loadPowX() {
		byte x,xp;
	    x = xp = (byte)0x02;
	    
	    powX[0] = 1; powX[1] = x;
	    
	    for (int i = 2; i < 15; i++) {
	    	xp = AES_Utils.FFMul(xp, x);
	        powX[i] = xp;
	    }
	}
}
