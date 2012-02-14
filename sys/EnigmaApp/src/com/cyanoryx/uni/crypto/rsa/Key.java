package com.cyanoryx.uni.crypto.rsa;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import com.cyanoryx.uni.common.FileIO;

public class Key {
	protected String key;
	protected BigInteger exp,N;

	public Key(String key) {
		this.key = key;
		parseKey(key);
	}

	public Key(File keyfile) throws IOException {
		this(readKeyFile(keyfile));
	}

	private static String readKeyFile(File keyfile) throws IOException {
		if (!keyfile.exists()) {
			throw new IOException("Key file does not exist");
		}

		if (!keyfile.isFile()) {
			throw new IllegalArgumentException("Key file should not be a directory: " + keyfile);
		}

		return FileIO.readFile(keyfile.toString());
	}

	protected void parseKey(String key) {
		String[] keys = key.split(",");

		this.N = new BigInteger(keys[0]);
		this.exp = new BigInteger(keys[1]);
	}

	public BigInteger getExponent() {
		return exp;
	}

	public BigInteger getN() {
		return N;
	}
	
	public String getKey() {
		return this.key;
	}
}
