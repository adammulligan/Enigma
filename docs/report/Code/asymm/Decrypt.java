package com.adammulligan.uni;


/**
 * An interactive console application that encrypts
 * a file based on a provided RSA key pair.
 * 
 * @author adammulligan
 *
 */
public class Decrypt {
  private String key_file;
  
  private BigInteger E,N;
  private BigInteger[] plaintext;
  
  /**
   * 
   * @param key_file The public key file for encryption
   */
  public Decrypt(String key_file,String input_file,String output_file) {
    this.key_file = key_file;
  }
  
  /**
   * Takes a String, encrypts using RSA Basic and stores locally
   * 
   * @param message Ciphertext to be decrypted
   */
  private void decrypt(String message) {
    byte[] temp = new byte[1], bytes;
    
    bytes = message.getBytes();

    BigInteger[] converted_bytes = new BigInteger[bytes.length];

    // Convert each byte of the message into a bigint
    for(int i=0; i<converted_bytes.length;i++) {
      temp[0] = bytes[i];
      converted_bytes[i] = new BigInteger(temp);
    }

    this.plaintext = new BigInteger[converted_bytes.length];

    // The actual ecryption!
    // Loop through the array of bigint bytes m,
    // and create an array making c = m
    for(int i=0;i<converted_bytes.length;i++) {
      this.plaintext[i] = converted_bytes[i].modPow(this.E, this.N);
    }
  }

  private void log(String msg) {
    System.out.println(msg);
  }
}
