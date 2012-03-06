package com.cyanoryx.uni.crypto.cert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.UUID;
import java.util.zip.DataFormatException;

import com.cyanoryx.uni.common.Bytes;
import com.cyanoryx.uni.crypto.rsa.Key;
import com.cyanoryx.uni.crypto.rsa.PrivateKey;
import com.cyanoryx.uni.crypto.rsa.PublicKey;
import com.cyanoryx.uni.crypto.rsa.RSA_PSS;

/**
 * Certificate Authority utilities.
 * Signs keys, and verifies certificates
 * 
 * @author adammulligan
 *
 */
public class CertificateAuthority {
	Key pub,priv;
	
	private static final byte[] ISSUER_ID = "EnigmaCryptoIncCA".getBytes(); 
	
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
	
	/**
	 * Returns a Certificate for the given parameters
	 * 
	 * @param algorithm - Byte representation of the algorithm ID
	 * @param subject_key - Subject's public key
	 * @param subject_name - Subject's name
	 * @param validity_notafter - Expiry date for this certificate
	 * @param validity_notbefore - Start date for this certificate
	 * @return Generated certificate
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public Certificate generate(byte[] algorithm,
			    				PublicKey subject_key,
			    				byte[] subject_key_bytes,
			    				String subject_name,
			    				Calendar validity_notafter,
			    				Calendar validity_notbefore)
				throws FileNotFoundException, IOException, DataFormatException {
		Hashtable<String,byte[]> cert = new Hashtable<String,byte[]>();
		
		byte[] public_key = Bytes.concat(subject_key.getExponent().toByteArray(), subject_key.getN().toByteArray());
		
		SecureRandom rng = new SecureRandom();
		byte[] serial = new byte[32];
		rng.nextBytes(serial);
		
		cert.put("SignatureAlgorithm", algorithm);
		cert.put("Signature", this.sign(public_key));
		cert.put("Issuer", ISSUER_ID);
		cert.put("Serial", serial);
		cert.put("Validity_notAfter", validity_notafter.toString().getBytes());
		cert.put("Validity_notBefore", validity_notbefore.toString().getBytes());
		cert.put("UniqIdent", UUID.randomUUID().toString().getBytes());
		cert.put("Subject_name", subject_name.getBytes());
		cert.put("Subject_algorithm", algorithm);
		cert.put("Subject_key", subject_key_bytes);
		
		return new Certificate(cert);
	}
}
