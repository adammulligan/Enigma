package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;
import java.io.Writer;
import java.util.prefs.Preferences;

import com.cyanoryx.uni.enigma.utils.AppPrefs;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

public class MessageHandler implements PacketListener {
	public void notify(Packet packet){
		Session s = packet.getSession();
		Preferences p = new AppPrefs().getPrefs();
		
	    try {
	    	// Ignore packets if this session is not authenticated
	    	if (!p.getBoolean("allow_unauthenticated_conversations", false) || s.getAuthenticated()) { // If it's an authentication-only session
	    		if (s.getStatus()!=Session.AUTHENTICATED) return;
	    	}
	    	
	    	String cipher = packet.getAttribute("cipher");
	    	if (cipher.equalsIgnoreCase("twofish")) {
	    		
	    	} else if (cipher.equalsIgnoreCase("aes")) {
	    		
	    	}
	    	
	    	Writer out = packet.getSession().getWriter();
			packet.writeXML(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
}
