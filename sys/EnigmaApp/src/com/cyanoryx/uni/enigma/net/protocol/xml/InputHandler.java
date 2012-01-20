package com.cyanoryx.uni.enigma.net.protocol.xml;

import java.io.IOException;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.xerces.parsers.SAXParser;

import com.cyanoryx.uni.enigma.net.protocol.Session;

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

    public void startElement(String namespaceURI,String localName,String qName,Attributes atts) throws SAXException {
    	switch (depth++){
    		case 0:
    			if (qName.equals("stream")){
    				Packet openPacket = new Packet(null,qName,namespaceURI,atts);
    				openPacket.setSession(session);
    				packet_queue.push(openPacket);
    				return;
    			}
    			throw new SAXException("Root element must be <stream>");
    		case 1:
    			packet = new Packet(null,qName,namespaceURI,atts);
    			packet.setSession(session);
    			break;
    		default:
    			Packet child = new Packet(packet,qName,namespaceURI,atts);
    			packet = child;
    	}
    }

    public void characters(char[] ch,int start,int length) throws SAXException {
    	if (depth > 1){
    		packet.getChildren().add(new String(ch,start,length));
    	}
    }

    public void endElement(java.lang.String uri,java.lang.String localName,java.lang.String qName) throws SAXException {
    	switch(--depth){
    		case 0:   // We're back at the end of the root
    			Packet closePacket = new Packet("/stream");
    			closePacket.setSession(session);
    			packet_queue.push(closePacket);
    			break;
    		case 1:   // The Packet is finished
    			packet_queue.push(packet);
    			break;
    		default:  // Move back up the tree
    			packet = packet.getParent();
    	}
    }
}

