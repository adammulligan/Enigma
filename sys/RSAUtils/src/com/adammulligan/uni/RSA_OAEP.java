package com.adammulligan.uni;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.DataFormatException;

public class RSA_OAEP {
	private BigInteger E,N;
	
	private Key key;
	
	private MessageDigest md;
	
	public RSA_OAEP(Key key) {
		this.key = key;
		
		this.E = this.key.getExponent();
		this.N = this.key.getN();
		
		try {
			this.md = MessageDigest.getInstance("sha1");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Encrypts a byte[] using RSA-OAEP and returns a byte[] of encrypted values
	 * 
	 * @param byte[] M byte array to be encrypted
	 * @return byte[] C byte array of encrypted values
	 * @throws DataFormatException
	 */
	public byte[] encrypt(byte[] M) throws DataFormatException, InternalError {
		if (this.key instanceof PrivateKey) {
			throw new InternalError("A public key must be passed for encryption");
		}
		
		// The comments documenting this function are from RFC 2437 PKCS#1 v2.0
		
		/*
		   1. Length checking:
		
		      a. If the length of L is greater than the input limitation for the
		         hash function (2^61 - 1 octets for SHA-1), output "label too
		         long" and stop.
		
		      b. If mLen > k - 2hLen - 2, output "message too long" and stop.
		 */
		int mLen = M.length;
		
		int k = (this.N.bitLength()+7)/8;

		if (mLen > (k - 2*this.md.getDigestLength() - 2)) {
			throw new DataFormatException("Block size too large");
		}
		
		/*
		 	b. Generate an octet string PS consisting of k - mLen - 2hLen - 2
         	zero octets.  The length of PS may be zero.
		 */
		byte[] PS = new byte[k - mLen - 2*this.md.getDigestLength() - 2];
		
		/*
		    c. Concatenate lHash, PS, a single octet with hexadecimal value
		       0x01, and the message M to form a data block DB of length k -
		       hLen - 1 octets as
		            DB = lHash || PS || 0x01 || M.
		 */
		byte[] DB = Bytes.concat(this.md.digest(),PS, new byte[]{0x01},M);
		
		// d. Generate a random octet string seed of length hLen (hLen = hash function output length in octets)
		SecureRandom rng = new SecureRandom();
		byte[] seed = new byte[this.md.getDigestLength()];
		rng.nextBytes(seed);
		
		// e. Let dbMask = MGF(seed, k - hLen - 1).
		MGF1 mgf1 = new MGF1(this.md);
		byte[] dbMask = mgf1.generateMask(seed, k - this.md.getDigestLength() - 1);

		// f. Let maskedDB = DB \xor dbMask.
		byte[] maskedDB = Bytes.xor(DB, dbMask);

		// g. Let seedMask = MGF(maskedDB, hLen).
		byte[] seedMask = mgf1.generateMask(maskedDB, this.md.getDigestLength());

		// h. Let maskedSeed = seed \xor seedMask.
		byte[] maskedSeed = Bytes.xor(seed, seedMask);

		/*
		 	i. Concatenate a single octet with hexadecimal value 0x00,
		       maskedSeed, and maskedDB to form an encoded message EM of
		       length k octets as
		
		            EM = 0x00 || maskedSeed || maskedDB.
		 */
		byte[] EM = Bytes.concat(new byte[]{ 0x00 }, maskedSeed, maskedDB);
		
		/*
		 	a. Convert the encoded message EM to an integer message
         	   representative m (see Section 4.2):

            		m = OS2IP (EM).
		 */
		BigInteger m = new BigInteger(1,EM);
		
		/*
		 	b. Apply the RSAEP encryption primitive (Section 5.1.1) to the RSA
         	   public key (n, e) and the message representative m to produce
               an integer ciphertext representative c:

            		c = RSAEP ((n, e), m).
		 */
		BigInteger c = m.modPow(this.E,this.N);
		
		/*
		 	c. Convert the ciphertext representative c to a ciphertext C of
         	   length k octets (see Section 4.1):

            		C = I2OSP (c, k).
		 */
		byte[] C = Bytes.toFixedLenByteArray(c, k);
		
		if(C.length != k) {
			throw new DataFormatException();
		}
		
		return C;
	}
	
	/**
	 * Takes a byte[] of encrypted values and uses RSAES-OAEP-DECRYPT to decrypt them
	 * 
	 * @param byte[] C Array of encrypted values
	 * @return byte[] M Array of decrypted values
	 * @throws DataFormatException
	 */
	public byte[] decrypt(byte[] C) throws DataFormatException, InternalError {
		if (this.key instanceof PublicKey) {
			throw new InternalError("A private key must be passed for decryption");
		}
		
		/*
		 	1. Length checking:

		      a. If the length of L is greater than the input limitation for the
		         hash function (2^61 - 1 octets for SHA-1), output "decryption
		         error" and stop. (NOTE: labels are not used in this implementation)
		
		      b. If the length of the ciphertext C is not k octets, output
		         "decryption error" and stop.
		 */
		int k = (this.N.bitLength()+7)/8;
		
		if(C.length != k) {
			throw new DataFormatException();
		}
		
		//c. If k < 2hLen + 2, output "decryption error" and stop
		
		if (k < (2*this.md.getDigestLength()+2)) {
			throw new DataFormatException("Decryption error");
		}

		/*
		 	2.    RSA decryption:

			      a. Convert the ciphertext C to an integer ciphertext
			         representative c (see Section 4.2):
			
			            c = OS2IP (C).
		 */
		BigInteger c = new BigInteger(1, C);

		/*
		 	b. Apply the RSADP decryption primitive (Section 5.1.2) to the
		       RSA private key K and the ciphertext representative c to
		       produce an integer message representative m:
		
		            m = RSADP (K, c).
		 */
		BigInteger m = c.modPow(this.E,this.N);

		/*
		 	c. Convert the message representative m to an encoded message EM
		       of length k octets (see Section 4.1):
		
		            EM = I2OSP (m, k).
		 */
		byte[] EM = Bytes.toFixedLenByteArray(m, k);
		if(EM.length != k) {
		    throw new DataFormatException();
		}
	
		/*
		 	3. EME-OAEP decoding:

		      a. If the label L is not provided, let L be the empty string. Let
		         lHash = Hash(L), an octet string of length hLen (see the note
		         in Section 7.1.1).
		
		      b. Separate the encoded message EM into a single octet Y, an octet
		         string maskedSeed of length hLen, and an octet string maskedDB
		         of length k - hLen - 1 as
		
		            EM = Y || maskedSeed || maskedDB.
		 */
		if(EM[0] != 0x00) throw new DataFormatException();
	
		byte[] maskedSeed = new byte[this.md.getDigestLength()];
		System.arraycopy(EM, 1, maskedSeed, 0, maskedSeed.length);
	
		byte[] maskedDB = new byte[k - this.md.getDigestLength() -1];
		System.arraycopy(EM, 1 + this.md.getDigestLength(), maskedDB, 0, maskedDB.length);
		
		//  c. Let seedMask = MGF (maskedDB, hLen).
		MGF1 mgf1 = new MGF1(this.md);
		byte[] seedMask = mgf1.generateMask(maskedDB, this.md.getDigestLength());
	
		//  d. Let seed = maskedSeed ^ seedMask.
		byte[] seed = Bytes.xor(maskedSeed, seedMask);
	
		//  e. Let dbMask = MGF (seed, k - hLen - 1).
		byte[] dbMask = mgf1.generateMask(seed, k - this.md.getDigestLength() -1);
	
		//  f. Let DB = maskedDB ^ dbMask.
		byte[] DB = Bytes.xor(maskedDB, dbMask);
		
		/*
		 	g. Separate DB into an octet string lHash' of length hLen, a
		       (possibly empty) padding string PS consisting of octets with
		       hexadecimal value 0x00, and a message M as
		
		            DB = lHash' || PS || 0x01 || M.
		
		       If there is no octet with hexadecimal value 0x01 to separate PS
		       from M, if lHash does not equal lHash', or if Y is nonzero,
		       output "decryption error" and stop.  (See the note below.)
		 */
		byte[] lHash1 = new byte[this.md.getDigestLength()];
		System.arraycopy(DB, 0, lHash1, 0, lHash1.length);
		if(!Bytes.equals(this.md.digest(), lHash1))
		    throw new DataFormatException("Decryption error");
	
		int i = this.md.getDigestLength();
		for( ; i < DB.length; i++) {
		    if(DB[i] != 0x00) break;
		}
	
		if(DB[i++] != 0x01) throw new DataFormatException();
	
		// 4. Output the message M.
		int mLen = DB.length - i;
		byte[] M = new byte[mLen];
		System.arraycopy(DB, i, M, 0, mLen);
		return M;
	}
}
