package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;
import java.io.Writer;
import java.util.prefs.Preferences;

import javax.swing.text.BadLocationException;

import com.cyanoryx.uni.enigma.gui.Conversation;
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
			//	if (s.getStatus()!=Session.AUTHENTICATED) return;
			}
			
			System.out.println("-=-=-=-=-=-=-MESSAGE-=-=-=-=-=-=-=-");
			
			System.out.println(packet.getAttribute("id"));
			System.out.println(packet.getChildValue("body"));
			
			Session stored_session = index.getSession(packet.getAttribute("id"));
			System.out.println("("+stored_session.getUser().getName()+")");
			if (stored_session!=null) {
				System.out.println("Session "+stored_session.getID()+" exists..");
				Client c = stored_session.getClient(packet.getAttribute("id"));
				if (c!=null) {
					System.out.println("Got client..checking for window");
					//Conversation window = c.getWindow();
					//if (window!=null) {
						System.out.println("Found window!");
						//Conversation con = new Conversation(index.getSession(packet.getAttribute("id")).getClient(packet.getAttribute("id")));
						//con.updateMessage(stored_session.getUser().getName(),packet.getChildValue("body"));
						index.getSession(packet.getAttribute("id")).getClient(packet.getAttribute("id")).getWindow().updateMessage(stored_session.getUser().getName(),packet.getChildValue("body"));
					//} else { System.out.println("Window does not exist"); }
				}	
			}

			Writer out = packet.getSession().getWriter();
			packet.writeXML(out);

			out.flush();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
