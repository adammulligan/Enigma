package com.adammulligan.uni;

import java.math.BigInteger;

public class PrivateKey extends Key {
	private final BigInteger x;
	
	public PrivateKey(BigInteger x) {
		this.x = x;
	}
	
	public BigInteger getX() {
		return this.x;
	}
	
	public byte[] getEncoded() {
		return new byte[2];
	}
}
