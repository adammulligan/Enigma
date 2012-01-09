package com.cyanoryx.uni.enigma.net.protocol.xml;

import java.util.HashMap;
import java.util.Iterator;

public class QueueThread extends Thread {
	PacketQueue packetQueue;
	HashMap<PacketListener, String> packetListeners = new HashMap<PacketListener, String>();
	
	public QueueThread(PacketQueue queue) {
		packetQueue = queue;
	}

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
	    for( Packet packet = packetQueue.pull();
		packet != null;
	    packet = packetQueue.pull()) {

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
	    			PacketListener listener = iter.next();
	    			String listenerString = packetListeners.get(listener);
	    			if (listenerString.equals(matchString)){
	    				listener.notify(packet);
	    			} 
	    		} 
	    	} 
	    } catch (Exception ex){}
	    } 
	} 
} 
