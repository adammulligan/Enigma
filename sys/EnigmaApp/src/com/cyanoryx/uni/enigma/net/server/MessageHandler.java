package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;
import java.io.Writer;
import java.util.prefs.Preferences;

import javax.swing.text.BadLocationException;

import com.cyanoryx.uni.enigma.utils.AppPrefs;
import com.cyanoryx.uni.enigma.gui.Conversation;
import com.cyanoryx.uni.enigma.net.client.Client;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

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
			//	if (s.getStatus()!=Session.AUTHENTICATED) return;
			}
			
			System.out.println(packet.getAttribute("id"));
			System.out.println(packet.getChildValue("body"));
			
			Session stored_session = index.getSession(packet.getAttribute("id"));
			if (stored_session!=null) {
				System.out.println("Session "+stored_session.getID()+"exists..");
				Client window = stored_session.getClient(packet.getAttribute("id"));
				if (window!=null) {
					System.out.println("Client exists");
				}
				//window.updateMessage(stored_session.getUser().getName(),packet.getChildValue("body"));
			}

			Writer out = packet.getSession().getWriter();
			packet.writeXML(out);

			out.flush();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
