package com.adammulligan.uni;

public enum KeySize {
	K128(128),K192(192),K256(256);
	
	private int blockSize,
				roundKeySize,
				keySize,
				nRounds;
	
	private KeySize(int keySize) {
		this.blockSize = roundKeySize = 128;
		this.nRounds = 16;
		this.keySize = keySize;
	}
	
	public int getBlockSizeBits() { return this.blockSize; }
	public int getBlockSizeBytes() { return this.blockSize/8; }
	public int getBlockSizeWords() { return this.blockSize/32; }
	
	public int getKeySizeBits() { return this.keySize; }
	public int getKeySizeBytes() { return this.keySize/8; }
	public int getKeySizeWords() { return this.keySize/32; }
	
	public int getRoundKeyBits() { return this.roundKeySize; }
	public int getRoundKeyBytes() { return this.roundKeySize/8; }
	public int getRoundKeyWords() { return this.roundKeySize/32; }
	
	public int getNumberOfRounds() { return this.nRounds; }
}
