package com.adammulligan.uni;

import java.io.File;

public class PublicKey extends Key {
	public PublicKey(File keyfile) {
		super(keyfile);
		this.parseKeyFile();
	}
	
	protected void parseKeyFile() {
		super.parseKeyFile();
	}
}
