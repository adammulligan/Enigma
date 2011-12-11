/**
 * 
 */
package com.adammulligan.uni;

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
		byte[][] b = new byte[4][4],bI;
		
		for (int i=0;i<4;i++)
			for (int j=0;j<4;j++)
				b[i][j] = (byte)i;
	
		b  = AES_Transformations.mixColumns(b);
		
		System.out.println("ok");
	}
	
	@Test
	public void testSBox() {
		byte b = 1;
		
		byte bSBox = AES_Transformations.getSBoxValue(b);
		byte bInvSBox = AES_Transformations.getInverseSBoxValue(b);
		
		assert(Bytes.equals(new byte[]{bSBox},new byte[]{bInvSBox}));
	}
}
