package com.adammulligan.uni;

import java.math.BigInteger;

public class PublicKey extends Key {
	private final BigInteger y,g,p;
	
	public PublicKey(BigInteger y, BigInteger g, BigInteger p) {
		this.y = y;
		this.g = g;
		this.p = p;
	}
	
	public BigInteger getY() {
		return this.y;
	}
	
	public BigInteger getG() {
		return this.g;
	}
	
	public BigInteger getP() {
		return this.p;
	}
	
	public byte[] getEncoded() {
		return new byte[2];
	}
}
