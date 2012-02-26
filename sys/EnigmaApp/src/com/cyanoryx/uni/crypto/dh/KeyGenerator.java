package com.cyanoryx.uni.crypto.dh;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Diffie-Hellman Public/Private Key Pair generator
 * 
 * See http://www.ietf.org/rfc/rfc2631.txt
 * 
 * @author adammulligan
 *
 */
public class KeyGenerator {
  public static int SIZE_DEFAULT = 256, SIZE_MAX = 16384, SIZE_MIN = 256;
  
  private int keysize = SIZE_DEFAULT;
  
  private BigInteger g,p;
  
  /**
   * Constructor
   * 
   * @param size Size of the keys in bits
   */
  public KeyGenerator(int size) throws InternalError {
    this.setKeySize(size);
  }
  
  /**
   * Create a key-pair based upon a received public key.
   * 
   * @param size
   * @param key
   */
  public KeyGenerator(int size,PublicKey key) {
    this.setKeySize(size);
    
    this.g = key.getG();
    this.p = key.getP();
  }
  
  /**
   * Generates a pair of priv/pub keys for a Diffie-Hellman 
   * key exchange
   * 
   * See section 2.1.1 RFC 2631
   * 
   * @return {x,y} - Private/Public DH Key Pair
   */
  public Key[] generatePair() {
    BigInteger[] pq = this.generatePrimes(80);
    
    /*
     * p is a large prime
     * q is a large prime
     */
    this.p = (this.p == null || this.p.compareTo(BigInteger.ONE)==-1) ? pq[0] : this.p;
    BigInteger q = pq[1];
    
    // Private key exponent
    // xa is party a's private key
    BigInteger x;
    do {
      x = new BigInteger(p.bitLength()-1,new SecureRandom());
    } while(x.compareTo(BigInteger.ZERO)!=1 ||
        x.compareTo(p.subtract(BigInteger.ONE))!=-1);
    
    if (this.g == null || this.g.compareTo(BigInteger.ZERO)==0) {
      /*
       * h is any integer with 1 < h < p-1 such
       * that h{(p-1)/q} mod p > 1
       */
      BigInteger h;
      do {
        h = new BigInteger(p.bitLength()-1,new SecureRandom());
        g = h.modPow((p.subtract(BigInteger.ONE)).divide(q),p); // h{(p-1)/q} mod p
      } while (h.compareTo(BigInteger.ONE)!=1 || // 1 < h
           h.compareTo(p.subtract(BigInteger.ONE))!=-1 || // h < p-1
           g.compareTo(BigInteger.ONE)!=1); // h{(p-1)/q} mod p > 1
    }
    
    // Public key
    // public key; ya = g ^ xa mod p
    BigInteger y = this.g.modPow(x,p);
    
    Key priv = new PrivateKey(x);
    Key pub  = new PublicKey(y,this.g,this.p);
    
    // {Priv,Pub}
    return new Key[]{priv,pub};
  }
  
  /**
   * Generates a pair of prime numbers using java.math.BigInteger
   * 
   * @param certainty - Prime certainty
   * @return {p,q} - Prime pair
   */
  private BigInteger[] generatePrimes(int certainty) {
    BigInteger p,q;
    
    p = new BigInteger(this.keysize/2,certainty,new SecureRandom());
    q = new BigInteger(this.keysize/2,certainty,new SecureRandom());
    
    if (p == q) this.generatePrimes(certainty);
    
    BigInteger[] pq = new BigInteger[2];
    pq[0] = p;
    pq[1] = q;
    
    return pq; 
  }
  
  private void setKeySize(int size) throws InternalError {
    if (size<SIZE_MAX) {
      this.keysize = size;
    } else {
      throw new InternalError("Key size must adhere to "+SIZE_MIN+" < k < "+SIZE_MAX);
    }
  }
}
