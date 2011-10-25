package com.adammulligan.uni;

import java.security.MessageDigest;

public class MGF1 {
	private final MessageDigest digest;
	
	public MGF1(MessageDigest digest) {
		this.digest = digest;
	}
	
	public byte[] generateMask(byte[] mgfSeed, int maskLen) {
		int hashCount = (maskLen + this.digest.getDigestLength() - 1) / this.digest.getDigestLength();
		
		byte[] mask = new byte[0];
		
		for (int i=0;i<hashCount;i++) {
			this.digest.update(mgfSeed);
			this.digest.update(new byte[3]);
			this.digest.update((byte)i);
			byte[] hash = this.digest.digest();
			
			mask = concat(mask, hash);
		}
		
		byte[] output = new byte[maskLen];
		System.arraycopy(mask, 0, output, 0, output.length);
		return output;
	}
	
	private byte[] concat(byte[] a, byte[] b) {
		byte[] output = new byte[a.length + b.length];
		System.arraycopy(a, 0, output, 0, a.length);
		System.arraycopy(b, 0, output, a.length, b.length);
		return output;
	}
}
