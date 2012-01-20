package com.cyanoryx.uni.enigma.net.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.zip.DataFormatException;

import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.StatusListener;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketQueue;
import com.cyanoryx.uni.enigma.net.protocol.xml.ProcessThread;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class Client {
	Session session = new Session();
	PacketQueue packetQueue;
	private static final String version = "Enigma Protocol v0.1";
	private String server_name,
				   server_address,
				   server_port,
				   user,
				   resource;
	
	Client(ClientThread qThread) {
		packetQueue = qThread.getQueue();
		qThread.addListener(new OpenStreamHandler(),"stream");
		qThread.addListener(new CloseStreamHandler(),"/stream");
		qThread.addListener(new MessageHandler(),"message");
	}

	public int getSessionStatus() { return session.getStatus(); }
	public void addStatusListener(StatusListener listener){
		session.addStatusListener(listener);
	}
	public void removeStatusListener(StatusListener listener){
		session.removeStatusListener(listener);
	}

	public void connect() throws IOException {

		// Create a socket
		session.setSocket(new Socket(server_address,Integer.parseInt(server_port)));
		session.setStatus(Session.CONNECTED);

		// Process incoming messages
		(new ProcessThread(session,packetQueue)).start();

		Writer out = session.getWriter();
		out.write("<?xml version='1.0' encoding='UTF-8' ?><stream to='");
		out.write(server_name);
		out.write("' from='");
		out.write(user + "@" + server_name);
		out.write("' xmlns='enigma:client' xmlns:stream='http://cyanoryx.com'>");
		out.flush();
	}
	public void disconnect() throws IOException { session.closeStream(); }

	public void sendMessage(String recipient,String subject,String thread,String type,String id,String body)
			throws IOException {
		Packet packet = new Packet("message");

		if (recipient != null) packet.setTo(recipient);
		if (id != null) packet.setID(id);
		if (type != null) packet.setType(type);
		if (thread != null) packet.getChildren().add(new Packet("thread",thread));
		if (body != null) packet.getChildren().add(new Packet("body",body));
		
		packet.writeXML(session.getWriter());
	}
	
	public void sendError(String packet, String stage, String error, String errnum, String recipient, String id) throws DataFormatException, IOException {
		if (packet    == null ||
			error     == null ||
			recipient == null ||
			errnum    == null ||
			id  	  == null) {
			throw new DataFormatException("");
		}
		
		Packet p = new Packet(packet);
		
		p.setID(id);
		p.setTo(recipient);
		p.setType("error");
		if (stage!=null) p.setAttribute("stage",stage);
		
		Packet e = new Packet("error",error);
		e.setType(errnum);
		
		p.getChildren().add(e);
		
		p.writeXML(new BufferedWriter(new OutputStreamWriter(System.out)));
		p.writeXML(session.getWriter());
	}
	
	public void sendAgreement() {
		
	}
	
	public static enum AgreementType {RSA,DH};
	private AgreementType agreementType;
	public AgreementType getAgreementType() { return this.agreementType; }
	public void setAgreementType(AgreementType type) { this.agreementType = type; }
	
	public final String getVersion(){ return version; }

	public String getServerName() {return server_name;}
	public void setServerName(String name) {server_name = name;}

	public String getServerAddress() {return server_address;}
	public void setServerAddress(String addr) {server_address = addr;}

	public String getPort() {return server_port;}
	public void setPort(String port) {server_port = port;}

	public String getUser() {return user;}
	public void setUser(String usr) {user = usr; }

	public String getResource() {return resource;}
	public void setResource(String res) {resource = res;}
}
