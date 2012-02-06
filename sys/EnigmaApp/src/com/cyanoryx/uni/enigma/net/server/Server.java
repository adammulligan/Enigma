package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.cyanoryx.uni.enigma.gui.Conversation;
import com.cyanoryx.uni.enigma.net.client.Client;
import com.cyanoryx.uni.enigma.net.client.ClientThread;
import com.cyanoryx.uni.enigma.net.protocol.Session;
import com.cyanoryx.uni.enigma.net.protocol.User;
import com.cyanoryx.uni.enigma.net.protocol.xml.PacketQueue;
import com.cyanoryx.uni.enigma.net.protocol.xml.ProcessThread;
import com.cyanoryx.uni.enigma.net.protocol.xml.QueueThread;

public class Server implements Runnable {
	private int port;
	//private String server;
	
	PacketQueue pq = new PacketQueue();

	private ServerSocket ss;
	private SessionIndex index;
	
	public Server(int port,String server) throws IOException {
		this.port   = port;
		//this.server = server;
		this.index  = new SessionIndex();
		
		QueueThread q = new QueueThread(this.pq);
			q.setDaemon(true);
			q.addListener(new OpenStreamHandler(index),"stream");
			q.addListener(new CloseStreamHandler(index),"/stream");
			q.addListener(new AuthHandler(),"auth");
			q.addListener(new MessageHandler(index), "message");
		q.start();
		
		ss = new ServerSocket(this.port);
	}
	
	public SessionIndex getSessionIndex() {
		return this.index;
	}
	
	public static Client createClient(String server, String port, String local_port, User u, String id) throws IOException {
		System.out.println("-----------------CLIENT CREATION---------------");
		ClientThread client_thread = new ClientThread();
		Client		 client		   = new Client(client_thread);
		
		client.setServerName(server);
		client.setServerAddress(server);
		client.setPort(port);
		client.setResource("test");
		client.setUser(u);
		
		client.setSessionID(id);
		client.setLocalPort(local_port);

		System.out.println("Creating client to "+server+":"+port+"...");
		
		Conversation conv = new Conversation(client);
		client.setWindow(conv);
		
		client.getSession().addClient(id, client);
		
		client_thread.setModel(client);
		client_thread.start();
		
		return client;
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
