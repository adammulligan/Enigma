package com.cyanoryx.uni.enigma.net.client;

import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

public class OpenStreamHandler implements PacketListener{
	public void notify(Packet packet){
		Session session = packet.getSession();
		session.setID(packet.getID());
		session.setStatus(Session.STREAMING);
	}
}