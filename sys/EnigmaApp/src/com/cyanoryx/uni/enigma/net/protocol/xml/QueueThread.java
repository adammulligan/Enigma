package com.cyanoryx.uni.enigma.net.protocol.xml;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Maintains the packet handler listeners, and forwards
 * packets to the appropriate handler.
 * 
 * @author adammulligan
 *
 */
public class QueueThread extends Thread {
  PacketQueue packetQueue;
  HashMap<PacketListener, String> packetListeners = new HashMap<PacketListener, String>();
  
  public QueueThread(PacketQueue queue) {
    packetQueue = queue;
  }

  /**
   * Example: addListener(new AuthHandler(), "auth");
   * 
   * @param listener - The listener object to handle the given packet
   * @param element - The root element name of the packet to be handled by listener
   */
  public void addListener(PacketListener listener, String element){
    packetListeners.put(listener,element);
  }

  public void run(){
    for (Packet packet=packetQueue.pull();packet!=null;packet=packetQueue.pull()) {
      try {
        // Element name to look for.
        // Matches name of listener
        String match = packet.getElement();

        synchronized(packetListeners){
          Iterator<PacketListener> iter = packetListeners.keySet().iterator();
          // Loop through the current set of listeners
          while (iter.hasNext()){
            PacketListener listener = iter.next();
            // Get the name of the tag to match
            String listenerString = packetListeners.get(listener);
            // If the packet's element matches the element for this listener...
            if (listenerString.equals(match)){
              listener.notify(packet); // ..send packet to handlers
            } 
          } 
        } 
      } catch (Exception e){
        e.printStackTrace();
      }
    } 
  } 
} 
