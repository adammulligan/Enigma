package com.adammulligan.uni;

import java.math.BigInteger;

public class PublicKey extends Key {
	private final BigInteger y;
	
	public PublicKey(BigInteger y) {
		this.y = y;
	}
	
	public BigInteger getY() {
		return this.y;
	}
	
	public byte[] getEncoded() {
		return new byte[2];
	}
}
