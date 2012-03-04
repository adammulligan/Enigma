package com.cyanoryx.uni.enigma.net.protocol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.prefs.Preferences;
import java.util.zip.DataFormatException;

import com.cyanoryx.uni.common.Base64;
import com.cyanoryx.uni.crypto.aes.AES;
import com.cyanoryx.uni.crypto.aes.Key;
import com.cyanoryx.uni.crypto.aes.KeySize;
import com.cyanoryx.uni.crypto.aes.Mode;
import com.cyanoryx.uni.crypto.cert.KeyAlgorithm;
import com.cyanoryx.uni.crypto.rsa.PublicKey;
import com.cyanoryx.uni.enigma.gui.Conversation;
import com.cyanoryx.uni.enigma.net.io.XercesReader;
import com.cyanoryx.uni.enigma.net.protocol.xml.Packet;
import com.cyanoryx.uni.enigma.utils.AppPrefs;

/**
 * Session represents a connection from the server to another Enigma server.
 * Handles all communication to the remote server, and stores information regarding 
 * the remote server.
 * 
 * @author adammulligan
 *
 */
public class Session {
	String ID;
	Preferences prefs;
	
	public Session(Socket s) {
		this.setSocket(s);
		this.prefs = new AppPrefs().getPrefs();
	}
	public Session() {
		this.setStatus(Session.DISCONNECTED);
		this.prefs = new AppPrefs().getPrefs();
	}
	
	Socket socket;
	public void setSocket(Socket s) {
		this.socket = s;
		this.in = null;
		this.out = null;
		this.setStatus(CONNECTED);
	}
	public Socket getSocket() { return this.socket; }
	
	private String server_name;
	public String getServerName() { return server_name; }
	public void setServerName(String server_name) { this.server_name = server_name; }
	
	private String server_port;
	public String getServerPort() { return server_port; }
	public void setServerPort(String server_port) { this.server_port = server_port; }
	
	private String local_port;
	public void setLocalPort(String port) { this.local_port = port; }
	public String getLocalPort() { return this.local_port; }
	
	User user;
	public User getUser() { return this.user; }
	public void setUser(User u) { this.user = u; }
	
	public String getID() { return this.ID; }
	public void setID(String ID) { this.ID = ID; }
	
	boolean authenticated = true;
	public void setAuthenticated(boolean a) { this.authenticated = a; }
	
	/**
	 * Returns whether or not the session requires authentication, as opposed to
	 * getStatus()==AUTHENTICATED, which represents whether or not this session is
	 * authenticated.
	 * 
	 * @return
	 */
	public boolean getAuthenticated() {
		return this.authenticated;
	}
	
	public static final int DISCONNECTED  = 1;
	public static final int CONNECTED     = 2;
	public static final int STREAMING     = 3;
	public static final int AUTHENTICATED = 4;

	int status;
	public int getStatus() { return status;  }
	public synchronized void setStatus(int newStatus){ status = newStatus; }

	public void closeStream() throws IOException {
	    getWriter().write("</stream>");
	    out.flush();
	    setStatus(CONNECTED); // Still connected, but not streaming
	}
	
	/**
	 * Sends an opening <stream> to the remote server.
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException {
		Writer out = getWriter();
		out.write("<stream ");
		out.write("  to='"+server_name);
		out.write("' from='"+ prefs.get("local_user_name", "Kim"));
		out.write("' id='"+ getID());
		out.write("' return-port='"+ getLocalPort());
		out.write("' xmlns='enigma:client'>");
		out.flush();
		
		System.out.println("Opening stream with "+getServerName()+":"+server_port);
	}

	public void disconnect() throws IOException{
	    this.socket.close();
	    setStatus(DISCONNECTED);
	}
	
	Writer out;
	/**
	 * Returns the output buffer to remote server.
	 * 
	 * @return
	 * @throws IOException
	 */
	public Writer getWriter() throws IOException {
		if (out == null) out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
	    
	    return out;
	}
	
	Reader in;
	public Reader getReader() throws IOException {
	    if (in == null) in = new XercesReader(this.socket.getInputStream());
	    
	    return in;
	}
	
	/**
	 * Sends a message in the format:
	 * 	 <message id="" [from="" to=""]>[<thread></thread><body></body>]</message>
	 * 
	 * @param recipient
	 * @param type
	 * @param body
	 * @throws IOException
	 * @throws DataFormatException 
	 */
	public void sendMessage(String recipient,String type,String body)
			throws IOException, DataFormatException {
		String msg=body;
		
		if (this.getAuthenticated() && this.getStatus()==Session.AUTHENTICATED) {
			byte[] key = this.getCipherKey();
			
			if (this.getCipherType()==CipherAlgorithm.AES) {
				AES aes = new AES();
				
				Key k = new Key(KeySize.K256);
				k.setKey(key);
				aes.setKey(k);
				
				aes.setMode(Mode.CBC);
				aes.setPlainText(body.getBytes());
				
				msg=Base64.encodeBytes(aes.encrypt());
			}
		}
		
		Packet packet = new Packet("message");

		if (recipient != null) packet.setTo(recipient);
		if (type != null) packet.setType(type);
		
		packet.setID(getID());
		packet.setFrom(prefs.get("local_user_name", "Kim"));
		
		if (body != null) packet.getChildren().add(new Packet("body",msg));
		
		packet.writeXML(getWriter());
	}
	
	/**
	 * Send an error packet in the format:
	 * 	 <packet id="" to="" [stage=""] type="error"><error type="errnum">error msg</error></packet>
	 * 
	 * @param packet
	 * @param stage
	 * @param error
	 * @param errnum
	 * @param recipient
	 * @param id
	 * @throws DataFormatException
	 * @throws IOException
	 */
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
		
		p.writeXML(getWriter());
	}
	
	/**
	 * Sends an auth message in the format:
	 * 	 <auth stage="stage" type="type" id="id">body</auth>
	 * 
	 * @param type
	 * @param stage
	 * @param body
	 * @param id
	 * @throws DataFormatException
	 * @throws IOException
	 */
	public void sendAuth(String type, String stage, String body, String id) throws DataFormatException, IOException {
		if (type  == null ||
			stage == null ||
			body  == null ||
			id    == null) {
			throw new DataFormatException("");
		}

		Packet p = new Packet("auth");

		p.setID(id);
		p.setType(type);
		p.setAttribute("stage",stage);
		p.getChildren().add(new Packet(type,body));

		p.writeXML(getWriter());
	}
	
	private Conversation window;
	public Conversation getWindow() { return this.window; }
	public void setWindow(Conversation window) { this.window=window; }
	
	private PublicKey public_key;
	public void setPublicKey(PublicKey publicKey) { this.public_key=publicKey; }
	public PublicKey getPublicKey() { return this.public_key; }
	
	private byte[] cipher_key;
	public void setCipherKey(byte[] key) { this.cipher_key=key;	}
	public byte[] getCipherKey() { return this.cipher_key; }
	
	CipherAlgorithm cipher_type;
	public void setCipherType(CipherAlgorithm algo) { this.cipher_type=algo; }
	public CipherAlgorithm getCipherType() { return this.cipher_type; }
	
	KeyAlgorithm agreement_type;
	public void setAgreementType(KeyAlgorithm algo) { this.agreement_type=algo; }
	public KeyAlgorithm getAgreementType() { return this.agreement_type; }
}
