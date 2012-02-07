package com.cyanoryx.uni.enigma.net.server;

import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

public class OpenStreamHandler implements PacketListener{
	private SessionIndex index;

	public OpenStreamHandler(SessionIndex index) {
		this.index = index;
	}

	public void notify(Packet packet){
		try {
			Session session = packet.getSession();
			session.setUser(new User(packet.getFrom()));
			session.setID(packet.getID());

			// Always default to authenticated
			String auth = packet.getAttribute("authenticated").isEmpty() ? "true" : packet.getAttribute("authenticated");

			// If no auth is requested,
			// check preferences to see if we allow that
			if (auth.equalsIgnoreCase("false")) {
				//Preferences p = new AppPrefs().getPrefs();

				// Drop packets; TODO return error
				//if (!p.getBoolean("allow_unauthenticated_conversations", false)) return; 
			}

			if (index.getSession(packet.getID())==null) {
				if (session.getStatus()==Session.CONNECTED) {
					Session s = Server.createClient(session.getSocket().getInetAddress().getHostAddress(),packet.getAttribute("return-port"),session.getLocalPort(),session.getUser(),session.getID());
					index.addSession(s);
				}
			}

			session.setStatus(Session.STREAMING);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}