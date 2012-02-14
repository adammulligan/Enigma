package com.cyanoryx.uni.enigma.utils;

import javax.swing.ImageIcon;

/**
 * Basic implementation of asset management, similar to drawables used
 * in Android SDK development.
 * 
 * @author adammulligan
 *
 */
public class Drawable {
	public static final String DRAWABLE_DIR="res/drawable/";
	
	/**
	 * Returns an ImageIcon for the given resource, stored in
	 * DRAWABLE_DIR
	 * 
	 * @param name
	 * @return
	 */
	public static ImageIcon loadImage(String name) {
		return new ImageIcon(DRAWABLE_DIR + name);
    }
}
