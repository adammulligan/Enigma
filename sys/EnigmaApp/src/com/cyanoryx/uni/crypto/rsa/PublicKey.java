package com.cyanoryx.uni.crypto.rsa;

import java.io.File;
import java.io.IOException;

public class PublicKey extends Key {
	public PublicKey(String key) {
		super(key);
	}
	
	public PublicKey(File keyfile) throws IOException {
		super(keyfile);
	}
}
