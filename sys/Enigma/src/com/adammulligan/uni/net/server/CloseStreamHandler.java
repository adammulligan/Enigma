package com.adammulligan.uni.net.server;

import java.io.IOException;

import com.adammulligan.uni.net.protocol.Session;
import com.adammulligan.uni.net.protocol.xml.Packet;
import com.adammulligan.uni.net.protocol.xml.PacketListener;

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
