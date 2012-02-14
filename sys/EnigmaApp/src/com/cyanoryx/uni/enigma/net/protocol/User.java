package com.cyanoryx.uni.enigma.net.protocol;

/**
 * Simple struct for storing user information.
 * Can be used in the future to store further details, e.g.
 * profile photos.
 * 
 * @author adammulligan
 *
 */
public class User {
	private String name;
	
	public User(String name) {
		this.name = name;
	}
	
	public String getName() { return this.name; }
	public void   setName(String name) { this.name = name; }
	
	@Override
	public String toString() {
		return this.getName();
	}
}
