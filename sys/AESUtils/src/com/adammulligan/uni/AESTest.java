/**
 * 
 */
package com.adammulligan.uni;

import static org.junit.Assert.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author adammulligan
 *
 */
public class AESTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAES() {
		AES aes = new AES();
		aes.setKey(new Key(KeySize.K128B));
		aes.setMode(Mode.ECB);
		aes.setPadding(Padding.NONE);
		aes.setPlainText("Test".getBytes());
		
		try {
			aes.cipher();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Ok");
	}

}
