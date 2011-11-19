/**
 * 
 */
package com.adammulligan.uni;

import static org.junit.Assert.*;

import java.io.File;
import java.util.zip.DataFormatException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author adammulligan
 *
 */
public class RSA_PSSTest {
	private Key public_key,private_key;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.public_key = new PublicKey(new File("id_rsa.pub"));
		this.private_key = new PrivateKey(new File("id_rsa"));
	}

	@Test
	public void testSignVerify() {
		RSA_PSS sign = new RSA_PSS(this.private_key);
		
		String msg = "Hello";
		
		try {
			byte[] signature = sign.sign(msg.getBytes());
			
			RSA_PSS verify = new RSA_PSS(this.public_key);
			
			boolean verified = verify.verify(msg.getBytes(),signature);
			System.out.println(verified);
		} catch (DataFormatException e) {
			fail(e.getMessage());
		}
	}
}
