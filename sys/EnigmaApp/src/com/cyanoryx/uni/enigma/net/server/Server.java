package com.cyanoryx.uni.enigma.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

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
	
	PacketQueue pq = new PacketQueue();

	private ServerSocket ss;
	
	public Server(int port,String server) throws IOException {
		this.port = port;
		
		ss = null;
		
		QueueThread q = new QueueThread(this.pq);
			q.setDaemon(true);
			q.addListener(new OpenStreamHandler(),"stream");
			q.addListener(new CloseStreamHandler(),"/stream");
			q.addListener(new AuthHandler(),"auth");
			q.addListener(new MessageHandler(), "message");
		q.start();
		
		ss = new ServerSocket(this.port);
	}
	
	public static void createClient(String id, String server, String port, User u, Session s) throws IOException {
		ClientThread client_thread = new ClientThread();
		Client		 client		   = new Client(client_thread);
		
		client.setServerName(id);
		client.setServerAddress(server);
		System.out.println("Creating client to "+port);
		client.setPort(port);
		client.setResource("test");
		client.setUser(u);
		
		s.addClient("22", client);
		
		Conversation conv = new Conversation(client);
		client.setWindow(conv);
		
		client_thread.setModel(client);
		client_thread.start();
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
