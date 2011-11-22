package com.adammulligan.uni.net.server;

import java.io.IOException;
import java.io.Writer;

import com.adammulligan.uni.net.protocol.xml.Packet;
import com.adammulligan.uni.net.protocol.xml.PacketListener;

public class MessageHandler implements PacketListener {
	public void notify(Packet packet){
	    try {
	    	Writer out = packet.getSession().getWriter();
			packet.writeXML(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
}
