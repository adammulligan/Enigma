package com.cyanoryx.uni.crypto.cert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;

import com.cyanoryx.uni.crypto.rsa.*;

/**
 * Certificate Authority utilities.
 * Signs keys, and verifies certificates
 * 
 * @author adammulligan
 *
 */
public class CertificateAuthority {
	Key pub,priv;
	
	/**
	 * Constructs object for use for verification and signing.
	 * 
	 * @param pub
	 * @param priv
	 */
	public CertificateAuthority(PublicKey pub, PrivateKey priv) {
		this.pub = pub;
		this.priv = priv;
	}
	
	/**
	 * Constructs object for use for verification only.
	 * 
	 * @param pub
	 */
	public CertificateAuthority(PublicKey pub) {
		this.pub = pub;
	}
	
	public boolean verify(Certificate cert) throws DataFormatException {
		byte[] signature = cert.getSignature();
		byte[] key		 = cert.getSubject_key();
		
		RSA_PSS rsa = new RSA_PSS(this.pub);
		return rsa.verify(key, signature);
	}
	
	/**
	 * Returns a signature for a given key byte array.
	 * 
	 * @param key
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public byte[] sign(byte[] key) throws FileNotFoundException, IOException, DataFormatException {
		if (this.priv==null) throw new DataFormatException("No private key found");
		
		RSA_PSS rsa = new RSA_PSS(this.priv);
		return rsa.sign(key);
	}
}
