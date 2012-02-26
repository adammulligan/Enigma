package com.cyanoryx.uni.crypto.dh;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.cyanoryx.uni.common.Bytes;

/**
 * Diffie-Hellman Key Agreement protocol
 * Takes a private key, and received public key to generate a shared 
 * secret between two parties
 * 
 * @author adammulligan
 *
 */
public class KeyAgreement {
  private PublicKey sender_key;
  
  private MessageDigest md;
  
  private BigInteger x,counter;
  
  /**
   * 
   * @param priv_key Personal private key
   * @param sender_key Public key of the second party
   */
  public KeyAgreement(PrivateKey priv_key, PublicKey sender_key) {
    this.sender_key = sender_key;
    
    this.counter = new BigInteger("0");
    
    try {
      this.md = MessageDigest.getInstance("sha1");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    
    this.x = priv_key.getX();
  }
  
  /**
   * Generates ZZ, as defined in RFC 2631
   * 
   * @return ZZ - byte array where ZZ = yB^x (mod p)
   */
  private byte[] generateSharedSecret() {
    BigInteger y = this.sender_key.getY();
    BigInteger p = this.sender_key.getP();
    
    BigInteger ZZ = y.modPow(this.x, p);
    
    return ZZ.toByteArray();
  }
  
  /**
   * Generates KEK, as defined in RFC 2631
   * 
   * @return KEK - byte array where KEK = ZZ || (OID || counter || Key Length)
   */
  public byte[] generateKeyMaterial() {
    this.counter = this.counter.add(BigInteger.ONE);
    
    byte[] ZZ = this.generateSharedSecret();
    
    byte[] OID = "2.16.840.1.101.3.4.1".getBytes();
    byte[] KeyLength = String.format("%x",
                       new BigInteger("256".getBytes())).getBytes();
    byte[] Counter = Bytes.toFixedLenByteArray(this.counter,4);
    
    byte[] KEK = Bytes.concat(ZZ,Bytes.concat(OID,Counter,KeyLength));
    
    this.md.update(KEK);
    return this.md.digest();
  }
}
