/**
 * 
 */
package com.cyanoryx.uni.rsa;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.cyanoryx.uni.crypto.rsa.KeyGenerator;

/**
 * @author adammulligan
 *
 */
public class KeyGeneratorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	@Test(expected=InternalError.class)
	public void testInitialise() {
		try {
			KeyGenerator kg = new KeyGenerator(KeyGenerator.SIZE_MAX+1);
		} catch (InternalError ie) {
			
		}
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
