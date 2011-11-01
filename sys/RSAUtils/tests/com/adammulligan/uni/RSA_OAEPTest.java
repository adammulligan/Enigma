/**
 * 
 */
package com.adammulligan.uni;

import static org.junit.Assert.*;

import java.io.File;
import java.util.zip.DataFormatException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author adammulligan
 *
 */
public class RSA_OAEPTest {
	private Key public_key,private_key;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.public_key = new PublicKey(new File("id_rsa.pub"));
		this.private_key = new PrivateKey(new File("id_rsa"));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEncryptDecrypt() {
		RSA_OAEP rsa_enc = new RSA_OAEP(this.public_key);
		
		String plaintext       = "Testing";
		byte[] plaintext_bytes = plaintext.getBytes();
		
		byte[] ciphertext;
		
		try {
			 if (rsa_enc.encrypt(plaintext_bytes) instanceof byte[]) {
				 ciphertext = rsa_enc.encrypt(plaintext_bytes);
				 assert(ciphertext.length>0);
				 
				 RSA_OAEP rsa_dec = new RSA_OAEP(this.private_key);
				 
				 try {
					byte[] decrypted_bytes = rsa_dec.decrypt(ciphertext);
					
					assert(Bytes.equals(plaintext_bytes, decrypted_bytes));
				 } catch (InternalError e) {
					fail(e.getMessage());
				 } catch (DataFormatException e) {
					fail(e.getMessage());
				 }
			 } else {
				 fail("byte[] not returned");
			 }
		} catch (DataFormatException dfe) {
			fail(dfe.getMessage());
		} catch (InternalError ie) {
			fail(ie.getMessage());
		}
	}

}
