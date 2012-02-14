package com.cyanoryx.uni.enigma.net.protocol.xml;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.xerces.parsers.SAXParser;

import com.cyanoryx.uni.enigma.net.protocol.Session;

/**
 * Handles SAX events and builds packets from them.
 * 
 * @author adammulligan
 *
 */
public class InputHandler extends DefaultHandler {
    PacketQueue packet_queue;
    Session session;
    
    Packet packet;
    int depth = 0;
  
    public InputHandler(PacketQueue packetQueue) {
        packet_queue = packetQueue;
    }

    public void process(Session session) throws IOException, SAXException {
    	SAXParser parser = new SAXParser();

    	parser.setContentHandler(this);
    	parser.setReaderFactory(new StreamingCharFactory());

    	this.session = session;

    	parser.parse(new InputSource(session.getReader()));
    }
    
    /**
     * Handles starting elements
     * 
     */
    @Override
    public void startElement(String namespace,String localName,String name,Attributes attributes)
    		throws SAXException {
    	switch (depth++){
    		case 0: // Root element
    			if (name.equals("stream")){
    				Packet packet = new Packet(null,name,namespace,attributes);
    				packet.setSession(session);
    				packet_queue.push(packet);
    				return;
    			}
    			throw new SAXException("Root element must be <stream>");
    		case 1: // Message elements
    			packet = new Packet(null,name,namespace,attributes);
    			packet.setSession(session);
    			break;
    		default: // Any child elements
    			Packet child = new Packet(packet,name,namespace,attributes);
    			packet = child;
    	}
    }

    /**
     * Adds a string (char array) as a child element to the current packet.
     *  
     * Ideally this would use a string, but for some unknown reason,
     * DefaultHandler uses char[].
     */
    @Override
    public void characters(char[] c,int start,int length) throws SAXException {
    	if (depth > 1) packet.getChildren().add(new String(c,start,length));
    }

    /**
     * Handles end elements
     * 
     */
    @Override
    public void endElement(String uri,String localName,String name) throws SAXException {
    	switch(--depth){
    		case 0: // End of the stream
    			Packet c_packet = new Packet("/stream");
    			c_packet.setSession(session);
    			packet_queue.push(c_packet);
    			break;
    		case 1: // Put the completed packet on the packet queue
    			packet_queue.push(packet);
    			break;
    		default:  // Parent still being constructed; traverse back up the tree
    			packet = packet.getParent();
    	}
    }
}

