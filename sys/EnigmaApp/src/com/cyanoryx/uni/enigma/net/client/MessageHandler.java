package com.cyanoryx.uni.enigma.net.client;

import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

public class MessageHandler implements PacketListener {
	  public void notify(Packet packet){
		System.out.println("Muahahaha");
	    String type = packet.getType() == null ? "normal" : packet.getType();
	    System.out.println("Received " + type + " message: " 
	                       + packet.getChildValue("body"));
	    System.out.println("    To: " + packet.getTo());
	    System.out.println("  From: " + packet.getFrom());
	  }
}