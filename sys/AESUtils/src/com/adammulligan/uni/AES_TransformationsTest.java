/**
 * 
 */
package com.adammulligan.uni;

import java.util.zip.DataFormatException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author adammulligan
 *
 */
public class AES_TransformationsTest {

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
	public void testShiftRows() {
		byte[] b  = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		byte[] bS = AES_Transformations.shiftRows(b);
		byte[] bIS = AES_Transformations.invShiftRows(bS);
		
		assert(Bytes.equals(b, bIS));
	}

	@Test
	public void testMixColumns() {
		byte[] b   = new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		byte[] bM,bIM = new byte[]{};
		
		try {
			bM = AES_Transformations.mixColumns(b);
			bIM = AES_Transformations.inverseMixColumns(bM);
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assert(Bytes.equals(b, bIM));
	}
	
	@Test
	public void testSBox() {
		byte b = 1;
		
		byte bSBox = AES_Transformations.getSBoxValue(b);
		byte bInvSBox = AES_Transformations.getInverseSBoxValue(b);
		
		assert(Bytes.equals(new byte[]{bSBox},new byte[]{bInvSBox}));
	}
}
