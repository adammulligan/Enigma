package com.cyanoryx.uni.enigma.net.client;

import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

public class CloseStreamHandler implements PacketListener {
	public void notify(Packet packet){
		try {
			System.out.println("Disconnecting");
			packet.getSession().setStatus(Session.DISCONNECTED);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}