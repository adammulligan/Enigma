package com.adammulligan.uni;

import java.math.BigInteger;

/*
	Cryptix General License
	
	Copyright (c) 1995-2005 The Cryptix Foundation Limited.
	All rights reserved.
	
	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions are
	met:
	
	  1. Redistributions of source code must retain the copyright notice,
	     this list of conditions and the following disclaimer.
	  2. Redistributions in binary form must reproduce the above copyright
	     notice, this list of conditions and the following disclaimer in
	     the documentation and/or other materials provided with the
	     distribution.
	
	THIS SOFTWARE IS PROVIDED BY THE CRYPTIX FOUNDATION LIMITED AND
	CONTRIBUTORS ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
	INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
	IN NO EVENT SHALL THE CRYPTIX FOUNDATION LIMITED OR CONTRIBUTORS BE
	LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
	CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
	SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
	BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
	WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
	OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
	IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Byte utilities - partially based on cryptix.jce.provider.util;
 * http://www.docjar.com/html/api/cryptix/jce/provider/util/Util.java.html
 * 
 * According with the Cryptix Ltd. General License, the following code can be used
 * freely without limitation. Please see above, or visit http://www.cryptix.org/LICENSE.TXT
 * 
 * @author adammulligan/Cryptix
 */
public final class Bytes {
	
	/**
	 * xor's two arrays of bytes together, item by item
	 * 
	 * @param a
	 * @param b
	 * @return Array of bytes where Output[i] = a[i] xor b[i]
	 */
	public static final byte[] xor(byte[] a, byte[] b) {
		if(a.length != b.length) {
			throw new InternalError("Byte a must equal Byte b");
		}
		
		// Create a temporary array to store results in
		byte[] output = new byte[a.length];
		
		// Loop through the array and xor each bit
		for(int i=0; i<output.length; i++) {
			output[i] = (byte)(a[i] ^ b[i]);
		}
		
		return output;
	}  
	
	/**
	 * Concatenates two byte arrays through arraycopy
	 * 
	 * @param a
	 * @param b
	 * @return An array of bytes length (a.length+b.length) consisting of A||B
	 */
	public static final byte[] concat(byte[] a, byte[] b) {
		byte[] output = new byte[a.length+b.length];
		
		System.arraycopy(a, 0, output, 0, a.length);
		System.arraycopy(b, 0, output, a.length, b.length);
		return output;
	}
	
	/**
	 * Concatenates three byte arrays through arraycopy
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return An array of bytes length (a.length+b.length+c.length) consisting of a||b|||c
	 */
	public static final byte[] concat(byte[] a, byte[] b, byte[] c) {
		return concat(a, concat(b, c));   
	} 
 
	/**
	 * Concatenates four byte arrays through arraycopy
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return An array of bytes length (a.length+b.length+c.length+d.length) consisting of a||b||c||d
	 */
	public static final byte[] concat(byte[] a, byte[] b, byte[] c, byte[] d) {
		return concat(a, concat(b, concat(c, d)));   
	}
	
	/**
	 * Taken from http://www.docjar.com/html/api/cryptix/jce/provider/util/Util.java.html
	 * 
	 * @param x
	 * @param resultByteLen
	 * @return
	 */
	public static final byte[] toFixedLenByteArray(BigInteger x, int resultByteLen) {
	    if (x.signum() != 1)
	        throw new IllegalArgumentException("BigInteger not positive.");

	    byte[] x_bytes = x.toByteArray();
	    int x_len = x_bytes.length;

	    if (x_len <= 0)
	        throw new IllegalArgumentException("BigInteger too small.");

	    /*
	     * The BigInteger contract specifies that we now have at most one
	     * superfluous leading zero byte:
	     */
	    int x_off = (x_bytes[0] == 0) ? 1 : 0;
	    x_len -= x_off;

	    /*
	     * Check whether the BigInteger will fit in the requested byte length.
	     */
	    if ( x_len > resultByteLen)
	        throw new IllegalArgumentException("BigInteger too large.");

	    /*
	     * Now stretch or shrink the encoding to fit in resByteLen bytes.
	     */
	    byte[] res_bytes = new byte[resultByteLen];
	    int res_off = resultByteLen-x_len;
	    System.arraycopy(x_bytes, x_off, res_bytes, res_off, x_len);
	    return res_bytes;
	}
	
	/**
	 * Taken from http://www.docjar.com/html/api/cryptix/jce/provider/util/Util.java.html
	 * 
	 * Checks to see if two byte arrays are equal.
	 * Note: two null arrays are equal.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static final boolean equals(byte[] a, byte[] b) {
		if( a==null && b==null ) return true;
		
		if( a==null ^ b==null ) return false;
		
		int aLen = a.length;
		int bLen = b.length;
		if( aLen != bLen ) return false;
		
		for(int i=0; i<aLen; i++)
			if( a[i] != b[i] ) return false;
		
		return true;
	}
	
	public static final String toString(byte[] a) { return ""; }
}
