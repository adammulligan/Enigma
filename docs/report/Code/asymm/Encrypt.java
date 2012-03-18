package com.adammulligan.uni;

/**
 * 
 * @author adammulligan
 *
 */
public class Encrypt {
  private String key_file;
  
  private int length;
  
  private BigInteger E,N;
  private BigInteger[] ciphertext;
  
  /**
   * 
   * @param key_file The public key file for encryption
   */
  public Encrypt(String key_file) {
    this.key_file = key_file;
  }
  
  /**
   * Takes a String, encrypts using RSA Basic and stores locally
   * 
   * @param message Plaintext to be encrypted
   */
  private void encrypt(String message) {
    byte[] temp = new byte[1], bytes;
    
    bytes = message.getBytes();

    BigInteger[] converted_bytes = new BigInteger[bytes.length];

    // Convert each byte of the message into a bigint
    for(int i=0; i<converted_bytes.length;i++) {
      temp[0] = bytes[i];
      converted_bytes[i] = new BigInteger(temp);
    }

    this.ciphertext = new BigInteger[converted_bytes.length];

    // The actual encryption!
    // Loop through the array of bigint bytes m,
    // and create an array making c = m
    for(int i=0;i<converted_bytes.length;i++) {
      this.ciphertext[i] = converted_bytes[i].modPow(this.E,
                                                     this.N);
    }
  }
  
  private void log(String msg) {
    System.out.println(msg);
  }
}
