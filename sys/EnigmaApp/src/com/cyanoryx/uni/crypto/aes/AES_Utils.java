package com.cyanoryx.uni.crypto.aes;

import java.io.IOException;
import java.util.zip.DataFormatException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Utilities for AES implementations
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
	 * Takes a linear array of size 16 and converts it into a 4x4 state array
	 * As defined in FIPS197
	 * 
	 * Idea of linearising/squaring arrays taken from watne.seis720.project.AES_Utilities
	 * Watne uses array indexes and some unpleasant looking maths, so I've rewritten it
	 * to use arraycopy instead, which is much more efficient.
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
	 * Idea of linearising/squaring arrays taken from watne.seis720.project.AES_Utilities
	 * Watne uses array indexes and some unpleasant looking maths, so I've rewritten it
	 * to use arraycopy instead, which is much more efficient.
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
	public static byte[] pad(byte[] bytes, KeySize k) throws DataFormatException {
		// PKCS#5 Padding
		// See RFC 2898
		
		// Figure out how many bytes to append by working out:
		//   Block Size - (bytes length mod block size) 
		byte[] new_plaintext = new byte[bytes.length + (16-(bytes.length%16))];
		System.arraycopy(bytes, 0, new_plaintext, 0, bytes.length);
		
		// As defined in PKCS#5
		// Each of the padding bytes are set to the value of the number of padding bytes
		// rather than the usual 0, so that when we unpad we can figure out how many
		// bytes to remove
		for (int i=bytes.length;i<new_plaintext.length;i++) new_plaintext[i] = (byte)(16-(bytes.length%16));
		
		return new_plaintext;
	}
	
	/**
	 * Finite Field multiplication of two bytes over the field GF(2^8)
	 * 
	 * TODO comment this
	 * 
	 * @param n
	 * @param o
	 * @return
	 */
	public static byte FFMul(byte a, byte b) {
		byte aa = a, bb = b, r = 0, t;
		
		while (aa != 0) {
			if ((aa & 1) != 0) r = (byte)(r ^ bb);
			
			t = (byte)(bb & 0x80);
			bb = (byte)(bb << 1);
			
			if (t != 0) bb = (byte)(bb ^ 0x1b);
			
			aa = (byte)((aa & 0xff) >> 1);
		}
	      
	    return r;
	}
}
