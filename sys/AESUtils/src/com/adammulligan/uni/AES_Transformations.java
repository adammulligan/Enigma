package com.adammulligan.uni;

import java.util.zip.DataFormatException;

/**
 * Implements AES calculations
 * 
 * @author adammulligan
 *
 */
public class AES_Transformations {
	
	/**
	 * FIPS197:
	 * Function used in the Key Expansion routine that takes a four-byte
	 * word and performs a cyclic permutation
	 *  
	 * The function RotWord() takes a word [a0,a1,a2,a3] as input, performs 
	 * a cyclic permutation, and returns the word [a1,a2,a3,a0].
	 *  
	 * @param word
	 * @return
	 */
	public static int rotWord(int word) {
		return ((0xff & ((word & 0xff000000) >> 24)) |
			   ((0x00ffffff & word) << 8));
	}
	
	public static byte getSBoxValue(byte o) {
		int i = ((o & 0xf0) >> 4);
        int j = (o & 0x0f);

        return AES_Constants.SBOX[i][j];
	}
	
	public static byte getInverseSBoxValue(byte o) {
		int i = ((o & 0xf0) >> 4);
        int j = (o & 0x0f);

        return AES_Constants.INVSBOX[i][j];
	}
	
	/**
	 * Taken from watne.seis720.project
	 * 
	 * @param word
	 * @return
	 */
	public static int subWord(int word) {
		word &= 0xffffffff;

        byte byte1 = (byte)((word & 0xff000000) >> 24);
        byte byte2 = (byte)((word & 0x00ff0000) >> 16);
        byte byte3 = (byte)((word & 0x0000ff00) >> 8);
        byte byte4 = (byte)(word & 0x000000ff);

        byte1 = AES_Transformations.getSBoxValue(byte1);
        byte2 = AES_Transformations.getSBoxValue(byte2);
        byte3 = AES_Transformations.getSBoxValue(byte3);
        byte4 = AES_Transformations.getSBoxValue(byte4);

        return (0xff000000 & (byte1 << 24)) |
        	   (0x00ff0000 & (byte2 << 16)) |
        	   (0x0000ff00 & (byte3 << 8)) |
        	   (0x000000ff & byte4);
	}
	
	public static byte[] subBytes(byte[] block) {
		for (int i=0;i<block.length;i++) {
			block[i] = AES_Transformations.getSBoxValue(block[i]);
		}
		
		return block;
	}
	
	public static byte[] invSubBytes(byte[] block) {
		for (int i=0;i<block.length;i++) {
			block[i] = AES_Transformations.getInverseSBoxValue(block[i]);
		}
		
		return block;
	}
	
	public static byte[] shiftRows(byte[] block) {
		byte[][] state = new byte[4][4];
		try {
			state = AES_Utils.arrayTo4xArray(block);
		} catch (DataFormatException e) {}
		
		byte[][] new_state = new byte[4][4];
		
		// Keep first row
		new_state[0] = state[0];
		
		for (int i=1;i<=3;i++) {
			int offset=i;
			
			System.arraycopy(state[i],offset,new_state[i],0,4-i);
			System.arraycopy(state[i],0,new_state[i],4-i,i);
		}
		
		return AES_Utils.array4xToArray(new_state);
	}
	
	public static byte[] invShiftRows(byte[] block) {
		byte[][] state = new byte[4][4];
		try {
			state = AES_Utils.arrayTo4xArray(block);
		} catch (DataFormatException e) {}
		
		byte[][] new_state = new byte[4][4];
		
		// Keep first row
		new_state[0] = state[0];
		
		for (int i=1;i<=3;i++) {
			System.arraycopy(state[i],4-i,new_state[i],0,i);
			System.arraycopy(state[i],0,new_state[i],i,4-i);
		}
		
		return AES_Utils.array4xToArray(new_state);
	}
	
	public static byte[] mixColumns(byte[] state) throws DataFormatException {
		return AES_Utils.array4xToArray(AES_Utils.matrixMultiply(AES_Constants.FORWARDMIXCOL,
																 AES_Utils.arrayTo4xArray(state)));
	}
	
	public static byte[] inverseMixColumns(byte[] state) throws DataFormatException {
		return AES_Utils.array4xToArray(AES_Utils.matrixMultiply(AES_Constants.INVMIXCOL,
				 												 AES_Utils.arrayTo4xArray(state)));
	}
	
	/**
	 * Adds a round key to the supplied byte block
	 * 
	 * @param block - Current block being ciphered/invciphered
	 * @param exp_key - Expanded key (in words)
	 * @param round - Current round
	 * @return block - The current block with the round key added
	 */
	public static byte[] addRoundKey(byte[] block, int[] exp_key, int round) {
		int[] block_word = new int[4];
		
		for (int i=0;i<block_word.length;i++) {
			block_word[i] = AES_Utils.byteToWord(block[4*i],
												 block[4*i+1],
												 block[4*i+2],
												 block[4*i+3]);
			
			block_word[i] ^= exp_key[round*4+i];
			
			System.arraycopy(AES_Utils.wordToByte(block_word[i]),0,block,i*4,4);
		}
		
		return block;
	}
}
