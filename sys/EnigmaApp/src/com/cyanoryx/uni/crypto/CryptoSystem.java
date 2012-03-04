package com.cyanoryx.uni.crypto;

public interface CryptoSystem {
	public byte[] encrypt(byte[] data);
	public byte[] decrypt(byte[] data);
}
