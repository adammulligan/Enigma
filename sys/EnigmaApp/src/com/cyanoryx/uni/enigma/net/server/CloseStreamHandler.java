package com.cyanoryx.uni.enigma.net.server;

import javax.swing.text.BadLocationException;

import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

public class CloseStreamHandler implements PacketListener {
	private SessionIndex index;
	
	public CloseStreamHandler(SessionIndex index) {
		this.index = index;
	}

	@Override
	public void notify(Packet packet) {
		Session s = packet.getSession();
		
		try {
			index.getSession(s.getID()).getClient(s.getID()).getWindow().update("Partner disconnected...");
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
