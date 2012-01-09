package com.cyanoryx.uni.crypto.rsa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;

import com.cyanoryx.uni.common.FileIO;

public class Key {
	protected File keyfile;
	protected BigInteger exp,N;
	
	public Key(File keyfile) {
		try {
			this.setKeyFile(keyfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setKeyFile(File filename) throws IOException {
		if (!filename.exists()) {
	      throw new IOException("Key file does not exist");
	    }
		
	    if (!filename.isFile()) {
	      throw new IllegalArgumentException("Key file should not be a directory: " + filename);
	    }
	    
	    this.keyfile = filename;
	}
	
	protected void parseKeyFile() {
		try {
			String key_string = FileIO.readFile(this.keyfile.toString());
			String[] keys     = key_string.split(",");
			
			this.N = new BigInteger(keys[0]);
			this.exp = new BigInteger(keys[1]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public BigInteger getExponent() {
		return exp;
	}
	
	public BigInteger getN() {
		return N;
	}
}
