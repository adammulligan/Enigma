package com.adammulligan.uni;

import java.io.IOException;
import java.math.BigInteger;
import java.util.zip.DataFormatException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Utilities for AES implementations
 * 
 * Effectively an improved and updated version of watne.seis720.project.AES_Utilities 
 * 
 * @author adammulligan
 *
 */
@SuppressWarnings("restriction")
public class AES_Utils {
	/**
	 * Converts a byte array to a Base64 String
	 * 
	 * @param bytes - Array of bytes
	 * @return Base64 String
	 */
	public static String byteToBase64(byte[] bytes) {
		BASE64Encoder enc = new BASE64Encoder();
		return enc.encode(bytes);
	}
	
	/**
	 * Converts a Base64 encoded string to a byte array
	 * 
	 * @param b64m - Base64 encoded String
	 * @return Array of bytes
	 * @throws IOException
	 */
	public static byte[] base64ToByte(String b64m) throws IOException {
		BASE64Decoder dec = new BASE64Decoder();
		return dec.decodeBuffer(b64m);
	}
	
	/**
	 * Converts a byte array into a hexadecimal string
	 * 
	 * @param bytes - Array of bytes
	 * @return hex - Hexadecimal string value for each byte cocatenated into a string
	 */
	public static String byteToHex(byte[] bytes) {
		BigInteger bInt = new BigInteger(bytes);
		String hex = bInt.toString(16);
		
		// Pad with a 0 if we get a non-even number of characters
		return (hex.length()%2!=0) ? "0"+hex : hex;
	}
	
	/**
	 * Converts a hexadecimal string into a byte array
	 * 
	 * @param hex - Hexadecimal string
	 * @return array - byte array
	 */
	public static byte[] hexToByte(String hex) {
		return new BigInteger(hex, 16).toByteArray();
	}
	
	/**
	 * Create an int word from 4 given bytes
	 * 
	 * @param b1
	 * @param b2
	 * @param b3
	 * @param b4
	 * @return word - word version of the 4 given bytes cocatenated togeter
	 */
	public static int byteToWord(byte b1, byte b2, byte b3, byte b4) {
		return (0xff000000 & ((0xff & b1) << 24)) | 
			   (0x00ff0000 & ((0xff & b2) << 16)) |
			   (0x0000ff00 & ((0xff & b3) << 8)) | 
			   (0x000000ff & b4);
	}
	
	/**
	 * Takes a int word, and returns a byte array of the bytes inside
	 * 
	 * @param w - integer word
	 * @return byte array
	 */
	public static byte[] wordToByte(int w) {
		// Do the exact opposite of byteToWord()
		return new byte[]{(byte)((w & 0xff000000) >> 24),
				  		  (byte)((w & 0xff0000) >> 16),
						  (byte)((w & 0xff00) >> 8),
						  (byte)(w & 0xff)};
	}
	
	/**
	 * Takes a linear array of size 16 and converts it into a 4x4 state array
	 * As defined in FIPS197
	 * 
	 * @param array - Linear byte array of length 16
	 * @return array - 4x4 2D byte array
	 * @throws DataFormatException
	 */
	public static byte[][] arrayTo4xArray(byte[] array) throws DataFormatException {
		if (array.length!=16) throw new DataFormatException("Array must be of length 16");
		
		byte[][] array4x = new byte[4][4];
	
		for (int i=0;i<4;i++) {
			System.arraycopy(array,(i*4),array4x[i],0,4);
		}
		
		return array4x;
	}
	
	/**
	 * Converts a 4x4 2D byte array to a cocatenated linear byte array
	 * As defined in FIPS197
	 * 
	 * @param array - 4x4 2D byte array
	 * @return array - linearised byte array
	 */
	public static byte[] array4xToArray(byte[][] array) {
		byte[] array1x = new byte[16];
		
		for (int i=0;i<4;i++) {
			System.arraycopy(array[i],0,array1x,(i*4),4);
		}
		
		return array1x;
	}
	
