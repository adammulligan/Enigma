package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.DataFormatException;

import com.cyanoryx.uni.enigma.gui.Conversation;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketQueue;
import com.cyanoryx.uni.enigma.net.protocol.xml.ProcessThread;
import com.cyanoryx.uni.enigma.net.protocol.xml.QueueThread;

/**
 * Server thread that listens for connections and starts
 * processes to handle them.
 * 
 * @author adammulligan
 *
 */
public class Server implements Runnable {
	private int port;
	
	PacketQueue pq = new PacketQueue();

	private ServerSocket ss;
	private SessionIndex index; // Global session index for this server
	
	public Server(int port,String server) throws IOException {
		this.port   = port;
		this.index  = new SessionIndex();
		
		QueueThread q = new QueueThread(this.pq);
			q.setDaemon(true);
			q.addListener(new OpenStreamHandler(index),"stream");
			q.addListener(new CloseStreamHandler(index),"/stream");
			q.addListener(new AuthHandler(index),"auth");
			q.addListener(new MessageHandler(index), "message");
		q.start();
		
		ss = new ServerSocket(this.port);
	}
	
	/**
	 * Returns the session index for this server instance.
	 * 
	 * @return
	 */
	public SessionIndex getSessionIndex() {
		return this.index;
	}
	
	/**
	 * Wrapper to create a session with attached conversation window.
	 * 
	 * Used to create connections from the server to another Enigma server.
	 * 
	 * @param server - Remote server address
	 * @param port - Remote server port
	 * @param local_port - Local server port
	 * @param u - User 
	 * @param id - Session ID
	 * @return - Session for the new connection
	 * @throws IOException
	 * @throws DataFormatException 
	 */
	public static Session createClient(String server, String port, String local_port, User u, String id)
			throws IOException, DataFormatException {
		Session session	= new Session(new Socket(server,Integer.parseInt(port)));
		
		session.setStatus(Session.CONNECTED);
		session.setServerName(server);
		session.setServerPort(port);
		session.setLocalPort(local_port);
		session.setID(id);
		session.setUser(u);

		System.out.println("Creating client to "+server+":"+port+"...");
		
		Conversation conv = new Conversation(session);
		session.setWindow(conv);
		
		session.connect();
		
		return session;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket clientSock = ss.accept();
				Session s = new Session(clientSock);
				
				s.setLocalPort(this.port+"");
				
				System.out.println("Client connected ("+clientSock.getInetAddress()+":"+clientSock.getPort()+")");
				
				ProcessThread p = new ProcessThread(s,this.pq);
				p.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
