package com.cyanoryx.uni.enigma.utils;

import java.net.MalformedURLException;
import java.net.URL;

import javax.rmi.CORBA.Util;
import javax.swing.ImageIcon;

public class Drawable {
	public static ImageIcon loadImage(String name) {
        URL imageURL;
		imageURL = Util.class.getResource("res/drawable/" + name);
		return new ImageIcon(imageURL);
    }
}
