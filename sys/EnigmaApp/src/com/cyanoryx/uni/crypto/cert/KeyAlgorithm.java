package com.cyanoryx.uni.crypto.cert;

public enum KeyAlgorithm {
	RSA,DH; 
	
	public static KeyAlgorithm searchByID(String id) {
		if (id.equalsIgnoreCase("rsa")) {
			return KeyAlgorithm.RSA;
		} else if (id.equalsIgnoreCase("dh")) {
			return KeyAlgorithm.DH;
		}
		
		return KeyAlgorithm.RSA;
	}
}
