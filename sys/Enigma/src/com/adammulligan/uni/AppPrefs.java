/**
 * 
 */
package com.adammulligan.uni;

import java.util.prefs.Preferences;

/**
 * Creates a preferences object using this class name as the node.
 * Allows us to refer to the same preference across multiple objects.
 * 
 * @author adammulligan
 *
 */
public class AppPrefs {
	public Preferences getPrefs() {
		return Preferences.userNodeForPackage(getClass());
	}
}
