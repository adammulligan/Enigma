/**
 * 
 */
package com.cyanoryx.uni.enigma.utils;

import java.util.prefs.Preferences;

/**
 * Creates a preferences object using this class name as the node.
 * Allows us to refer to the same preference across multiple objects.
 * 
 * @author adammulligan
 *
 */
public class AppPrefs {
	/**
	 * Returns a Preferences object for this application
	 * 
	 * @return
	 */
	public Preferences getPrefs() {
		return Preferences.userNodeForPackage(getClass());
	}
	
	/**
	 * Returns an array of the last 10 connections as IP addresses
	 * 
	 * @return
	 */
	public String[] getLastConnections() {
		String   ips 		 = this.getPrefs().get("last_connections", "");
		String[] connections = new String[10];
		
		String separator = ";";
		
		connections = ips.split(separator);
		
		return connections;
	}
}