	/**
	 * Pads an array of bytes inline with the given padding scheme
	 * Currently only supports PKCS5
	 * 
	 * @param bytes - Array of bytes to pad
	 * @param k - KeySize
	 * @param p - Padding type
	 * @return bytes - Padding array of bytes
	 * @throws DataFormatException
	 */
	public static byte[] pad(byte[] bytes, KeySize k, Padding p) throws DataFormatException {
		byte[] result;
		
		// PKCS#5 Padding
		// See RFC 2898
		if (p == Padding.PKCS5PADDING) {
			// Figure out how many bytes to append by working out:
			//   KeyLength - (Array mod KeyLength)
			int paddingLength = k.getBlockSizeBytes() - (bytes.length % k.getBlockSizeBytes());
			
			result = new byte[bytes.length + paddingLength];
			System.arraycopy(bytes,0,result,0,bytes.length);
			
			// As defined in PKCS#5
			// Each of the padding bytes are set to the value of the number of padding bytes
			// rather than the usual 0, so that when we unpad we can figure out how many
			// bytes to remove
			for (int i=0;i<paddingLength;i++)
				result[i+bytes.length] = (byte)paddingLength;
		} else if (p == Padding.NONE) {
			result = bytes;
		} else {
			throw new DataFormatException("Invalid padding type");
		}
		
		return result;
	}
	
	/**
	 * Removes padding from an array of bytes, given the paddign scheme
	 * Currently only supports PKCS#5
	 * 
	 * @param bytes - Padded array of bytes to remove padding from
	 * @param p - Padding type
	 * @return bytes - Bytes array minus padding
	 * @throws DataFormatException
	 */
	public static byte[] unpad(byte[] bytes, Padding p) throws DataFormatException {

		byte[] result = new byte[]{1};
		
		if (p == Padding.PKCS5PADDING) {
			// As we set the padding bytes to be the number of padding bytes,
			// we can calculate how many to remove
			int paddingLength = bytes[bytes.length-1];
			
			if (paddingLength < 1 || paddingLength > bytes.length) throw new DataFormatException("Padding byte length error");
			
			result = new byte[bytes.length - paddingLength];
			System.arraycopy(bytes,0,result,0,result.length);
		} else if (p == Padding.NONE) {
			result = bytes;
		} else {
			throw new DataFormatException("Invalid padding type");
		}
		
		return result;
	}
	
	/**
	 * Multiply two matrices, though not in the traditional manner
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static byte[][] matrixMultiply(byte[][] a, byte[][] b) {
		if ((a.length!=b.length) || (a[0].length!=b[0].length)) throw new ArithmeticException("Matrix dimensions must be equal");
		
		int h = a.length,w = a[0].length;
		byte[][] output = new byte[h][w];
		
		for (int i=0;i<h;i++) {
			for (int j=0;j<w;j++) {
				output[i][j]=0;
				
				for (int k=0;k<h;k++) {
					output[i][j] ^= AES_Utils.gfProduct(a[i][k], b[i][k]);
				}
			}
		}
		
		return output;
	}
	
	/**
	 * Sources:
	 * 	watne.seis720.project
	 *  http://www.partow.net/projects/galois/#Download
	 * 
	 * @param n
	 * @param o
	 * @return
	 */
	public static byte gfProduct(byte n, byte o) {
		/*
		 * Convert the given bytes into integers to make calculations easier
		 * Also and with 0xff for positive values
		 */
		int ni = n & 0xff;
		int oi = o & 0xff;
		
		int result = 0;
		for (int i=0,j=1;i<8;i++,j*=2) {
			if ((ni & j & 0xff) > 0) {
                result ^= (oi << i);
            }
		}
		
		while (result > 0xff) {
			int divisor = AES_Constants.GF_POLYNOMIAL;
			int m = 0x100;
			
			while ((result/m)>1) {
				divisor <<= 1;
				m <<= 1;
			}
			
			result ^= divisor;
		}
		
		return (byte)result;
	}
}
