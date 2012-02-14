package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;
import java.util.prefs.Preferences;
import java.util.zip.DataFormatException;

import javax.swing.text.BadLocationException;

import com.cyanoryx.uni.common.Base64;
import com.cyanoryx.uni.crypto.aes.AES;
import com.cyanoryx.uni.crypto.aes.Key;
import com.cyanoryx.uni.crypto.aes.KeySize;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;
import com.cyanoryx.uni.enigma.utils.AppPrefs;

/**
 * Handles incoming <message> tags.
 * 
 * @author adammulligan
 *
 */
public class MessageHandler implements PacketListener {
	private SessionIndex index;
	
	public MessageHandler(SessionIndex index) {
		this.index = index;
	}

	public void notify(Packet packet){
		Preferences p = new AppPrefs().getPrefs();

		try {
			// If a session exists where SessionID==AttrID, update windows, etc.
			Session ss = index.getSession(packet.getAttribute("id"));
			if (ss!=null) {
				String message = packet.getChildValue("body");
				
				if (ss.getAuthenticated()) {
					if (p.get("default_sym_cipher", "AES").equalsIgnoreCase("aes")) {
						AES aes = new AES();
						Key k = new Key(KeySize.K256);
						k.setKey(ss.getCipherKey());
						aes.setKey(k);
						aes.setCipherText(Base64.decode(message));
						message = new String(aes.decrypt());
					}
				} else {
					if (!p.getBoolean("allow_unauthenticated_conversations", false)) return;
				}
				
				if (ss.getWindow()!=null) {
					// Update user name and window title, to allow remote users to change nicknames
					ss.getUser().setName(packet.getFrom());
					ss.getWindow().updateUser(packet.getFrom());
					
					ss.getWindow().updateMessage(packet.getFrom(),message);
				}	
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (DataFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
