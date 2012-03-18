package com.cyanoryx.uni.crypto.aes;

import java.security.SecureRandom;
import java.util.zip.DataFormatException;

/**
 * Object representation of an AES key that provides methods to generate and expand
 * the key.
 * 
 * @author adammulligan
 *
 */
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
  
  /**
   * Manually set a key rather than having it randomly generated.
   * 
   * @param key
   * @throws DataFormatException
   */
  public void setKey(byte[] key) throws DataFormatException {
    if (key.length!=this.getKeySize().getKeySizeBytes()) throw new DataFormatException("Key length does not much expected size ("+this.getKeySize().getKeySizeBytes()+")");
    this.key = key;
  }
  
  public KeySize getKeySize() {
    return this.ksize;
  }
  
  /**
   * Returns the expanded key required for the round key addition
   * (process as defined in FIPS-197)
   * 
   * @return
   */
  public byte[] getExpandedKey() {
    // If the key has already been expanded, return it rather than re-calculating
    if (expanded_key != null) return this.expanded_key;
  
    // Make following FIPS-197 pseudo-code easier and use their conventions
    int Nk = this.getKeySize().getKeySizeWords();
    int Nr = this.getKeySize().getNumberOfRounds();
    int Nb = 4;
    
    this.expanded_key = new byte[Nb*4*(Nr+1)];
    
    byte[] cur_word = new byte[4];
      
    int i;  
    for(int j=4*Nk; j < 4*Nb*(Nr+1); j+=4) {
       i = j/4;
       
       // Get the next 4 bytes (word) of the key
       for (int k=0;k<4;k++) cur_word[k] = this.expanded_key[j-4+k];
       
       if (i % Nk == 0) {
         // Loop through the current word
         for (int k=0;k<4;k++) {
           // Determine which byte of the word to use
            byte temp = cur_word[(k==3) ? 0 : k+1];
            // Determine rcon value
            byte rcon = (k == 0) ? AES_Constants.RCON[(i/Nk)-1] : 0;
            // xor each byte in the word with temp value and rcon value
            cur_word[k] = (byte)(AES_Transformations.getSBoxValue(temp) ^ rcon);
         }
       // As defined in FIPS-197...
       // For 256-bit keys we apply SubWord() to
       // expanded_key[i-1] before xoring below
       } else if ((Nk==8) && ((i%Nk)==4)) {
         for (int k=0;k<4;k++)
           cur_word[k] = AES_Transformations.getSBoxValue(cur_word[k]);
       }
       
       // The actual work..
       // xor each byte of the current word with the
       // matching byte in the key
       for (int k=0;k<4;k++) {
         this.expanded_key[j+k] = (byte)(this.expanded_key[j - 4*Nk + k] ^ cur_word[k]);
       }
    }
        
    return this.expanded_key;
  }
}
