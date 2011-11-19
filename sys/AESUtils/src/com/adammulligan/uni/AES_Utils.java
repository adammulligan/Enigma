/**
 * 
 */
package com.adammulligan.uni;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Utilities for AES implementations
 * 
 * @author adammulligan
 *
 */
public class AES_Utils {
	public static String byteToBase64(byte[] bytes) {
		BASE64Encoder enc = new BASE64Encoder();
		return enc.encode(bytes);
	}
	
	public static byte[] base64ToByte(String b64m) throws IOException {
		BASE64Decoder dec = new BASE64Decoder();
		return dec.decodeBuffer(b64m);
	}
	
	public static String byteToHex(byte[] bytes) {
		BigInteger bInt = new BigInteger(bytes);
		String hex = bInt.toString(16);
		
		// Pad with a 0 if we get a non-even number of characters
		return (hex.length()%2!=0) ? "0"+hex : hex;
	}
	
	public static byte[] hexToByte(String hex) {
		return new BigInteger(hex, 16).toByteArray();
	}
	
	public static int byteToWord(byte b1, byte b2, byte b3, byte b4) {
		return 1;
	}
	public static byte[] wordToByte(int w) { return new byte[]{}; }
	
	public static byte[][] arrayTo4xArray(byte[] array) { return new byte[][]{}; }
	public static byte[] array4xToArray(byte[][] array) { return new byte[]{}; }
	
	public static int rotateLeft(int n, int bits) { return 1; }
	
	public static byte[] pad(byte[] bytes, Padding p) { return new byte[]{}; }
	public static byte[] unpad(byte[] bytes, Padding p) { return new byte[]{}; }
	
	public static long getChecksum(File file) throws IOException { return 1L; }
}
