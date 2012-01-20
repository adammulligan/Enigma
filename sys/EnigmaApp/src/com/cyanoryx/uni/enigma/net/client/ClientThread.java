package com.cyanoryx.uni.enigma.net.client;

import java.util.HashMap;
import java.util.Iterator;

import com.cyanoryx.uni.enigma.net.protocol.xml.PacketListener;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketQueue;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ClientThread extends Thread {
	// TODO auth stage
	PacketQueue packetQueue = new PacketQueue();
	public PacketQueue getQueue() { return packetQueue; }

	HashMap<PacketListener, String> packetListeners = new HashMap<PacketListener, String>();

	public boolean addListener(PacketListener listener, String element){
		if (listener == null || element == null){
			return false;
		}
		packetListeners.put(listener,element);
		return true;
	}

	public boolean removeListener(PacketListener listener){
		packetListeners.remove(listener);
		return true;
	}

	public void run(){
	} // run()

	Client model;
	public void setModel(Client newModel){
		model = newModel;
	}

	Packet waitFor(String element, String type){
		for( Packet packet = packetQueue.pull();
				packet != null;
				packet = packetQueue.pull()) {
			notifyHandlers(packet);
			if (packet.getElement().equals(element)){
				if (type != null){
					if (packet.getType().equals(type)){
						return packet;
					}
				} else {
					return packet;
				}
			}
		}
		return null;
	}

	void notifyHandlers(Packet packet){
		try {
			Packet child;
			String matchString;
			if (packet.getElement().equals("iq")){
				child = packet.getFirstChild("query");
				if (child == null){
					matchString = "iq";
				} else {
					matchString = child.getNamespace();
				}
			} else {
				matchString = packet.getElement();
			}

			synchronized(packetListeners){
				Iterator<PacketListener> iter = packetListeners.keySet().iterator();
				while (iter.hasNext()){
					PacketListener listener = (PacketListener)iter.next();
					String listenerString = (String)packetListeners.get(listener);
					if (listenerString.equals(matchString)){
						listener.notify(packet);
					} // if
				} // while
			} // sync
		} catch (Exception ex){
			ex.printStackTrace();
			// Log.error("TestThread: ", ex); // Soldier on - no matter what
		}
	}
}
