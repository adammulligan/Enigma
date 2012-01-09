package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;

import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

public class CloseStreamHandler implements PacketListener {
	@Override
	public void notify(Packet packet) {
		Session s = packet.getSession();
		
		try {
			s.getWriter().write("</stream:stream>");
			s.getWriter().flush();
			s.getWriter().close();
			
			s.getSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
