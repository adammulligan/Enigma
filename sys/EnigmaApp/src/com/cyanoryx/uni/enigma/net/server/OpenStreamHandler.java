package com.cyanoryx.uni.enigma.net.server;

import java.io.Writer;

import com.cyanoryx.uni.enigma.net.client.Client;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;

public class OpenStreamHandler implements PacketListener{
	private SessionIndex index;

	public OpenStreamHandler(SessionIndex index) {
		this.index = index;
	}

	public void notify(Packet packet){
		try {
			Session session = packet.getSession();
			session.setUser(new User(packet.getFrom()));
			System.out.println("Session user is now: "+packet.getFrom());
			session.setID(packet.getID());
			System.out.println("Session id is now "+packet.getID());

			System.out.println("Session ID: "+session.getID());

			// Always default to authenticated
			String auth = packet.getAttribute("authenticated").isEmpty() ? "true" : packet.getAttribute("authenticated");

			// If no auth is requested,
			// check preferences to see if we allow that
			if (auth.equalsIgnoreCase("false")) {
				//Preferences p = new AppPrefs().getPrefs();

				// Drop packets; TODO return error
				//if (!p.getBoolean("allow_unauthenticated_conversations", false)) return; 
			}

			session.setAuthenticated(auth.equalsIgnoreCase("true"));

			Writer out = session.getWriter();

			/*if (index.getSession(packet.getID())==null) {
				System.out.println("Creating client for "+session.getSocket().getInetAddress().getHostAddress()+":"+packet.getAttribute("return-port"));
				Client c = Server.createClient(session.getSocket().getInetAddress().getHostAddress(),packet.getAttribute("return-port"),session.getLocalPort(),session.getUser(),session.getID());
				System.out.println(c.getSession().getID());
				index.addSession(c.getSession());
			}*/

			/*out.write("<?xml " +
      			"version='1.0' " +
      		    "encoding='UTF-8' ?>");

      out.write("<stream " +
      			"from='xxx' " +
      			"id='x' " +
      			"xmlns='enigma:server' " +
      			"authenticated='"+session.getAuthenticated()+"'>");*/

			out.flush();

			session.setStatus(Session.STREAMING);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}