package com.cyanoryx.uni.enigma.net.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.DataFormatException;

import com.cyanoryx.uni.enigma.gui.Conversation;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.StatusListener;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketQueue;
import com.cyanoryx.uni.enigma.net.protocol.xml.ProcessThread;

public class Client {
	private Session session;
	
	private PacketQueue packetQueue;
	
	private static final String version = "Enigma Protocol v0.1";
	
	private String server_name,
				   server_address,
				   server_port,
				   local_port,
				   session_id;
	
	private User   user;
	
	private Conversation window;
	
	private Writer writer;
	
	public Client() {}

	public int getSessionStatus() { return getSession().getStatus(); }
	public void addStatusListener(StatusListener listener){
		getSession().addStatusListener(listener);
	}
	public void removeStatusListener(StatusListener listener){
		getSession().removeStatusListener(listener);
	}

	public void connect() throws IOException {
		// Process incoming messages 
		(new ProcessThread(getSession(),packetQueue)).start();
		
		System.out.println("Client created for "+server_address+":"+server_port);
		
		Writer out = writer;
		out.write("<stream ");
		out.write(" to='server_name");
		out.write("' from='"+ user);
		out.write("' id='"+session_id);
		out.write("' return-port='" + local_port);
		out.write("' xmlns='enigma:client'>");
		out.flush();
		
		System.out.println("Opening stream with "+server_address+":"+server_port);
	}
	public void disconnect() throws IOException { getSession().closeStream(); }

	public void sendMessage(String recipient,String subject,String thread,String type,String body)
			throws IOException {
		Packet packet = new Packet("message");

		if (recipient != null) packet.setTo(recipient);
		packet.setID(this.session_id);
		if (type != null) packet.setType(type);
		if (thread != null) packet.getChildren().add(new Packet("thread",thread));
		if (body != null) packet.getChildren().add(new Packet("body",body));
		
		packet.writeXML(writer);
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
		p.writeXML(writer);
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
	public void setUser(User usr) {user = usr;}
	
	public Conversation getWindow() { return this.window; }
	public void setWindow(Conversation window) { this.window=window; }
	
	public String getLocalPort() { return this.local_port; }
	public void setLocalPort(String port) {this.local_port=port;}

	public String getSessionID() { return this.session_id; }
	public void setSessionID(String id) { this.session_id=id; System.out.println("Setting session ID to "+id); }
	
	public Writer getWriter() { return this.writer; }
	public void setWriter(Writer writer) { this.writer=writer; }

	public Session getSession() { return session; }

	public void setSession(Session session) { this.session = session; }
}
