package com.cyanoryx.uni.crypto.aes;

import java.util.zip.DataFormatException;

/**
 * Implements AES transformations
 * 
 * @author adammulligan
 *
 */
public class AES_Transformations {
	/**
	 * Converts a byte in to the corresponding S-Box value
	 * 
	 * @param o - Byte value to retrieve
	 * @return S-Box value for given byte
	 */
	public static byte getSBoxValue(byte o) {
		// Get 4 left-most and right-most bits
		int i = ((o & 0xf0) >> 4);
        int j = (o & 0x0f);

        return AES_Constants.SBOX[i][j];
	}
	
	/**
	 * Converts a byte in to the corresponding Inverse S-Box value
	 * 
	 * @param o - Byte value to retrieve
	 * @return Inverse S-Box value for given byte
	 */
	public static byte getInverseSBoxValue(byte o) {
		// Get 4 left-most and right-most bits
		int i = ((o & 0xf0) >> 4);
        int j = (o & 0x0f);

        return AES_Constants.INVSBOX[i][j];
	}
	
	/**
	 * As defined in FIPS-197:
	 * 	The SubBytes() transformation is a non-linear byte substitution that 
	 *  operates independently on each byte of the State using a substitution 
	 *  table (S-box).
	 * 
	 * @param block
	 * @return
	 */
	public static byte[][] subBytes(byte[][] block) {
		for (int i=0;i<block.length;i++) {
			for (int j=0;j<4;j++) {
				block[i][j] = AES_Transformations.getSBoxValue(block[i][j]);
			}
		}
		
		return block;
	}
	
	/**
	 * As defined in FIPS-197:
	 *  InvSubBytes() is the inverse of the byte substitution transformation, in 
	 *  which the inverse Sbox is applied to each byte of the State. This is 
	 *  obtained by applying the inverse of the affine transformation followed 
	 *  by taking the multiplicative inverse in GF(2^8)
	 * 
	 * @param block
	 * @return
	 */
	public static byte[][] invSubBytes(byte[][] block) {
		for (int i=0;i<block.length;i++) {
			for (int j=0;j<4;j++) {
				block[i][j] = AES_Transformations.getInverseSBoxValue(block[i][j]);
			}
		}
		
		return block;
	}
	
	/**
	 * As defined in FIPS-197:
	 * 	In the ShiftRows() transformation, the bytes in the last three rows of the 
	 *  State are cyclically shifted over different numbers of bytes (offsets). 
	 *  The first row, r = 0, is not shifted.
	 * 
	 * @param state
	 * @return
	 */
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
	
	/**
	 * As defined in FIPS-197:
	 *  InvShiftRows() is the inverse of the ShiftRows() transformation.  The bytes 
	 *  in the last three rows of the State are cyclically shifted over different 
	 *  numbers of bytes (offsets). The first row, r = 0, is not shifted.
	 *  
	 * @param state
	 * @return
	 */
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
				column[col] = AES_Utils.FFMul(AES_Constants.MIXCOL[col][0], state[0][i]) ^
							  AES_Utils.FFMul(AES_Constants.MIXCOL[col][1], state[1][i]) ^
							  AES_Utils.FFMul(AES_Constants.MIXCOL[col][2], state[2][i]) ^
							  AES_Utils.FFMul(AES_Constants.MIXCOL[col][3], state[3][i]);
				
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
	 * @param block - Current block being invciphered
	 * @param k - Key
	 * @param round - Current round number (0 for initial round)  
	 * @return block - The current block with the round key added
	 */
	public static byte[][] addRoundKey(byte[][] block, Key k, int r) {
		byte[][] state = new byte[4][4];
		
		byte[] exp_key = k.getExpandedKey();
		
		// Initial round starts from index 0
		// All further rounds start from 16 bits * round, i.e. 16 bytes ahead of the last round
		int index = r==0 ? 0 : (r*16); 
		
		// According to FIPS-197, number of columns should always equals 4
		for (int col=0;col<4;col++) {
			for (int row=0;row<4;row++) {
				state[row][col] = (byte)(block[row][col]^exp_key[index++]);
			}
		}
		
		return state;
	}
	
	/**
	 * Removes a round key from the supplied byte block
	 * 
	 * @param block - Current block being invciphered
	 * @param k - Key
	 * @param round - Current round number (Nr+1 for initial round)  
	 * @return block - The current block with the round key added
	 */
	public static byte[][] inverseAddRoundKey(byte[][] block, Key k, int r) {
		byte[][] state = new byte[4][4];
		
		byte[] exp_key = k.getExpandedKey();
		
		// Start from end of the array and work backwards in 16 byte increments
		int index = r==k.getKeySize().getNumberOfRounds()+1 ? 16*(k.getKeySize().getNumberOfRounds()+1) : (r*16);
		
		// According to FIPS-197, number of columns should always equals 4
		for (int col=3;col>=0;col--) {
			for (int row=3;row>=0;row--) {
				state[row][col] = (byte)(block[row][col]^exp_key[--index]);
			}
		}
		
		return state;
	}
}
