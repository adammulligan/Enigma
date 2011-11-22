package com.adammulligan.uni.net.protocol.xml;

import java.util.LinkedList;

public class PacketQueue {
	LinkedList<Packet> queue = new LinkedList<Packet>();
	
	public synchronized void push(Packet packet) {
		queue.add(packet);
		notifyAll();
	}
	
	public synchronized Packet pull() {
		try { while (this.queue.isEmpty()) wait(); }
		catch (InterruptedException e) { return null; }
		
		return (Packet)this.queue.remove(0);
	}
}
