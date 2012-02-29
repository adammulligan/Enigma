 package com.cyanoryx.uni.enigma.net.protocol.xml;

import java.io.IOException;

import com.cyanoryx.uni.enigma.net.protocol.Session;

/**
 * Thread to process incoming connections. Each new connection
 * creates a ProcessThread, which handles the PacketQueue for that
 * connection and sends it on to be parsed and handled. 
 * 
 * @author adammulligan
 *
 */
public class ProcessThread extends Thread {
	Session session;
	PacketQueue queue;
	
	public ProcessThread(Session s, PacketQueue q) {
		this.session = s;
		this.queue = q;
	}
	
	public void run() {
		try {
			InputHandler h = new InputHandler(this.queue);
			h.process(this.session);
		} catch (Exception e) {
			try {
				// Disconnect on incorrect XML
				session.disconnect();
			} catch (IOException ioe) {}
		}
	}
}
