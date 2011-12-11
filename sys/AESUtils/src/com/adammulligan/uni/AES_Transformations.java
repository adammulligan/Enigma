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
	
	public static byte[][] subBytes(byte[][] block) {
		for (int i=0;i<block.length;i++) {
			for (int j=0;j<4;j++) {
				block[i][j] = AES_Transformations.getSBoxValue(block[i][j]);
			}
		}
		
		return block;
	}
	
	public static byte[] invSubBytes(byte[] block) {
		for (int i=0;i<block.length;i++) {
			block[i] = AES_Transformations.getInverseSBoxValue(block[i]);
		}
		
		return block;
	}
	
	public static byte[][] shiftRows(byte[][] state) {
		byte[][] new_state = new byte[4][4];
		
		// Keep first row
		new_state[0] = state[0];
		
		for (int i=1;i<=3;i++) {
			int offset=i;
			
			System.arraycopy(state[i],offset,new_state[i],0,4-i);
			System.arraycopy(state[i],0,new_state[i],4-i,i);
		}
		
		return new_state;
	}
	
	public static byte[][] invShiftRows(byte[][] state) {
		byte[][] new_state = new byte[4][4];
		
		// Keep first row
		new_state[0] = state[0];
		
		for (int i=1;i<=3;i++) {
			System.arraycopy(state[i],4-i,new_state[i],0,i);
			System.arraycopy(state[i],0,new_state[i],i,4-i);
		}
		
		return new_state;
	}
	
	/**
	 * Takes a state block and multiplies each column with the mix column matrice
	 * (as defined in FIPS-197)
	 * 
	 * @param state
	 * @return
	 * @throws DataFormatException
	 */
	public static byte[][] mixColumns(byte[][] state) {
		byte[][] tmp_state = new byte[4][4];
		
		// Takes each of the four columns in the state
		for (int i=0;i<4;i++) {
			int[] column = new int[4];
			
			// And XOR each row value with the matching value in the mix tables 
			for (int col=0;col<4;col++) {
				column[col] = AES_Utils.FFMul(AES_Constants.FORWARDMIXCOL[col][0], state[0][i]) ^
							  AES_Utils.FFMul(AES_Constants.FORWARDMIXCOL[col][1], state[1][i]) ^
							  AES_Utils.FFMul(AES_Constants.FORWARDMIXCOL[col][2], state[2][i]) ^
							  AES_Utils.FFMul(AES_Constants.FORWARDMIXCOL[col][3], state[3][i]);
				
			}
			
			// Fill the temporary state to be returned with each column
			for (int j=0;j<4;j++) {
				tmp_state[j][i] = (byte)column[j];
			}
		}
		
		return tmp_state;
	}
	
	/**
	 * Takes a state block and multiplies each column with the matching value in the
	 * inverse mix table
	 * 
	 * @param state
	 * @return
	 */
	public static byte[][] inverseMixColumns(byte[][] state) {
		byte[][] tmp_state = new byte[4][4];
		
		// Takes each of the four columns in the state
		for (int i=0;i<4;i++) {
			int[] column = new int[4];
			
			// And XOR each row value with the matching value in the mix tables 
			for (int col=0;col<4;col++) {
				column[col] = AES_Utils.FFMul(AES_Constants.INVMIXCOL[col][0], state[0][i]) ^
							  AES_Utils.FFMul(AES_Constants.INVMIXCOL[col][1], state[1][i]) ^
							  AES_Utils.FFMul(AES_Constants.INVMIXCOL[col][2], state[2][i]) ^
							  AES_Utils.FFMul(AES_Constants.INVMIXCOL[col][3], state[3][i]);
				
			}
			
			// Fill the temporary state to be returned with each column
			for (int j=0;j<4;j++) {
				tmp_state[j][i] = (byte)column[j];
			}
		}
		
		return tmp_state;
	}
	
	/**
	 * Adds a round key to the supplied byte block
	 * 
	 * TODO: w = exp key, wcount = round_bytes
	 * 
	 * @param block - Current block being ciphered/invciphered
	 * @param exp_key - Expanded key
	 * @param round - Current byte number for given round (should be 4*round)
	 * @return block - The current block with the round key added
	 */
	public static byte[][] addRoundKey(byte[][] block, byte[] exp_key, int round_byte_count) {
		byte[][] state = new byte[4][4];
		
		// According to FIPS-197, number of columns should always equals 4
		for (int col=0;col<4;col++) {
			for (int row=0;row<4;row++) {
				state[row][col] = (byte)(block[row][col]^exp_key[round_byte_count]);
			}
		}
		
		return state;
	}
}
