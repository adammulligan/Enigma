package com.cyanoryx.uni.crypto.aes;

/**
 * AES key size struct for the AES interface.
 * 
 * Based on requirements in FIPS-197, and idea from watne.seis720.project
 * 
 * @author adammulligan
 *
 */
public enum KeySize {
	K128(128, 10, 176),K192(192,12,208),K256(256,14,240);
	
	private int blockSize,
				roundKeySize,
				keySize,
				nRounds,
				expKeyBytes;
	
	private KeySize(int keySize,int nRounds,int expKeyBytes) {
		this.blockSize = roundKeySize = 128;
		this.nRounds = nRounds;
		this.keySize = keySize;
		this.expKeyBytes = expKeyBytes;
	}
	
	public int getBlockSizeBits() { return this.blockSize; }
	public int getBlockSizeBytes() { return this.blockSize/8; }
	public int getBlockSizeWords() { return this.blockSize/32; }
	
	public int getExpandedKeyBytes() { return this.expKeyBytes; }
	public int getExpandedKeyWords() { return this.expKeyBytes/4; }
	
	public int getKeySizeBits() { return this.keySize; }
	public int getKeySizeBytes() { return this.keySize/8; }
	public int getKeySizeWords() { return this.keySize/32; }
	
	public int getRoundKeyBits() { return this.roundKeySize; }
	public int getRoundKeyBytes() { return this.roundKeySize/8; }
	public int getRoundKeyWords() { return this.roundKeySize/32; }
	
	public int getNumberOfRounds() { return this.nRounds; }
}
