package com.cyanoryx.uni.crypto.rsa;

import java.io.File;
import java.math.BigInteger;

public class PrivateKey extends Key {
	private BigInteger p,q;
	
	public PrivateKey(File keyfile) {
		super(keyfile);
		this.parseKeyFile();
	}
	
	protected void parseKeyFile() {
		super.parseKeyFile();
		
		// Other stuff...
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
