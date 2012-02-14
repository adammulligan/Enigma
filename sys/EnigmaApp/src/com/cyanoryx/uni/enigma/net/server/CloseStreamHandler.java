package com.cyanoryx.uni.enigma.net.server;

import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

/**
 * Handles </stream> tags.
 * 
 * @author adammulligan
 *
 */
public class CloseStreamHandler implements PacketListener {
	private SessionIndex index;
	
	public CloseStreamHandler(SessionIndex index) {
		this.index = index;
	}

	@Override
	public void notify(Packet packet) {
		Session s = packet.getSession();
		
		try {
			Session stored_session = index.getSession(s.getID());
			stored_session.getWindow().update("Partner disconnected...");
			stored_session.closeStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
