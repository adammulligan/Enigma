package com.cyanoryx.uni.crypto.rsa;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

public class PrivateKey extends Key {
	private BigInteger p,q;
	
	public PrivateKey(String key) {
		super(key);
	}
	
	public PrivateKey(File keyfile) throws IOException {
		super(keyfile);
	}
	
	public BigInteger getExponent() {
		return super.getExponent();
	}
	
	public BigInteger getN() {
		return super.getN();
	}
	
	public BigInteger getP() {
		return this.p;
	}
	
	public BigInteger getQ() {
		return q;
	}
}
