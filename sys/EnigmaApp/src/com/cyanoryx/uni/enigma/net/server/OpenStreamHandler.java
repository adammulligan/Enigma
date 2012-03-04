package com.cyanoryx.uni.enigma.net.server;

import java.util.prefs.Preferences;

import com.cyanoryx.uni.crypto.aes.Key;
import com.cyanoryx.uni.crypto.aes.KeySize;
import com.cyanoryx.uni.enigma.net.protocol.CipherAlgorithm;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;
import com.cyanoryx.uni.enigma.utils.AppPrefs;

/**
 * Handles <stream> tags.
 * 
 * @author adammulligan
 *
 */
public class OpenStreamHandler implements PacketListener{
	private SessionIndex index;

	public OpenStreamHandler(SessionIndex index) {
		this.index = index;
	}

	public void notify(Packet packet){
		try {
			Session session = packet.getSession();
			session.setUser(new User(packet.getFrom()));
			session.setID(packet.getID());
			
			Preferences p = new AppPrefs().getPrefs();

			// If there's no auth attribute, default to authenticated,
			// if there is, use the value sent
			// Always defaults to authenticated.
			boolean auth = packet.getAttribute("authenticated").isEmpty() ? true : (packet.getAttribute("authenticated").equalsIgnoreCase("true")) ? true : false;

			// If no auth is requested,
			// check preferences to see if we allow that
			if (!auth && !p.getBoolean("allow_unauthenticated_conversations", false)) return; 

			if (index.getSession(packet.getID())==null) {
				if (session.getStatus()==Session.CONNECTED) {
					Session s = Server.createClient(session.getSocket().getInetAddress().getHostAddress(),packet.getAttribute("return-port"),session.getLocalPort(),session.getUser(),session.getID());
					s.setAuthenticated(auth);
					s.setCipherType(CipherAlgorithm.AES);
					s.setCipherKey(new Key(KeySize.K256).getKey());
					index.addSession(s);
				}
			}

			session.setStatus(Session.STREAMING);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
}