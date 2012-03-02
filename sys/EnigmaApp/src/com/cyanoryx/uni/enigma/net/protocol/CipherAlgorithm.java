package com.cyanoryx.uni.enigma.net.protocol;

public enum CipherAlgorithm {
	AES;
	
	public static CipherAlgorithm searchByID(String id) {
		if (id.equalsIgnoreCase("aes")) {
			return CipherAlgorithm.AES;
		}
		
		return CipherAlgorithm.AES;
	}
}
