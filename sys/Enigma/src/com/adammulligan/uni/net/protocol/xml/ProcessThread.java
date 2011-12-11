package com.adammulligan.uni.net.protocol.xml;

import com.adammulligan.uni.net.protocol.Session;

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
				// TODO: send a proper message saying the input was malformed. E.g. sending <message> after </stream>
				session.getWriter().write("Malformed XML received. Disconnecting you. \n");
				session.getWriter().flush();
				session.disconnect();
			} catch (Exception e2) {}
		}
	}
}
