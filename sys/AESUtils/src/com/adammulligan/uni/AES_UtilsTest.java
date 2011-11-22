/**
 * 
 */
package com.adammulligan.uni;

import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author adammulligan
 *
 */
public class AES_UtilsTest {

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
	public void testByteBase64Conv() {
		String tmp = "Hello";
		byte[] b   = tmp.getBytes();
		
		String tmp64 = AES_Utils.byteToBase64(b);
		assert(tmp64 instanceof String);
		
		byte[] tmpB;
		try {
			tmpB = AES_Utils.base64ToByte(tmp64);
			assert(tmpB instanceof byte[]);
			
			assert(tmpB.toString().equals(tmp));
		} catch (IOException e) {
		}
	}

	@Test
	public void testHexByteConv() {
		String tmp = "Hello";
		byte[] b   = tmp.getBytes();
		
		String tmpHex = AES_Utils.byteToHex(b);
		assert(tmpHex instanceof String);
		
		byte[] tmpB = AES_Utils.hexToByte(tmpHex);
		assert(tmpB instanceof byte[]);
		
		assert(tmpB.toString().equals(tmp));
	}
	
	@Test
	public void testWordByteConv() {
		byte[] b = new byte[]{1,2,3,4};
		int tmp = AES_Utils.byteToWord(b[0], b[1], b[2], b[3]);
		byte[] tmpW = AES_Utils.wordToByte(tmp);
		
		assert(Bytes.equals(b, tmpW));
	}
	
	@Test
	public void test4x4Arrays() {
		byte[] b = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		try {
			byte[][] tmpB4 = AES_Utils.arrayTo4xArray(b);
			
			assert(Bytes.equals(AES_Utils.array4xToArray(tmpB4),b));
		} catch (DataFormatException e) {
		}
	}
	
	@Test
	public void testPadding() {
		byte[] b = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12};
		
		try {
			byte[] padded = AES_Utils.pad(b, KeySize.K256B, Padding.PKCS5PADDING);
			byte[] unpadded = AES_Utils.unpad(padded, Padding.PKCS5PADDING);
			
			assert(Bytes.equals(b,unpadded));
		} catch (DataFormatException e) {
		}
	}
}
