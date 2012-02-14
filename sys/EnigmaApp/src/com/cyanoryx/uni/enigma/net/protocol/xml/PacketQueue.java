package com.cyanoryx.uni.enigma.net.protocol.xml;

import java.util.LinkedList;

/**
 * A basic queue structure for packets. Packets are stored here
 * after parsing, before being handled by registered handles in
 * QueueThread. 
 * 
 * PacketQueue is thread-safe.
 * 
 * @author adammulligan
 *
 */
public class PacketQueue {
	LinkedList<Packet> queue = new LinkedList<Packet>();
	
	/**
	 * Adds a packet to the bottom of the queue.
	 * 
	 * @param packet
	 */
	public synchronized void push(Packet packet) {
		queue.add(packet);
		notifyAll();
	}
	
	/**
	 * Removes and returns the first element from the packet queue.
	 * 
	 * @return Removed packet
	 */
	public synchronized Packet pull() {
		try { while (this.queue.isEmpty()) wait(); }
		catch (InterruptedException e) { return null; }
		
		return (Packet)this.queue.remove(0);
	}
}
