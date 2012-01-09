package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;

import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;
import com.cyanoryx.uni.enigma.utils.AppPrefs;
import java.util.prefs.Preferences;

public class AuthHandler implements PacketListener {
	@Override
	public void notify(Packet packet) {
		Session s = packet.getSession();
		Preferences p = new AppPrefs().getPrefs();
		
		// Firstly, if we're setup not to do auth for this session, just ignore these packets
		if (p.getBoolean("allow_unauthenticated_conversations", false) || s.getAuthenticated()==false) return;
		
		try {
			String method = packet.getAttribute("stage");
			String type = packet.getAttribute("type");
			
			if (method.equalsIgnoreCase("agreement")) {
				// FOR: <auth stage="agreement">signed key</auth>
				if (type.equalsIgnoreCase("RSA")) {
					
				} else if (type.equalsIgnoreCase("DH")) {
					
				}
			} else if (method.equalsIgnoreCase("identification")) {
				// FOR: <auth stage="identification">certificate</auth>
				if (s.getStatus()==Session.AUTHENTICATED) return; // Ignore these packets if the stream is already authenticated
			}
			
			packet.writeXML(s.getWriter());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
