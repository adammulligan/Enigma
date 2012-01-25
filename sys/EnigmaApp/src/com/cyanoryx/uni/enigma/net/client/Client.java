package com.cyanoryx.uni.enigma.net.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.zip.DataFormatException;

import com.cyanoryx.uni.enigma.gui.Conversation;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.StatusListener;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketQueue;
import com.cyanoryx.uni.enigma.net.protocol.xml.ProcessThread;

public class Client {
	Session session, remote_session;
	PacketQueue packetQueue;
	private static final String version = "Enigma Protocol v0.1";
	private String server_name,
				   server_address,
				   server_port,
				   resource;
	private User   user;
	
	private Conversation window;
	
	public Client(ClientThread qThread) {
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
		session = new Session();
		// Create a socket
		session.setSocket(new Socket(server_address,Integer.parseInt(server_port)));
		session.setStatus(Session.CONNECTED);
		
		System.out.println("Client created for "+server_address+":"+server_port);

		// Process incoming messages
		(new ProcessThread(session,packetQueue)).start();
		
		Writer out = session.getWriter();
		out.write("<stream to='");
		out.write(server_name);
		out.write("' from='");
		out.write(user + "@" + server_name);
		out.write("' return-port='" + session.getLocalPort());
		out.write("' xmlns='enigma:client'>");
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

	public User getUser() {return user;}
	public void setUser(User usr) {user = usr; }

	public String getResource() {return resource;}
	public void setResource(String res) {resource = res;}
	
	public Conversation getWindow() {return this.window;}
	public void setWindow(Conversation window) {this.window=window;}
	
	public Session getRemoteSession() {return this.remote_session;}
	public void setRemoteSession(Session s){this.remote_session=s;}
}
