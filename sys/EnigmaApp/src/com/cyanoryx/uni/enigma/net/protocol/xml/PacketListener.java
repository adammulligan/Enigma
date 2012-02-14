package com.cyanoryx.uni.enigma.net.protocol.xml;

/**
 * Listener interface for new packets
 * 
 * @author adammulligan
 *
 */
public interface PacketListener {
	public void notify(Packet packet);
}
