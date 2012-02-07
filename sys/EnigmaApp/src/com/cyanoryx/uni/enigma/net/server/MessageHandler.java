package com.cyanoryx.uni.enigma.net.server;

import java.util.prefs.Preferences;

import javax.swing.text.BadLocationException;

import com.cyanoryx.uni.enigma.net.client.Client;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;
import com.cyanoryx.uni.enigma.utils.AppPrefs;

public class MessageHandler implements PacketListener {
	private SessionIndex index;
	
	public MessageHandler(SessionIndex index) {
		this.index = index;
	}

	public void notify(Packet packet){
		Session s = packet.getSession();
		Preferences p = new AppPrefs().getPrefs();

		try {
			// Ignore packets if this session is not authenticated
			if (!p.getBoolean("allow_unauthenticated_conversations", false) || s.getAuthenticated()) { // If it's an authentication-only session
//				if (s.getStatus()!=Session.AUTHENTICATED) return;
			}
			
			Session stored_session = index.getSession(packet.getAttribute("id"));
			if (stored_session!=null) {
				Client c = stored_session.getClient(packet.getAttribute("id"));
				if (c!=null && c.getWindow()!=null) {
					c.getWindow().updateMessage(stored_session.getUser().getName(),packet.getChildValue("body"));
				}	
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
