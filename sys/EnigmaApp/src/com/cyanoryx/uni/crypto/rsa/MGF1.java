package com.cyanoryx.uni.crypto.rsa;

import java.security.MessageDigest;

import com.cyanoryx.uni.common.Bytes;

/**
 *  A mask generation function takes an octet string of variable length
 *  and a desired output length as input, and outputs an octet string of
 *  the desired length. There may be restrictions on the length of the
 *  input and output octet strings, but such bounds are generally very
 *  large.  Mask generation functions are deterministic; the octet string
 *  output is completely determined by the input octet string.
 *
 * @author adammulligan
 *
 */
public class MGF1 {
  private final MessageDigest digest;

  public MGF1(MessageDigest digest) {
    this.digest = digest;
  }


  /**
   *  MGF1 is a Mask Generation Function based on a hash function.
   *
   *  MGF1 (mgfSeed, maskLen)
   *  
   *  Options:
   *  Hash     hash function (hLen denotes the length in octets of the hash
   *           function output)
   *
   *  Input:
   *  mgfSeed  seed from which mask is generated, an octet string
   *  maskLen  intended length in octets of the mask, at most 2^32 hLen
   *
   *  Output:
   *  mask     mask, an octet string of length maskLen
   * 
   * @param mgfSeed
   * @param maskLen
   * @return
   */
  public byte[] generateMask(byte[] mgfSeed, int maskLen) {
	// (maskLen / hLen) - 1
    int hashCount = (maskLen + this.digest.getDigestLength() - 1)
    		        / this.digest.getDigestLength();

    byte[] mask = new byte[0];

    // For counter from 0 to \ceil (maskLen / hLen) - 1, do the following:
    for (int i=0;i<hashCount;i++) {
      /*
       * a. Convert counter to an octet string C of length 4 octets (see
       *    Section 4.1):
       *
       *     C = I2OSP (counter, 4) .
       */
      this.digest.update(mgfSeed);
      this.digest.update(new byte[3]);
      this.digest.update((byte)i);
      byte[] hash = this.digest.digest();

      /*
       * b. Concatenate the hash of the seed mgfSeed and C to the octet
       *    string T:
       *
       *     T = T || Hash(mgfSeed || C) 
       */
      mask = Bytes.concat(mask, hash);
    }

    //  4. Output the leading maskLen octets of T as the octet string mask.
    byte[] output = new byte[maskLen];
    System.arraycopy(mask, 0, output, 0, output.length);
    return output;
  }
}
