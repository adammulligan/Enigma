package com.cyanoryx.uni.crypto.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * 
 * 
 * @author adammulligan
 *
 */
public class KeyGenerator {
  public static int SIZE_DEFAULT = 1024, SIZE_MAX = 16384, SIZE_MIN = 256;
  
  private int keysize = SIZE_DEFAULT;
  
  /**
   * Constructor
   * 
   * @param size Size of the keys in bits
   */
  public KeyGenerator(int size) throws InternalError {
    if (size<SIZE_MAX) {
      this.keysize = size;
    } else {
      throw new InternalError("Key size must adhere to "
                                     +SIZE_MIN+" < k < "+SIZE_MAX);
    }
  }
  
  public BigInteger[] generatePair() {
    BigInteger[] pq = this.generatePrimes(80);
    
    BigInteger p = pq[0];
    BigInteger q = pq[1];
    
    // N = pq
    BigInteger N = p.multiply(q);
    
    // r = (p-1)*(q-1)
    BigInteger phi = p.subtract(BigInteger.valueOf(1));
    phi = phi.multiply(q.subtract(BigInteger.valueOf(1)));
    
    BigInteger E;
    
    do {
      E = new BigInteger(this.keysize/2,
                         new SecureRandom());
    } while((E.compareTo(phi)!=-1)
            || (E.gcd(phi)
            .compareTo(BigInteger.valueOf(1))!=0));
      
    // D = 1/E mod r
    BigInteger D = E.modInverse(phi);
    
    return new BigInteger[]{N,E,D};
  }
  
  private BigInteger[] generatePrimes(int certainty) {
    BigInteger p,q;
    
    p = new BigInteger(this.keysize/2,certainty,
                       new SecureRandom());
    q = new BigInteger(this.keysize/2,certainty,
                       new SecureRandom());
    
    if (p == q) this.generatePrimes(certainty);
    
    BigInteger[] pq = new BigInteger[2];
    pq[0] = p;
    pq[1] = q;
    
    return pq; 
  }
}
