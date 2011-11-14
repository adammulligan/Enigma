package com.adammulligan.uni;

public class KeyAgreement {
	private Key pub_key,priv_key;
	
	public KeyAgreement(PublicKey pub_key, PrivateKey priv_key) {
		this.pub_key  = pub_key;
		this.priv_key = priv_key; 
	}
}
